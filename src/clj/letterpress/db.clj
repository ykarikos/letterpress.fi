(ns letterpress.db
  (:require [monger.collection :as mc]
            [monger.operators :refer [$set $or]]
            [monger.core :as mg]
            [config.core :refer [env]]))

(def db-uri
  (or (:mongodb-uri env) "mongodb://localhost/letterpress"))

(def connection
  (mg/connect-via-uri db-uri))

(def db (:db connection))

(def game-db "games")

(defn save-game [game]
  (mc/insert db game-db game))

(defn get-game [id]
  (mc/find-one-as-map db game-db {:_id id}))

(defn join-game [id player-name]
  (let [result (mc/update-by-id db game-db id {$set {:player-two player-name}})]
    (.getN result)))

(defn update-game [id game]
  (mc/update-by-id db game-db id {$set game}))

(defn list-games [player-name]
  (mc/find-maps db game-db {$or [{:player-one player-name}
                                 {:player-two player-name}]}
                           [:_id :player-one :player-two]))
