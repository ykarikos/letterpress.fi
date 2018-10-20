(ns letterpress.views.game
  (:require [letterpress.state :refer [game]]
            [ajax.core :refer [GET]]
            [cljs.tools.reader.edn :as edn]))

(defn- update-game-state [data]
  (reset! game (edn/read-string data)))

(defn- fetch-game [id]
  (GET (str "/api/game/" id)
       {:handler update-game-state}))

(defn- render-tile [tile]
 [:span tile])

(defn- render-player [num name score]
  [:li {:class (str "player" num)}
   [:span {:class "icon"
           :title name}
    "☺"]
   [:br]
   [:span {:class "playerName"}
    name]
   [:br]
   [:span {:class "score"}
    score]])

(defn- render-players [game]
  [:ul
   (render-player 1
                  (:player-one game)
                  (-> game :score first))
   (render-player 2
                  (:player-two game)
                  (-> game :score second))])

(defn game-page [id]
  (if (nil? @game)
    (do (fetch-game id) [:div "Fetching the game..."])
    [:div
     (str @game)
     (render-players @game)
     [:div {:id "board"
            :style {:width (str (* 80 (:size @game)) "px")}}
      (for [tile (:tiles @game)]
        ^{:key tile} (render-tile tile))]]))
