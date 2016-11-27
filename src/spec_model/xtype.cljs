(ns spec-model.xtype
  (:require
    [goog.date.UtcDateTime]
   ; [clojure.test.check.generators]
    [clojure.spec :as s]
    #_[clojure.spec.gen :as gen]))


(defn x-int? [x]
      (cond
        (integer? x) x
        (string? x) (try
                      (js/parseInt x 10)
                      (catch js/Error e
                        :clojure.spec/invalid))
        :else :clojure.spec/invalid))


(s/def ::x-int (s/conformer x-int? (fn [_ v] (str v))))


(def ^:dynamic *conformer-m*
  {'integer?              ::x-integer
   ;'clojure.core/integer? ::x-integer
   })


