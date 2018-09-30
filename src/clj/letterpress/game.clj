(ns letterpress.game
  (:require [letterpress.db :as db]))

(def alphabet
  {\I 0.11607
   \A 0.23011
   \T 0.31651
   \S 0.39098
   \E 0.45836
   \U 0.52488
   \K 0.59058
   \N 0.65494
   \L 0.71334
   \O 0.76748
   \R 0.80699
   \Ä 0.83889
   \P 0.86920
   \M 0.89892
   \V 0.92313
   \H 0.94481
   \Y 0.96644
   \J 0.97941
   \D 0.98683
   \Ö 0.99417
   \G 0.99682
   \F 0.99847
   \B 1.00000})

(def letters (keys alphabet))

(defn- generate-id []
  (str (. java.util.UUID randomUUID)))

(defn- generate-tiles [size]
  (take (* size size) (repeatedly #(rand-nth letters))))

(defn create-game
  "Creates a new game, saves it in the database and
returns the game object."
  [player-name size]
  (let [id (generate-id)
        size-num (clojure.edn/read-string size)
        game {:id id
              :tiles (generate-tiles size-num)
              :player-one player-name
              :score [0 0]
              :turn :player-one
              :words []
              :size size-num}]
    (db/save-game game)
    game))

(defn get-game
  [id]
  (db/get-game id))
