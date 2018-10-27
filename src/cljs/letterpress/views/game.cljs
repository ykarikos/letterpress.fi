(ns letterpress.views.game
  (:require [letterpress.state :refer [game current-player]]
            [reagent.core :as r]
            [ajax.core :refer [GET POST]]
            [cljs.tools.reader.edn :as edn]))

(def selected-tiles (r/atom []))

(defn- update-game-state [data]
  (reset! game (edn/read-string data)))

(defn- fetch-game [id]
  (GET (str "/api/game/" id)
       {:handler update-game-state}))

(defn- selected-tile-click! [tile]
  (reset! selected-tiles (filterv #(not= (:id tile) (:id %)) @selected-tiles)))

(defn- tile-click! [tile]
  (swap! selected-tiles conj tile))

(defn- hidden-tile? [tile]
  (some #(= (:id tile) (:id %)) @selected-tiles))

(defn- selected-tile [tile]
  [:li
   {:on-click #(selected-tile-click! tile)
    :class (:owner tile)}
   (:letter tile)])

(defn- render-tile [tile size]
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
           [join-game-form game current-player])
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
    (do (fetch-game id) [:div "Haetaan peliä..."])
    (let [size (:size @game)]
      [:div
       (render-players @game @current-player)
       [:ul {:id "selected"
             :style {:width (* 80 (count @selected-tiles))}}
        (for [tile @selected-tiles]
          ^{:key (:id tile)} [selected-tile tile])]
       [:ul {:id "board"
             :style {:width (str (* 80 size) "px")}}
        (for [tile (:tiles @game)]
          ^{:key (:id tile)} [render-tile tile size])]
       [:ul {:class "playedWords"
             :style {:top (str (* size 90) "px")}}
        (for [word (:played-words @game)]
          ^{:key (:word word)}
          [:li {:class (str "player" (:turn word))} (:word word)])]])))
