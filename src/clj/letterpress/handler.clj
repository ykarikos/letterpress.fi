(ns letterpress.handler
  (:require [compojure.core :refer [GET POST defroutes context]]
            [compojure.route :refer [not-found resources]]
            [hiccup.page :refer [include-js include-css html5]]
            [letterpress.middleware :refer [wrap-middleware]]
            [letterpress.game :as game]
            [ring.util.response :refer [response]]
            [config.core :refer [env]]))

(def mount-target
  [:div#app
   [:h3 "ClojureScript has not been compiled!"]
   [:p "please run "
    [:b "lein figwheel"]
    " in order to start the compiler"]])

(defn head []
  [:head
   [:meta {:charset "utf-8"}]
   [:meta {:name "viewport"
           :content "width=device-width, initial-scale=1"}]
   (include-css (if (env :dev) "/css/site.css" "/css/site.min.css"))])

(defn main-page []
  (html5
    (head)
    [:body {:class "body-container"}
     mount-target
     (include-js "/js/app.js")]))

(defn- api-response [o]
  (-> o prn-str response))

(defroutes routes
  (GET "/" [] (main-page))
  (GET "/about" [] (main-page))
  (context "/api" []
    (POST "/game" [player-name size]
      (api-response (game/create-game player-name size)))
    (GET "/game/:id" [id]
      (api-response (game/get-game id))))

  (resources "/")
  (not-found "Not Found"))

(def app (wrap-middleware #'routes))
