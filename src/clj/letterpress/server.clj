(ns letterpress.server
    (:require [letterpress.handler :refer [app]]
              [config.core :refer [env]]
              [letterpress.db.postgresql :as db]
              [ring.adapter.jetty :refer [run-jetty]])
    (:gen-class))

(defn -main [& args]
  (let [port (or (env :port) 3000)]
    (db/init!)
    (run-jetty app {:port port :join? false})))
