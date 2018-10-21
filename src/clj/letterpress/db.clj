(ns letterpress.db
  (:require [monger.collection :as mc]
            [monger.core :as mg]
            [config.core :refer [env]]))

(def db-uri
  (or (:db-uri env) "mongodb://localhost/letterpress"))

(def connection
  (mg/connect-via-uri db-uri))

(def db (:db connection))

(def game-db "games")

(defn save-game [game]
  (mc/insert db game-db game))

(defn get-game [id]
  (mc/find-one-as-map db game-db {:_id id}))