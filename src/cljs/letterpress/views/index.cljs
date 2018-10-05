(ns letterpress.views.index
  (:require [reagent.core :as r]
            [ajax.core :refer [POST]]
            [secretary.core :as secretary]
            [letterpress.state :refer [game]]
            [cljs.tools.reader.edn :as edn]))

(def default-size 5)

(defn- redirect! [loc]
  (set! (.-location js/window) loc))

(defn- start-game [data]
  (let [new-game (edn/read-string data)]
    (reset! game new-game)
    (redirect! (str "/game/" (:_id new-game)))))

(defn- create-game [player-name]
  (if (empty? player-name)
    (println "Player name missing!") ; TODO
    (do
      (POST "/api/game"
            {:params {:player-name player-name
                      :size default-size}
             :format :raw
             :handler start-game}))))

(defn index-page []
  (let [player-name (r/atom "")]
    (fn []
      [:div
       [:h1 "Letterpress.fi"]
       [:p "A "
        [:a {:href "http://www.atebits.com/letterpress/"}
         "Letterpress"]
        " clone in Finnish"]
       [:p
        [:a {:href "/list"}
         "List"]
        (str " your games")]
       [:div
        [:input {:type "text"
                 :value @player-name
                 :on-change #(reset! player-name (-> % .-target .-value))}]
        " "
        [:input {:type "submit"
                 :value "Start a new game"
                 :on-click #(create-game @player-name)}]]])))
