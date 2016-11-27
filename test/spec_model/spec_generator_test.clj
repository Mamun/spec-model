(ns spec-model.spec-generator-test
  (:use [clojure.test]
        [spec-model.spec-generator]
        [spec-model.util])
  (:require [clojure.spec.test :as stest]
            [clojure.spec :as s]))


(comment
  (run-tests)

  )



(deftest model->spec-test
  (testing "spec builder test "
    (is (= (model->spec {:student {:opt {:id :a}}}
                        {:spec-model.core/gen-type         :spec-model.core/un-qualified
                         :spec-model.core/ns-identifier    :app
                         :spec-model.core/entity-identifer :entity
                         :spec-model.core/prefix           :ex
                         :spec-model.core/gen-list?        true
                         :spec-model.core/gen-etype?      true})
           `((clojure.spec/def :ex.app.student/id :a)
              (clojure.spec/def :ex.app/student (clojure.spec/keys :req-un [] :opt-un [:ex.app.student/id]))
              (clojure.spec/def :ex.app/student-list (clojure.spec/coll-of :ex.app/student :kind clojure.core/vector?))
              (clojure.spec/def :ex.entity.app/student (clojure.spec/keys :req-un [:ex.app/student]))
              (clojure.spec/def :ex.entity.app/student-list (clojure.spec/coll-of :ex.entity.app/student :kind clojure.core/vector?))
              (clojure.spec/def
                :ex.app/model
                (clojure.spec/or
                  :ex.app/student
                  :ex.app/student
                  :ex.app/student-list
                  :ex.app/student-list
                  :ex.entity.app/student
                  :ex.entity.app/student
                  :ex.entity.app/student-list
                  :ex.entity.app/student-list)))
           ))
    )

  (testing "spec builder test  "
    (is (= (model->spec {:student {:opt {:id :a}}}
                        {:spec-model.core/gen-type         :spec-model.core/un-qualified
                         :spec-model.core/ns-identifier    :app
                         :spec-model.core/entity-identifer :entity
                         :spec-model.core/prefix           :ex})



           `((clojure.spec/def :ex.app.student/id :a)
             (clojure.spec/def :ex.app/student (clojure.spec/keys :req-un [] :opt-un [:ex.app.student/id]))
             (clojure.spec/def :ex.app/model (clojure.spec/or :ex.app/student :ex.app/student))))))
  (testing "spec builder test  "
    (is (= (model->spec {:student {:opt {:id :a}}}
                        {:spec-model.core/gen-type         :spec-model.core/qualified
                         :spec-model.core/ns-identifier    :app
                         :spec-model.core/entity-identifer :entity})
           `((clojure.spec/def :app.student/id :a)
              (clojure.spec/def :app/student (clojure.spec/keys :req [] :opt [:app.student/id]))
              (clojure.spec/def :app/model (clojure.spec/or :app/student :app/student)))


           )))
  (testing "spec gen test "
    (is (= (model->spec {:dept {:opt {:id :a}}}
                        {:spec-model.core/gen-type         :spec-model.core/un-qualified
                         :spec-model.core/ns-identifier    :app
                         :spec-model.core/join             [[:dept :id :spec-model.core/rel-1-n :student :dept-id]]
                         :spec-model.core/entity-identifer :entity
                         :spec-model.core/prefix           :unq})
           '((clojure.spec/def :unq.app.dept/id :a)
              (clojure.spec/def :unq.app/dept (clojure.spec/keys :req-un [] :opt-un [:unq.app/student-list :unq.app.dept/id]))
              (clojure.spec/def :unq.app/model (clojure.spec/or :unq.app/dept :unq.app/dept)))
           ))))



(comment

  (model->spec-test)
  )



