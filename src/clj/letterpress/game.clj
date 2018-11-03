(ns letterpress.game
  (:require [letterpress.db :as db]
            [clojure.java.io :as io]
            [letterpress.tile :as tile]))

(def words
  (-> "fi.txt"
      io/resource
      slurp
      clojure.string/split-lines))

(defn- generate-id []
  (str (. java.util.UUID randomUUID)))

(defn create-game
  "Creates a new game, saves it in the database and
returns the game object."
  [player-name size]
  (let [size-num (clojure.edn/read-string size)
        game {:_id (generate-id)
              :tiles (tile/generate-tiles size-num)
              :player-one player-name
              :score [0 0]
              :turn :player-one
              :played-words []
              :size size-num}]
    (db/save-game game)
    game))

(defn get-game
  [id]
  (db/get-game id))

(defn join-game
  [id player-name]
  (let [result (db/join-game id player-name)]
    (when (= 1 result)
      (db/get-game id))))

(defn- game-contains-tiles?
  [game-tiles tiles]
  (->> tiles
       (map (fn [x] (some #(= x %) game-tiles)))
       (every? identity)))

(defn- is-played-word?
  [word played-words]
  (some #(.startsWith (:word %) word) played-words))

(defn- valid-submit?
  [word game tiles player-name]
  "Are `word` and `tiles` a valid submission for game state `game` and `player-name`?"
  (and (some (partial = word) words)
       (game-contains-tiles? (:tiles game) tiles)
       (-> game :turn keyword game (= player-name))
       (not (is-played-word? word (:played-words game)))))

(defn- turn-flip
  [turn]
  (if (= turn "player-one")
      "player-two"
      "player-one"))

(defn- new-score-fn
  [turn]
  (fn [old-score {owner :owner}]
    (if (or (= owner turn)
            (= owner "player-one-locked")
            (= owner "player-two-locked"))
      old-score
      (if (= owner (turn-flip turn)) ; other player owned the tile
        (if (= turn "player-one")
          [(-> old-score first inc) (-> old-score second dec)]
          [(-> old-score first dec) (-> old-score second inc)])
        (if (= turn "player-one") ; no one owned the tile
          [(-> old-score first inc) (-> old-score second)]
          [(-> old-score first) (-> old-score second inc)])))))

(defn- create-new-game-state
  [word game tiles]
  (let [new-tiles (tile/update-tiles game tiles)
        new-score (reduce (new-score-fn (:turn game)) (:score game) tiles)
        new-played-words (conj (:played-words game) {:word word :turn (:turn game)})
        tile-count (count new-tiles)
        score-sum (apply + new-score)
        winner (when (= score-sum tile-count)
                 {:winner (if (apply > new-score)
                            (:player-one game)
                            (:player-two game))})]
    (conj
      {:tiles new-tiles
       :score new-score
       :turn (turn-flip (:turn game))
       :played-words new-played-words}
      winner)))

(defn submit-word
  [id player-name tiles]
  (let [word (->> tiles (map :letter) (reduce str) clojure.string/lower-case)
        game (db/get-game id)]
    (when (valid-submit? word game tiles player-name)
      (db/update-game
        id
        (create-new-game-state word game tiles))
      (db/get-game id))))
