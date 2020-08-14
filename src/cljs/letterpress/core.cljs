(ns letterpress.core
  (:require [reagent.core :as r]
            [reagent.dom :as dom]
            [secretary.core :as secretary :include-macros true]
            [accountant.core :as accountant]
            [letterpress.views.index :refer [index-page]]
            [letterpress.views.list :refer [list-page]]
            [letterpress.views.game :refer [game-page]]))

;; -------------------------
;; Routes

(defonce page (r/atom {:page #'index-page}))

(defn current-page []
  [:div [(:page @page) (:id @page)]])

(secretary/defroute "/" []
  (swap! page assoc :page #'index-page))

(secretary/defroute "/list" []
  (swap! page assoc :page #'list-page))

(secretary/defroute "/game/:id" [id]
  (swap! page assoc :page #'game-page :id id))


;; -------------------------
;; Initialize app

(defn mount-root []
  (dom/render [current-page] (.getElementById js/document "app")))

(defn init! []
  (accountant/configure-navigation!
    {:nav-handler
     (fn [path]
       (secretary/dispatch! path))
     :path-exists?
     (fn [path]
       (secretary/locate-route path))})
  (accountant/dispatch-current!)
  (mount-root))
