(ns letterpress.handler
  (:require [compojure.core :refer [GET POST defroutes context]]
            [compojure.route :refer [not-found resources]]
            [hiccup.page :refer [include-js include-css html5]]
            [letterpress.middleware :refer [wrap-middleware]]
            [letterpress.game :as game]
            [clojure.edn :as edn]
            [ring.util.response :refer [response]]
            [ring.middleware.edn :refer [wrap-edn-params]]
            [config.core :refer [env]]))

(def mount-target
  [:div#app
   [:div {:class "container"
          :style "width: 300px"}
    [:h1 "Letterpress.fi"]
    [:p "Suomenkielinen "
     [:a {:href "http://www.letterpressapp.com/"}
      "Letterpress"]
     "-kopio"]
    [:p "Ladataan..."]]])

(defn head []
  [:head
   [:title "Letterpress.fi"]
   [:meta {:charset "utf-8"}]
   [:meta {:name "viewport"
           :content "width=device-width, initial-scale=1"}]
   (include-css (if (env :dev) "/css/site.css" "/css/site.min.css"))])

(defn main-page []
  (html5
    (head)
    [:body {:class "body-container"}
     [:script
      "(function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
        (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
        m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
        })(window,document,'script','//www.google-analytics.com/analytics.js','ga');

        ga('create', 'UA-59335226-1', 'auto');
        ga('send', 'pageview');"]
     mount-target
     (include-js "/js/app.js")]))

(defn- api-response [o]
  (if (nil? o)
    (not-found "Game not found.")
    (-> o prn-str response)))

(defroutes routes
  (GET "/" [] (main-page))
  (GET "/list" [] (main-page))
  (GET "/game/:id" [id] (main-page))
  (context "/api" []
    (POST "/game" [player-name size]
      (api-response (game/create-game player-name size)))
    (GET "/game/:id" [id]
      (api-response (game/get-game id)))
    (POST "/game/:id/join" [id player-name]
      (api-response (game/join-game id player-name)))
    (POST "/game/:id/pass" [id player-name]
      (api-response (game/pass-game id player-name)))
    (POST "/game/:id/submit" [id tiles player-name]
      (api-response (game/submit-word id player-name (edn/read-string tiles))))
    (POST "/game/:id/validate" [id tiles player-name]
      (api-response (game/validate-word id player-name (edn/read-string tiles))))
    (GET "/list" [player-name]
      (api-response (game/list-games player-name))))

  (resources "/")
  (not-found "Not Found"))

(def app
  (-> routes
      wrap-middleware
      wrap-edn-params))
