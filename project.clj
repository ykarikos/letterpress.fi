(defproject letterpress "0.1.0-SNAPSHOT"
  :description "A Finnish language Letterpress clone."
  :url "http://example.com/FIXME"
  :license {:name "MIT"
            :url "https://github.com/ykarikos/letterpress.fi/blob/clj-rewrite/LICENSE"}

  :dependencies [[org.clojure/clojure "1.10.1"]
                 [ring-server "0.5.0"]
                 [reagent "0.10.0"]
                 [reagent-utils "0.3.3"]
                 [ring "1.8.1"]
                 [ring/ring-defaults "0.3.2"]
                 [compojure "1.6.2"]
                 [hiccup "1.0.5"]
                 [yogthos/config "1.1.7"]
                 [alandipert/storage-atom "2.0.1"]
                 [com.novemberain/monger "3.5.0" :exclusions [com.google.guava/guava]]
                 [org.clojure/clojurescript "1.10.773"
                  :scope "provided"]
                 [clj-commons/secretary "1.2.4"]
                 [cljs-ajax "0.8.0"]
                 [fogus/ring-edn "0.3.0"]
                 [venantius/accountant "0.2.5"
                  :exclusions [org.clojure/tools.reader]]
                 [org.clojure/java.jdbc "0.7.11"]
                 [org.postgresql/postgresql "42.2.16"]
                 [clj-postgresql "0.7.0"]]

  :plugins [[lein-environ "1.2.0"]
            [lein-cljsbuild "1.1.8"]
            [lein-asset-minifier "0.4.6"
             :exclusions [org.clojure/clojure]]
            [lein-exec "0.3.7"]
            [lein-ancient "0.6.15"]]

  :aliases
  {"compile-sass" ["exec" "-e"
                   "(println (:out (clojure.java.shell/sh \"lein\" \"sass4clj\" \"once\")))"]}

  :ring {:handler letterpress.handler/app
         :uberwar-name "letterpress.war"}

  :min-lein-version "2.5.0"
  :uberjar-name "letterpress.jar"
  :main letterpress.server
  :clean-targets ^{:protect false}
  [:target-path
   [:cljsbuild :builds :app :compiler :output-dir]
   [:cljsbuild :builds :app :compiler :output-to]]

  :source-paths ["src/clj" "src/cljc"]
  :resource-paths ["resources" "target/cljsbuild"]

  :minify-assets
  [[:css {:source "resources/public/css/site.css"
          :target "resources/public/css/site.min.css"}]]

  :cljsbuild
  {:builds {:min
            {:source-paths ["src/cljs" "src/cljc" "env/prod/cljs"]
             :compiler
             {:output-to        "target/cljsbuild/public/js/app.js"
              :output-dir       "target/cljsbuild/public/js"
              :source-map       "target/cljsbuild/public/js/app.js.map"
              :optimizations :advanced
              :pretty-print  false}}
            :app
            {:source-paths ["src/cljs" "src/cljc" "env/dev/cljs"]
             :figwheel {:on-jsload "letterpress.core/mount-root"}
             :compiler
             {:main "letterpress.dev"
              :asset-path "/js/out"
              :output-to "target/cljsbuild/public/js/app.js"
              :output-dir "target/cljsbuild/public/js/out"
              :source-map true
              :optimizations :none
              :pretty-print  true}}}}

  :figwheel
  {:http-server-root "public"
   :reload-clj-files true
   :server-port 3030
   :nrepl-port 7002
   :nrepl-middleware [cider.piggieback/wrap-cljs-repl]

   :css-dirs ["resources/public/css"]
   :ring-handler letterpress.handler/app}


  :sass {:source-paths ["src/sass"]
         :target-path "resources/public/css"}

  :profiles {:dev {:repl-options {:init-ns letterpress.repl}
                   :dependencies [[cider/piggieback "0.5.1"]
                                  [binaryage/devtools "1.0.2"]
                                  [ring/ring-mock "0.4.0"]
                                  [ring/ring-devel "1.8.1"]
                                  [prone "2020-01-17"]
                                  [figwheel-sidecar "0.5.20"]
                                  [nrepl "0.8.0"]
                                  [cider/piggieback "0.5.1"]
                                  [pjstadig/humane-test-output "0.10.0"]

                                  ;; To silence warnings from sass4clj dependecies about missing logger implementation
                                  [org.slf4j/slf4j-nop "1.7.30"]]


                   :source-paths ["env/dev/clj"]
                   :plugins [[lein-figwheel "0.5.20"]
                             [com.jakemccrary/lein-test-refresh "0.24.1"]
                             [deraen/lein-sass4clj "0.5.1"]]

                   :injections [(require 'pjstadig.humane-test-output)
                                (pjstadig.humane-test-output/activate!)]

                   :env {:dev true}}

             :uberjar {:hooks [minify-assets.plugin/hooks]
                       :source-paths ["env/prod/clj"]
                       :prep-tasks ["compile-sass" "compile" ["cljsbuild" "once" "min"]]
                       :env {:production true}
                       :aot :all
                       :omit-source true}})
