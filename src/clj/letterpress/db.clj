(ns letterpress.db)

(defn save-game [game]
  (println "TODO: Save game " game))

(defn get-game [id]
  (println "TODO: Get game " id)
  {:id id
   :tiles (list \V \V \S \Ä \A \A \R \S \R \M \T \N \O \P \G \U)
   :player-one "Jorma"
   :score [0 0]
   :turn :player-one
   :words []
   :size 4})
