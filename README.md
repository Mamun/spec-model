# spec-model
 
Define model as data and generate Clojure(Script) spec with convention.    

  
Status: **Alpha** 

## Latest version 

[![Clojars Project](http://clojars.org/mamun/spec-model/latest-version.svg)](http://clojars.org/mamun/spec-model)  

## Why do you need it 

Clojure spec specifies the structure of your data. But to define your spec for client request validation (unqualified key), business logic (qualified key) or sql data model 
is challenging. Think about your data format in bellow 


* {:empl-name "Max" :id 23 :department {:dept-name "IT" :id 10}}
* {:dept-name "IT" :id 10 :employee-list [{:empl-name "Max" :id 23}]}
* [{:dept-name "IT" :id 10}]
* {:department {:dept-name "IT" :id 10}}
* [{:department {:dept-name "IT" :id 10}}]


How do you define spec that is understandable to all ? How do you add relation among entity? As a developer do you really need to care all of them?  
         
     

## Features
* Like UML tools, define model and spec-model will generate spec.
* Generate spec with convention, as a result everyone within team know about spec registry.
* Generate spec for qualified key, unqualified key (with prefix -unq). 
* Generate spec for string value (With prefix ex-) as coercions.     
* Support join and disjoin for entity model.
 


### Define data model 
```clj
(require '[clojure.spec :as s])
(require '[spec-model.core :as m])

;; Define model 
;; app should be name should be same as maven groupid 
;; provide clojure entity spec 
;; Optional join key as relation among entity 

(m/defmodel app {:dept    {:req {:id   int?
                                 :name string?}
                           :opt {:note string?}}
                 :student {:req {:name string?
                                 :id   int?}}}
            :spec-model.core/join
            [[:dept :id :spec-model.core/rel-1-n :student :dept-id]])

;;generate spec 
;; :app/dept           {:app.dept/id 1 :app.dept/name "a"}
;; :app/dept-list      list of dept entity  
;; :un-app/dept        {:id 1 "a"} 
;; entity.un-app/dept  {:dept {:id 1 :name "a"}}


; Example   
(binding [s/*recursion-limit* 0]
    (clojure.pprint/pprint
      (s/exercise :app/dept 1)))


(binding [s/*recursion-limit* 0]
  (clojure.pprint/pprint
   (s/exercise :app/dept-list 1)))
      

      
      

```

To check full list of spec
```clj
 
 (gen-spec :app '{:dept    {:req {:id   int?
                                    :name string?}
                              :opt {:note string?}}
                    :student {:req {:name string?
                                    :id   int?}}}
             {:spec-model.core/join [[:dept :id :spec-model.core/rel-1-n :student :dept-id]]})
 
 ```
 
 
 Join and disjoin data 
 ```clj
 ;; To save data in sql database or use some external lib, you need really destructe data 
 ;; Do disjion 
 
 (binding [s/*recursion-limit* 0]
     (let [w (gen/sample (s/gen :entity.unq-app/dept) 1)]
       (clojure.pprint/pprint w)
       (->> w
            (first)
            (do-disjoin [[:dept :id :spec-model.core/rel-1-n :student :dept-id]])
            (clojure.pprint/pprint)
            ))
     )

 ;; Do join again
  
 (binding [s/*recursion-limit* 0]
      (let [w (gen/sample (s/gen :entity.unq-app/dept) 1)]
        (clojure.pprint/pprint w)
        (->> w
             (first)
             (do-disjoin [[:dept :id :spec-model.core/rel-1-n :student :dept-id]])
             (do-join [[:dept :id :spec-model.core/rel-1-n :student :dept-id]])
             (clojure.pprint/pprint)
             ))
      )

 
 
 ```
 
 ## Limitation
  
 * Join and disjoin is work only for unqualified key
 
  
 ## License
 
 Copyright © 2016 Abdullah Mamun
 
 Distributed under the Eclipse Public License, the same as Clojure.