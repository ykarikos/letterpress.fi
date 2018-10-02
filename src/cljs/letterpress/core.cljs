(ns letterpress.core
  (:require [reagent.core :as r]
            [secretary.core :as secretary :include-macros true]
            [accountant.core :as accountant]
            [letterpress.views.index :refer [index-page]]
            [letterpress.views.list :refer [list-page]]
            [letterpress.views.game :refer [game-page]]))

;; -------------------------
;; Routes

(defonce page (r/atom #'index-page))

(defn current-page []
  [:div [@page]])

(secretary/defroute "/" []
  (reset! page #'index-page))

(secretary/defroute "/list" []
  (reset! page #'list-page))

(secretary/defroute "/game/:id" [id]
  (reset! page #'game-page))


;; -------------------------
;; Initialize app

(defn mount-root []
  (r/render [current-page] (.getElementById js/document "app")))

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
