(defproject letterpress "0.1.0-SNAPSHOT"
  :description "A Finnish language Letterpress clone."
  :url "http://example.com/FIXME"
  :license {:name "MIT"
            :url "https://github.com/ykarikos/letterpress.fi/blob/clj-rewrite/LICENSE"}

  :dependencies [[org.clojure/clojure "1.9.0"]
                 [ring-server "0.5.0"]
                 [reagent "0.8.1"]
                 [reagent-utils "0.3.1"]
                 [ring "1.7.0"]
                 [ring/ring-defaults "0.3.2"]
                 [compojure "1.6.1"]
                 [hiccup "1.0.5"]
                 [yogthos/config "1.1.1"]
                 [alandipert/storage-atom "1.2.4"]
                 [com.novemberain/monger "3.1.0" :exclusions [com.google.guava/guava]]
                 [org.clojure/clojurescript "1.10.339"
                  :scope "provided"]
                 [secretary "1.2.3"]
                 [cljs-ajax "0.7.5"]
                 [fogus/ring-edn "0.3.0"]
                 [venantius/accountant "0.2.4"
                  :exclusions [org.clojure/tools.reader]]]

  :plugins [[lein-environ "1.1.0"]
            [lein-cljsbuild "1.1.7"]
            [lein-asset-minifier "0.2.7"
             :exclusions [org.clojure/clojure]]]

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
  {:assets
   {"resources/public/css/site.min.css" "resources/public/css/site.css"}}

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
                   :dependencies [[cider/piggieback "0.3.8"]
                                  [binaryage/devtools "0.9.10"]
                                  [ring/ring-mock "0.3.2"]
                                  [ring/ring-devel "1.7.0"]
                                  [prone "1.6.1"]
                                  [figwheel-sidecar "0.5.17"]
                                  [nrepl "0.4.5"]
                                  [cider/piggieback "0.3.8"]
                                  [pjstadig/humane-test-output "0.8.3"]

                                  ;; To silence warnings from sass4clj dependecies about missing logger implementation
                                  [org.slf4j/slf4j-nop "1.7.25"]]


                   :source-paths ["env/dev/clj"]
                   :plugins [[lein-figwheel "0.5.17"]
                             [deraen/lein-sass4clj "0.3.1"]]

                   :injections [(require 'pjstadig.humane-test-output)
                                (pjstadig.humane-test-output/activate!)]

                   :env {:dev true}}

             :uberjar {:hooks [minify-assets.plugin/hooks]
                       :source-paths ["env/prod/clj"]
                       :prep-tasks ["compile" ["cljsbuild" "once" "min"]]
                       :env {:production true}
                       :aot :all
                       :omit-source true}})
