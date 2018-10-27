(ns letterpress.views.list)

(defn list-page []
  [:div {:class "container"
         :style {:width "300px"}}
   [:h1 "Pelilista"]
   [:div [:a {:href "/"} "Siirry etusivulle"]]])
