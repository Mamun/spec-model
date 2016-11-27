(ns spec-model.core
  (:require [clojure.walk :as w]
            [spec-model.spec-generator :as impl]
            [spec-model.util :as u]
            [clojure.spec :as s]
            [clojure.string]
            [spec-model.xtype]
            [spec-model.join.join-key-impl :as dj-impl]
            [spec-model.join.core :as j-impl]))


(def ^:dynamic *conformer-m*
  {'integer?              :spec-model.xtype/x-integer
   'clojure.core/integer? :spec-model.xtype/x-integer
   'int?                  :spec-model.xtype/x-int
   'clojure.core/int?     :spec-model.xtype/x-int
   'boolean?              :spec-model.xtype/x-boolean
   'clojure.core/boolean? :spec-model.xtype/x-boolean
   'double?               :spec-model.xtype/x-double
   'clojure.core/double?  :spec-model.xtype/x-double
   'keyword?              :spec-model.xtype/x-keyword
   'clojure.core/keyword  :spec-model.xtype/x-keyword
   'inst?                 :spec-model.xtype/x-inst
   'clojure.core/inst?    :spec-model.xtype/x-inst
   'uuid?                 :spec-model.xtype/x-uuid
   'clojure.core/uuid?    :spec-model.xtype/x-uuid})


(defn- conform* [m]
  (clojure.walk/postwalk (fn [s]
                           (if-let [r (get *conformer-m* s)]
                             r
                             s)
                           ) m))



(s/def :spec-model.core/req (s/every-kv keyword? any? :min-count 1))
(s/def :spec-model.core/opt (s/every-kv keyword? any? :min-count 1))

(s/def :spec-model.core/model
  (s/every-kv keyword?
              (s/merge (s/keys :opt-un [:spec-model.core/opt :spec-model.core/req]) (s/map-of #{:req :opt} any?))
              :min-count 1))



(s/def :spec-model.core/join
  (clojure.spec/*
    (clojure.spec/alt
      :one (s/tuple keyword? keyword? #{:spec-model.core/rel-1-1 :spec-model.core/rel-1-n :spec-model.core/rel-n-1} keyword? keyword?)
      :many (s/tuple keyword? keyword? #{:spec-model.core/rel-n-n} keyword? keyword? (s/tuple keyword? keyword? keyword?)))))


(s/def :spec-model.core/gen-type (s/coll-of #{:spec-model.core/qualified :spec-model.core/un-qualified :spec-model.core/ex} :pred #{}))

(s/def :spec-model.core/fixed-key? boolean?)
(s/def :spec-model.core/gen-list? boolean?)
(s/def :spec-model.core/gen-etype? boolean?)

(s/def :spec-model.core/opt-k (s/merge (s/keys :opt [:spec-model.core/join
                                                    :spec-model.core/fixed-key?
                                                    :spec-model.core/gen-type
                                                    :spec-model.core/gen-list?
                                                    :spec-model.core/gen-etype?])
                                      (s/map-of #{:spec-model.core/join :spec-model.core/gen-type
                                                  :spec-model.core/fixed-key?
                                                  :spec-model.core/gen-list?
                                                  :spec-model.core/gen-etype?} any?)))


(s/def :spec-model.core/input (s/cat :base-ns keyword?
                                    :model ::model
                                    :opt ::opt-k))
(s/def :spec-model.core/output any?)


(defn- var->symbol [v]
  (if (var? v)
    (symbol (clojure.string/replace (str v) #"#'" ""))
    v))


(def gen-config {:spec-model.core/entity-identifer :etype
                 :spec-model.core/fixed-key?       false
                 :spec-model.core/gen-list?        true
                 :spec-model.core/gen-etype?      true})


(defmulti gen-spec-impl (fn [_ _ t] t))

(defmethod gen-spec-impl
  :spec-model.core/un-qualified
  [m opt-config-m _]
  (->> (merge opt-config-m {:spec-model.core/gen-type :spec-model.core/un-qualified
                            :spec-model.core/prefix   :unq})
       (merge gen-config)
       (impl/model->spec m)))


(defmethod gen-spec-impl
  :spec-model.core/qualified
  [m opt-config-m _]
  (->> (merge opt-config-m {:spec-model.core/gen-type :spec-model.core/qualified})
       (merge gen-config)
       (impl/model->spec m)))


(defmethod gen-spec-impl
  :spec-model.core/ex
  [m opt-config-m _]
  (->> (merge opt-config-m {:spec-model.core/gen-type :spec-model.core/un-qualified
                            :spec-model.core/prefix   :ex})
       (merge gen-config)
       (impl/model->spec (conform* m))))



(defn gen-spec
  ([ns-identifier model-m opt-config-m]
   (if (s/valid? :spec-model.core/input [ns-identifier model-m opt-config-m])
     (let [m (clojure.walk/postwalk var->symbol model-m)
           opt-config-m (assoc opt-config-m :spec-model.core/ns-identifier ns-identifier)]
       (->> (or (:spec-model.core/gen-type opt-config-m)
                #{:spec-model.core/qualified
                  :spec-model.core/un-qualified
                  :spec-model.core/ex})
            (map (fn [t] (gen-spec-impl m opt-config-m t)))
            (apply concat)))
     #?(:cljs (throw (js/Error. "Opps! spec validation exception  "))
        :clj  (throw (ex-info (s/explain-str ::input [ns-identifier model-m opt-config-m]) {})))))
  ([namespace-name model-m]
   (gen-spec namespace-name model-m {})))


(s/fdef gen-spec :args ::input :ret ::output)


(defmacro defmodel
  ([m-name m & {:as opt-m}]
   (let [m-name (keyword m-name)
         opt-m (if (nil? opt-m)
                 (gen-spec m-name m)
                 (gen-spec m-name m opt-m))]
     `~(cons 'do opt-m))))



;(sc/create-ns-key :hello :a)
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn do-assoc-relation-key [data join-coll]
  (dj-impl/assoc-join-key data join-coll))


(defn do-disjoin [data join-coll]
  (j-impl/do-disjoin-impl data join-coll))


(defn do-join [data join-coll]
  (j-impl/do-join-impl data join-coll))



;;;;;;;;;;;;;;;;;;;;;;;;;;Additional spec


(defn registry [namespace-name]
  (->> (s/registry)
       (w/postwalk (fn [v]
                     (if (map? v)
                       (->> v
                            (filter (fn [[k _]]
                                      (clojure.string/includes? (str k) (str namespace-name))))
                            (into {}))
                       v)))))



(defn as-file-str [ns-ident-k spec-list]
  (let [w (str "(ns " (name ns-ident-k)  " \n (:require [clojure.spec :as s] [spec-model.core])) \n\n")]
    (->> (map str spec-list)
         (interpose "\n")
         (cons w)
         (clojure.string/join))))





(comment




  #_(s/write-spec-to-file
      "dev"
      :app.spec
      {:company {:req {:name string?
                       :id   int?
                       :type (s/coll-of (s/and keyword? #{:software :hardware})
                                        :into #{})}}})


  #_(format "(ns %s \n (:require [clojure.spec] \n [spec-model.core]) " "com.dir")

  #_(write-spec-to-file "src" :app '{:student {:req {:id int?}}} {:gen-type #{:ex}})

  )


