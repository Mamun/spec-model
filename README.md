# spec-model
 
Define model as data and generate Clojure(Script) spec with convention.    

  
Status: **Alpha** 

## Latest version 

[![Clojars Project](https://img.shields.io/clojars/v/spec-model.svg)](https://clojars.org/spec-model)

## Why do you need it 

Clojure spec specifies the structure of your data. But to define your spec for different format of request validation (unqualified key), business logic (qualified key) or sql data model 
is challenging. Think about different kind of data format in bellow 


* As join {:empl-name "Max" :id 23 :department {:dept-name "IT" :id 10}}
* As reverse join {:dept-name "IT" :id 10 :employee-list [{:empl-name "Max" :id 23}]}
* As list [{:dept-name "IT" :id 10}]
* As entity type {:department {:dept-name "IT" :id 10}}
* As entity type list [{:department {:dept-name "IT" :id 10}}]


How do you define spec that is understandable to all ? How do you add relation among entity map?   
         

## Features
* Like UML tools, define model and spec-model will generate spec.
* Generate spec with convention, as a result everyone within team know about spec registry.
* Generate spec for qualified key, unqualified key (with prefix -unq). 
* Generate spec for string conformation (With prefix ex-) as coercions.     
* Support join and disjoin for entity model.
* Support assoc relational key. 

## Convention 
1. Qualified key: com.abc/id, com.abc/name. Example (s/def :com.abc/id int?)
2. Qualified entity: com.abc/dept. Example (s/def :com.abc/dept (s/keys :req [:com.abc/id] :opt [:com.abc/name]))  
3. Qualified entity list: com.abc/dept-list. Example (s/def :com.abc/dept-list (s/coll-of :com.abc/dept))      
4. Qualified entity type: etype.com.abc/dept. Example (s/def :etype.com.abc/dept (s/keys :req [com.abc/dept]) ) 
5. Qualified entity type list: etype.com.abc/dept-list. Example (s/def :etype.com.abc/dept-list (s/coll-of :etype.com.abc/dept) )
6. Unqualified key start with :un. Example  (s/def :un.com.abc/id int?)
7. Repeat 2 to 5 with prefix :un
8. Extended type is used for string conformer and start with prefix ex.  Example  (s/def :ex.com.abc/id int?)
9. Repeat 2 to 5 with prefix :ex

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

;;generated spec 
;; :app/dept           {:app.dept/id 1 :app.dept/name "a"}
;; :app/dept-list      list of dept entity  
;; :un-app/dept        {:id 1 "a"} 
;; entity.un-app/dept  {:dept {:id 1 :name "a"}}


; Check spec for namespace     
(binding [s/*recursion-limit* 0]
    (clojure.pprint/pprint
      (s/exercise :app/dept 1)))

; Output 
{:app.dept/id -1,
   :app.dept/des "",
   :app.dept/name "",
   :app/student-list
   [{:app.student/name "",
     :app.student/age 0,
     :app.student/id 0}]}


(binding [s/*recursion-limit* 0]
  (clojure.pprint/pprint
   (s/exercise :app/dept-list 1)))

;;Output
[{:app.dept/id -1,
    :app.dept/des "",
    :app.dept/name "",
    :app/student-list
    [{:app.student/name "",
      :app.student/age 0,
      :app.student/id 0}]}
  
 (binding [s/*recursion-limit* 0]
    (clojure.pprint/pprint
      (s/exercise :etype.app/dept 2)))
 
 ;;Output           
([#:app{:dept #:app.dept{:id -1, :des "", :name "", :note ""}}
  #:app{:dept #:app.dept{:id -1, :des "", :name "", :note ""}}]
 [#:app{:dept #:app.dept{:id -1, :des "X", :name "0"}}
  #:app{:dept #:app.dept{:id -1, :des "X", :name "0"}}])      
      
      
; Check spec for unq namespace 
 (binding [s/*recursion-limit* 0]
     (clojure.pprint/pprint
       (s/exercise :unq.app/dept 1)))
       
;;Output 
{:id 0,
   :des "",
   :name "",
   :student-list
   [{:name "", :age 0, :id -1}
    {:name "",
     :age 0,
     :id 0}]}       
       
       
       
; Check spec for string conformer
 (binding [s/*recursion-limit* 0]
     (clojure.pprint/pprint
       (s/exercise :ex.app/dept 1)))

      
```

To check full list of generate spec
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

 ;; Assoc join key

 (binding [s/*recursion-limit* 0]
      (let [w (gen/sample (s/gen :entity.unq-app/dept) 1)]
        (clojure.pprint/pprint w)
        (->> w
             (first)
             (do-assoc-relation-key [[:dept :id :spec-model.core/rel-1-n :student :dept-id]])
             (clojure.pprint/pprint)
             ))
      )

 ;; 
 
 ```
 
## Limitation
  
 * Join and disjoin is work only for unqualified key
 
  
## License
 
 Copyright © 2016 Abdullah Mamun
 
 Distributed under the Eclipse Public License, the same as Clojure.