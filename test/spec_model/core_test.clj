(ns spec-model.core-test
  (:use [clojure.test]
        [spec-model.core])
  (:require [clojure.spec.test :as stest]
            [clojure.spec :as s]
            [cheshire.core :as ch]
            [clojure.spec.gen :as gen])
  (:import (java.util Date UUID)))


(comment

  (run-tests)
  )

#_(deftest relation-merge-test
  (testing "testing relation merge"
    (let [w (relation-merge :hello [[:a :ta :spec-model.core/rel-1-1 :b :tb]
                                    [:a :ta :spec-model.core/rel-1-1 :c :tc]])]
      (is (= w
             [`(clojure.spec/merge
                 :hello/a
                 (clojure.spec/keys :opt [:hello/b-list :hello/c-list]))])))))


(deftest gen-spec-test
  (testing "gen spec test "
    (let [v (gen-spec :app '{:dept    {:req {:name string?
                                             :date inst?}
                                       :opt {:note string?}}
                             :student {:req {:name string?
                                             :dob  inst?}}}
                      {:spec-model.core/join [[:dept :id :spec-model.core/rel-1-1 :student :dept-id]]
                       :spec-model.core/gen-type #{:spec-model.core/un-qualified}
                       })]
      (is (not-empty v)))))


(deftest check-exec-test
  (testing "test generate spec "
    (do
      (defmodel test {:dept {:req {:id int?}
                             :opt {:note string?}}
                   :student {:req {:name string?
                                   :id   int?}}}
                :spec-model.core/join [[:dept :id :spec-model.core/rel-1-1 :student :dept-id]]
                :spec-model.core/gen-type #{:spec-model.core/un-qualified
                                           :spec-model.core/qualified
                                           :spec-model.core/ex})
      (is (s/valid? :test/dept {:test.dept/id 123}))
      (is (s/valid? :test/dept {:test.dept/id      123
                                :test/student-list [{:test.student/id   23
                                                     :test.student/name "asdf"}]}))
      )))


(deftest do-join-test
  (testing "testing do join "
    (let [w (->> {:dept
                  {:id -1,
                   :name "",
                   :note "",
                   :student-list
                   [{:name "", :id -1}
                    {:name "", :id -1}]}})

          j-value  (do-disjoin w [[:dept :id :spec-model.core/rel-1-n :student :dept-id]] )
          dj-value (do-join j-value [[:dept :id :spec-model.core/rel-1-n :student :dept-id]] )]
      (is (= w dj-value)))))


(deftest do-dis-join-test
  (testing "testing do disjoin "
    (let [w (->> {:dept
                  {:id -1,
                   :name "",
                   :note "",
                   :student-list
                   [{:name "", :id -1}
                    {:name "", :id -1}]}})
          j-value  (do-disjoin w [[:dept :id :spec-model.core/rel-1-n :student :dept-id]] )
          expected-value {:dept {:id -1, :name "", :note ""},
                          :student [{:name "", :id -1, }
                                    {:name "", :id -1, }]}]
      (is (= expected-value j-value)))))



(comment
  (run-tests)

  )


