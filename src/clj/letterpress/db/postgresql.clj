(ns letterpress.db.postgresql
  (:require [clojure.java.jdbc :as jdbc]))

(defn- get-connection [host user password dbname]
  {:dbtype "postgresql"
   :dbname dbname
   :host "localhost"
   :user (str user "@" host)
   :password password})
