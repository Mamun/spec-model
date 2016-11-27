(ns user
  (:require [figwheel-sidecar.repl-api :as figwheel]
            [dadyspec.core :as c]
            ))

;; Let Clojure warn you when it needs to reflect on types, or when it does math
;; on unboxed numbers. In both cases you should add type annotations to prevent
;; degraded performance.
(set! *warn-on-reflection* true)
(set! *unchecked-math* :warn-on-boxed)



(comment


  (c/write-spec-to-file
    "dev"
    :app.spec
    {:company {:req {:name string?
                     :id   int?
                     :type (s/coll-of (s/and keyword? #{:software :hardware})
                                      :into #{})}}}
    {:gen-type #{:ex :qualified :unqualified}}
    )


   (println "asdf")

  (figwheel/start-figwheel!)

  (figwheel/cljs-repl)

  ;(cljs-repl)
  ;(run)
  )