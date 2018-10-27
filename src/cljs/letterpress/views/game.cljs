(ns letterpress.views.game
  (:require [letterpress.state :refer [game current-player selected-tiles]]
            [letterpress.views.tile :as tile]
            [reagent.core :as r]
            [ajax.core :refer [GET POST]]
            [cljs.tools.reader.edn :as edn]))

(defn- update-game-state [data]
  (reset! game (edn/read-string data)))

(defn- fetch-game [id]
  (GET (str "/api/game/" id)
       {:handler update-game-state}))

(defn- render-player [num name score current-turn]
  [:li {:class (str "player" num)}
   [:span {:class "icon"
           :title name}
    "☺"]
   [:br]
   [:span {:class "playerName"}
    name]
   [:br]
   [:span {:class "score"}
    score]
   (when current-turn
     [:span
      [:br]
      [:span {:class "arrow"} "▼"]])])

(defn- turn [game]
  (-> game :turn keyword game))

(defn- join-game! [id player-name]
  (if (empty? player-name)
    (println "Player name missing!") ;TODO
    (do
      (reset! current-player player-name)
      (POST (str "/api/game/" id "/join")
            {:params {:player-name player-name}
             :format :raw
             :handler update-game-state}))))

(defn- join-game-form [game current-player]
  (let [player-name (r/atom current-player)]
    (fn []
      [:li
       (if (nil? current-player)
          [:label "Nimi: "
           [:input {:value @player-name
                    :class "nameinput"
                    :on-change #(reset! player-name (-> % .-target .-value))}]]
          [:label "Nimi: " current-player])
       [:br]
       [:input {:type "submit"
                :value "Liity peliin"
                :class "namesubmit"
                :on-click #(join-game! (:_id game) @player-name)}]])))

(defn- render-players [game current-player]
  (let [ended (-> game :winner nil? not)]
    [:div {:class "playersContainer container"}
     [:input {:type "submit"
              :value "Tyhjennä"
              :class "clear topbuttons"}]
     [:input (conj {:type "submit"
                    :value "Pass"
                    :class "pass topbuttons"}
                   (when (or ended (not= current-player (turn game)))
                     {:disabled "disabled"}))]
     [:ul {:class "players"}
      (render-player 1
                     (:player-one game)
                     (-> game :score first)
                     (and (= (:turn game) "player-one") (not ended)))
      (if (-> game :player-two nil?)
        (if (= (:player-one game) current-player)
          [:li
           [:span {:class "waiting"}
            "Odotetaan toista pelaajaa."]]
          [join-game-form game current-player])
        (render-player 2
                       (:player-two game)
                       (-> game :score second)
                       (and (= (:turn game) "player-two") (not ended))))]
     [:input (conj {:type "submit"
                    :value "Lähetä"
                    :class "submit topbuttons"}
                   (when (or ended (not= current-player (turn game)))
                     {:disabled "disabled"}))]]))

(defn- render-game-over [game]
  (let [winner (:winner game)
        ended (-> winner nil? not)]
    (when ended
      [:div
       {:class "game-over container"}
       [:p {:class "ended"}
        "Peli on päättynyt. " winner " voitti!"]
       [:p {:class "ended"}
        [:a {:href "/"}
         "Pelaa uudestaan."]]])))

(defn game-page [id]
  (if (nil? @game)
    (do (fetch-game id)
      [:div {:class "container"
             :width "300px"}
       "Haetaan peliä..."])
    (let [size (:size @game)]
      [:div
       (render-game-over @game)
       (render-players @game @current-player)
       [:ul {:id "selected"
             :class "container"
             :style {:width (str (* 80 (count @selected-tiles)) "px")}}
        (for [tile @selected-tiles]
          ^{:key (:id tile)} [tile/selected-tile tile])]
       [:ul {:id "board"
             :class "container"
             :style {:width (str (* 80 size) "px")}}
        (for [tile (:tiles @game)]
          ^{:key (:id tile)} [tile/tile tile size])]
       [:ul {:class "playedWords container"
             :style {:top (str (+ 120 (* size 90)) "px")}}
        (for [word (:played-words @game)]
          ^{:key (:word word)}
          [:li {:class (str "player" (:turn word))} (:word word)])]])))
