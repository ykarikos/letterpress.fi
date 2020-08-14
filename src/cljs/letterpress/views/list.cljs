(ns letterpress.views.list
  (:require [letterpress.state :refer [current-player]]
            [reagent.core :as r]
            [ajax.core :refer [GET]]
            [cljs.tools.reader.edn :as edn]))

(def game-list (r/atom nil))

(defn- fetch-list! [player-name]
  (GET (str "/api/list")
       {:params {:player-name player-name}
        :handler #(reset! game-list (edn/read-string %))}))

(defn- render-game [game]
  [:a {:href (str "/game/" (:_id game))}
   (:player-one game) "-" (:player-two game)])

(defn list-page []
  [:div {:class "container"
         :style {:width "300px"}}
   [:h1 "Pelilista"]
   [:div [:a {:href "/"} "Siirry etusivulle"]]
   (if (nil? @current-player)
     [:div "nimi puuttuu"]
     (if (nil? @game-list)
       (do (fetch-list! @current-player)
         [:div "Ladataan listaa..."])
       (if (= (count @game-list) 0)
         [:div "Ei tallennettuja pelejä."]
         [:ul
          (for [game @game-list]
            ^{:key (:_id game)} [:li (render-game game)])])))])
