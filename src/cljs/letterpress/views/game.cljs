(ns letterpress.views.game
  (:require [letterpress.state :refer [game]]
            [ajax.core :refer [GET]]
            [cljs.tools.reader.edn :as edn]))

(defn- update-game-state [data]
  (reset! game (edn/read-string data)))

(defn- fetch-game [id]
  (GET (str "/api/game/" id)
       {:handler update-game-state}))

(defn- render-tile [tile size]
  (let [id (:id tile)
        left (* (mod id size) 80)
        top (+ 200 (* (quot id size) 90))]
    [:span
     {:style {:left (str left "px")
              :top (str top "px")}
      :class (:owner tile)
      :data-id id}
     (:letter tile)]))

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

(defn- join-game [current-player]
  [:li
   (if (nil? current-player)
      [:label "Nimi: "
       [:input {:id "player-two-input"
                :class "nameinput"}]]
      [:label "Nimi: " current-player])
   [:br]
   [:input {:type "submit"
            :value "Liity peliin"
            :class "namesubmit"}]])

(defn- render-players [game current-player]
  (let [winner (:winner game)
        ended (-> winner nil? not)]
    [:div
     (when ended
       [:div
        [:p {:class "ended"}
         "Peli on päättynyt. " winner " voitti!"]
        [:p {:class "ended"}
         [:a {:href "/"}
          "Pelaa uudestaan."]]])
     [:div {:class "playersContainer"}
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
           (join-game current-player))
         (render-player 2
                        (:player-two game)
                        (-> game :score second)
                        (and (= (:turn game) "player-two") (not ended))))]
      [:input (conj {:type "submit"
                     :value "Lähetä"
                     :class "submit topbuttons"}
                    (when (or ended (not= current-player (turn game)))
                      {:disabled "disabled"}))]]]))

(defn game-page [id]
  (if (nil? @game)
    (do (fetch-game id) [:div "Fetching the game..."])
    (let [size (:size @game)]
      (println @game)
      [:div
       (render-players @game "foo") ; TODO: read current player from cookie
       [:div
        [:ul {:id "selected"}]]
       [:div {:id "board"
              :style {:width (str (* 80 size) "px")}}
        (for [tile (:tiles @game)]
          ^{:key (:id tile)} [render-tile tile size])]
       [:ul {:class "playedWords"
             :style {:top (str (* size 90) "px")}}
        (for [word (:played-words @game)]
          ^{:key (:word word)}
          [:li {:class (str "player" (:turn word))} (:word word)])]])))
