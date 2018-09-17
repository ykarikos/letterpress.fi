(ns ^:figwheel-no-load letterpress.dev
  (:require
    [letterpress.core :as core]
    [devtools.core :as devtools]))

(devtools/install!)

(enable-console-print!)

(core/init!)
