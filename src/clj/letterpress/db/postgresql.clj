(ns letterpress.db.postgresql
  (:require [clojure.java.jdbc :as jdbc]
            [clj-postgresql.core :as pg]
            [clojure.walk :as walk]
            [config.core :refer [env]]
            [clojure.string :as str])
  (:import [java.util UUID]
           [java.net URI]))

(def db-url
  (or (:database-url env)
      "postgres://postgres:secret@localhost:5432/letterpress"))

(def db
  (delay
    (let [db-uri              (URI. db-url)
          [username password] (str/split (or (.getUserInfo db-uri) ":") #":")]
      (pg/pool :user     username
               :password (or password "")
               :port     (.getPort db-uri)
               :dbname   (str/replace-first (.getPath db-uri) "/" "")
               :host     (.getHost db-uri)))))

(def tablename "games")

(defn save-game [{:keys [_id player-one player-two] :as game}]
  (jdbc/insert! @db tablename {:id (UUID/fromString _id)
                               :player_one player-one
                               :player_two player-two
                               :gamedata (dissoc game :id :player-one :player-two)}))

(defn- jdbc-to-game [{:keys [player_one player_two id gamedata]}]
  (-> gamedata
      walk/keywordize-keys
      (assoc-in [:player-one] player_one)
      (assoc-in [:player-two] player_two)
      (assoc-in [:_id] (str id))))

(defn get-game [id]
  (-> (jdbc/query @db [(str "SELECT * FROM " tablename " WHERE id = ?") (UUID/fromString id)])
      first
      jdbc-to-game))

(defn join-game [id player-name]
  (jdbc/update! @db tablename {:player_two player-name} ["id = ?" (UUID/fromString id)])
  (get-game id))

(defn update-game [id game]
  (jdbc/update! @db tablename
                {:gamedata (dissoc game :_id :player-one :player-two)}
                ["id = ?" (UUID/fromString id)])
  (get-game id))

(defn list-games [player-name]
  (let [games (jdbc/query @db [(str "SELECT * FROM " tablename " WHERE player_one = ? OR player_two = ?")
                               player-name
                               player-name])]
    (map jdbc-to-game games)))

(defn init! []
  (jdbc/execute! @db
    (str "CREATE TABLE IF NOT EXISTS " tablename
         " ( id UUID NOT NULL PRIMARY KEY, "
         "player_one VARCHAR(100) NOT NULL, "
         "player_two VARCHAR(100), "
         "gamedata JSONB NOT NULL)"))
  (jdbc/execute! @db
    (str "CREATE INDEX IF NOT EXISTS player_one_idx ON " tablename
         " (player_one)"))
  (jdbc/execute! @db
    (str "CREATE INDEX IF NOT EXISTS player_two_idx ON " tablename
         " (player_two)"))
  (println "Database initialized!"))


