(ns spec-model.xtype-test
  (:use [spec-model.xtype]
        [clojure.test])
  (:require [clojure.spec :as s]
            [clojure.spec.gen :as gen]))


(comment


  (s/exercise :spec-model.core-xtype/x-int)
  (s/exercise :spec-model.core-xtype/x-inst)
  (s/exercise :spec-model.core-xtype/x-double)
  (s/exercise :spec-model.core-xtype/x-keyword)


  (count "asfd")
  )