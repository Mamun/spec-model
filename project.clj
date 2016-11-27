(defproject spec-model "0.1.0-SNAPSHOT"
            :description "FIXME: write description"
            :url "http://example.com/FIXME"
            :license {:name "Eclipse Public License"
                      :url  "http://www.eclipse.org/legal/epl-v10.html"}
            :dependencies [[org.clojure/clojure "1.9.0-alpha14"]]
  :plugins [[lein-cljsbuild "1.1.4"]
            [lein-figwheel "0.5.8"]
            [lein-doo "0.1.6"]
            [lein-cloverage "1.0.6"]
            [jonase/eastwood "0.2.2"]
            [codox "0.8.12"]
            [lein-midje "3.0.0"]
            [lein-pprint "1.1.1"]]
  :clean-targets ^{:protect false} [:target-path :compile-path "dev-resources/public/js"]
  :figwheel {:server-port    3001                           ;; default
             ;     :css-dirs       ["dev-resources/public/css"]   ;; watch and update CSS
             :server-logfile "target/figwheel.log"}

  :cljsbuild {:builds
              {:app
               {:source-paths ["src" "dev"]
                :figwheel     {:devcards true}
                :compiler     {:main                 app.core
                               :verbose true
                               :asset-path           "js/compiled/out"
                               :output-dir           "dev-resources/public/js/compiled/out"
                               :output-to            "dev-resources/public/js/compiled/app.js"
                               :source-map-timestamp true}}}}
  :profiles {:dev {:repl-options   {:port 4555}
                   :codox          {:src-linenum-anchor-prefix "L"
                                    :sources                   ["src"]}
                   :dependencies   [[org.clojure/tools.namespace "0.3.0-alpha3"]
                                    [clj-time "0.12.2"]
                                    [cheshire "5.6.3"]
                                    [org.clojure/tools.nrepl "0.2.12"]
                                    [org.clojure/test.check "0.9.0"]
                                    [org.clojure/clojurescript "1.9.293" ]
                                    [figwheel-sidecar "0.5.0-6"]
                                    [devcards "0.2.2" ]]}})


