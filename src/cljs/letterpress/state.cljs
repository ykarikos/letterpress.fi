(ns letterpress.state
  (:require [reagent.core :as r]
            [alandipert.storage-atom :refer [local-storage]]))

(def game (r/atom nil))

(def current-player
  (local-storage
    (r/atom nil)
    :current-player))
