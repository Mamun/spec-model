(ns ^:figwheel-always app.core
  (:require [devcards.core]
            [dadyspec.core :as c]
            [app.spec]
    ;  [cljs.spec.impl.gen :as gen]
            [cljs.spec :as s]
            [cljs.spec.test :as st]
            [cljs.spec.impl.gen :as gen]
            )
  (:require-macros
    [devcards.core :as dc :refer [defcard deftest defcard-rg]]
    #_[dadyspec.core :as c :refer [defsp ]]))



(defn fig-reload []
      ;; optionally touch your app-state to force rerendering depending on
      ;; your application
      ;; (swap! app-state update-in [:__figwheel_counter] inc)
      ;        (query "http://localhost:3000/tie" [:get-dept-by-id] {:id 1} handler)
      )


#_(defcard my-first-card
         (sab/html [:h1 "Devcards is freaking awesome!"]))

(defcard my-first-card
        "Hello "
        (s/explain-str :un-app.spec/company {:name "Hello"
                                         :id 1223
                                         :type #{:software}})
         )

#_(js/alert (s/valid? :un-app.spec/company {:name "Hello"
                                     :id 1223

                                     }))


(defcard my-first-card3
         "asfsd"
         (s/valid? int? 23))


;(js/alert "asdfsd")

(defcard check-student
         "Company "
         (gen/generate  :un-app.model/company)
         #_(s/valid? :un-app.spec.company/id 23)
         #_(gen/sample (s/gen :un-app.spec/company))
         )



#_(defcard All
         "all view "
         (c/gen-spec
           :hello
           '{:student {:opt {:id int?}} }
           {:gen-type :unqualified}
           ) )


#_(defcard All2
         "all view 3 "
         (c/as-file-str "hello"   (c/gen-spec :hello '{:student {:opt {:id int?}} }))


          )



#_(do
    (defsp :app {:student {:opt {:id int?}}})

  )






#_(defcard Sample
         "Spec sample"
         (s/registry)

         ;  (s/explain-str :a/student {:a.student/id "sdf"})
         ;(s/explain-str int? 23)
        )