(ns letterpress.game
  (:require [letterpress.db :as db]
            [clojure.java.io :as io]))

(def words
  (-> "fi.txt"
      io/resource
      slurp
      clojure.string/split-lines))

(def alphabet
  ['(\I 0.11607)
   '(\A 0.23011)
   '(\T 0.31651)
   '(\S 0.39098)
   '(\E 0.45836)
   '(\U 0.52488)
   '(\K 0.59058)
   '(\N 0.65494)
   '(\L 0.71334)
   '(\O 0.76748)
   '(\R 0.80699)
   '(\Ä 0.83889)
   '(\P 0.86920)
   '(\M 0.89892)
   '(\V 0.92313)
   '(\H 0.94481)
   '(\Y 0.96644)
   '(\J 0.97941)
   '(\D 0.98683)
   '(\Ö 0.99417)
   '(\G 0.99682)
   '(\F 0.99847)
   '(\B 1.00000)])

(defn- generate-id []
  (str (. java.util.UUID randomUUID)))

(defn- get-character [p]
  (->> alphabet
       (filter #(> (second %) p))
       first
       first))

(defn- letter-to-tile [acc letter]
  {:index (-> acc :index inc)
   :tiles (conj (:tiles acc)
                {:letter letter
                 :id (:index acc)})})

(defn- generate-tiles [size]
  (let [tile-letters (take (* size size)
                           (repeatedly #(get-character (rand))))
        tiles (reduce letter-to-tile
                      {:index 0 :tiles []}
                      tile-letters)]
    (:tiles tiles)))

(defn create-game
  "Creates a new game, saves it in the database and
returns the game object."
  [player-name size]
  (let [id (generate-id)
        size-num (clojure.edn/read-string size)
        game {:_id id
              :tiles (generate-tiles size-num)
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

(defn- update-tiles-fn
  [owner]
  (fn [old-tiles tile]
    (replace {tile (assoc tile :owner owner)} old-tiles)))

(defn- new-score
  [old-score turn tiles]
  old-score) ; TODO

(defn submit-word
  [id player-name tiles]
  (let [word (->> tiles (map :letter) (reduce str) clojure.string/lower-case)
        game (db/get-game id)]
    (when (valid-submit? word game tiles player-name)
      (db/update-game
        id
        (reduce (update-tiles-fn (:turn game)) (:tiles game) tiles)
        (new-score (:score game) (:turn game) tiles)
        (if (= (:turn game) "player-one")
          "player-two"
          "player-one")
        (conj (:played-words game) {:word word :turn (:turn game)}))
      (db/get-game id))))
