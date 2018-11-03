(ns letterpress.views.index
  (:require [reagent.core :as r]
            [ajax.core :refer [POST]]
            [secretary.core :as secretary]
            [accountant.core :as accountant]
            [letterpress.state :refer [game current-player]]
            [cljs.tools.reader.edn :as edn]))

(def default-size 5)

(defn- start-game [data]
  (let [new-game (edn/read-string data)]
    (reset! game new-game)
    (println @game)
    (accountant/navigate! (str "/game/" (:_id new-game)))))

(defn- save-player-name! [player-name]
  (if (empty? player-name)
    (println "Player name missing!")
    (reset! current-player player-name)))


(defn- create-game! [player-name]
  (if (empty? player-name)
    (println "Player name missing!") ; TODO
    (do
      (reset! current-player player-name)
      (POST "/api/game"
            {:params {:player-name player-name
                      :size default-size}
             :format :raw
             :handler start-game}))))

(defn index-page []
  (let [player-name (r/atom @current-player)]
    (fn []
      [:div {:class "container"
             :style {:width "300px"}}
       [:h1 "Letterpress.fi"]
       [:p "Suomenkielinen "
        [:a {:href "http://www.letterpressapp.com/"}
         "Letterpress"]
        "-kopio"]
       [:p
        [:a {:href "/list"}
         "Näytä"]
        (str " omat pelisi")]
       [:div
        "Nimi: "
        (if (nil? @current-player)
          [:input {:type "text"
                   :value @player-name
                   :on-change #(reset! player-name (-> % .-target .-value))}]
          @current-player)]
       [:div
        [:input {:type "submit"
                 :value "Käynnistä uusi peli"
                 :on-click #(create-game! @player-name)}]
        (when (nil? @current-player)
          [:input {:type "submit"
                   :value "Tallenna nimi"
                   :on-click #(save-player-name! @player-name)}])]])))
