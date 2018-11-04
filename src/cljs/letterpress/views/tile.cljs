(ns letterpress.views.tile
  (:require [letterpress.state :refer [game current-player selected-tiles]]
            [ajax.core :refer [POST]]
            [cljs.tools.reader.edn :as edn]))

(defn- update-game-state [data]
  (swap! game conj (edn/read-string data)))

(defn- selected-tile-click! [tile]
  (reset! selected-tiles (filterv #(not= (:id tile) (:id %)) @selected-tiles))
  (POST (str "/api/game/" (:_id @game) "/validate")
        {:params {:tiles (str @selected-tiles)
                  :player-name @current-player}
         :format :raw
         :handler update-game-state}))

(defn- tile-click! [tile]
  (swap! selected-tiles conj tile)
  (POST (str "/api/game/" (:_id @game) "/validate")
        {:params {:tiles (str @selected-tiles)
                  :player-name @current-player}
         :format :raw
         :handler update-game-state}))

(defn- hidden-tile? [tile]
  (some #(= (:id tile) (:id %)) @selected-tiles))

(defn selected-tile [tile]
  [:li
   {:on-click #(selected-tile-click! tile)
    :class (:owner tile)}
   (:letter tile)])

(defn tile [tile size]
  (let [id (:id tile)
        left (* (mod id size) 80)
        top (+ 200 (* (quot id size) 90))]
    [:li
     {:style (conj
              {:left (str left "px")
               :top (str top "px")}
              (when (hidden-tile? tile) {:display "none"}))
      :on-click #(tile-click! tile)
      :class (:owner tile)}
     (:letter tile)]))
