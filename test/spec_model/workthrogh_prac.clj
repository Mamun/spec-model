(ns spec-model.workthrogh-prac
  (:require [clojure.spec :as s]
            [clojure.spec.gen :as gen]))




(comment

  (s/def :a/name string?)
  (s/def :a/child int?)
  (s/def :a/human (s/merge (s/keys  :req-un [:a/name :a/child]
                                    :opt-un [:a/human])
                           (s/map-of #{:name :child :human} any?)))


  (binding [s/*recursion-limit* 0]
    (clojure.pprint/pprint
      (s/exercise :a/human 1)
      ))




  )
