(ns letterpress.views.list)

(defn list-page []
  [:div
   [:h1 "Game list"]
   [:div [:a {:href "/"} "go to the home page"]]])
