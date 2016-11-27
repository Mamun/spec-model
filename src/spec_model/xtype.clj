(ns spec-model.xtype
  (:require [clojure.spec :as s]
            [clojure.spec.gen :as gen])
  (:import [BigInteger]
           [Long]
           [Double]
           (java.text SimpleDateFormat)
           (java.util Date UUID)))


(defn x-int? [x]
  (cond
    (integer? x) x
    (string? x) (try
                  (Integer/parseInt x)
                  (catch Exception e
                    :clojure.spec/invalid))
    :else :clojure.spec/invalid))




(defn x-integer? [x]
  (cond
    (integer? x) x
    (string? x) (try
                  (Long/parseLong x)
                  (catch Exception e
                    :clojure.spec/invalid))
    :else :clojure.spec/invalid))



(defn x-double? [x]
  (cond
    (double? x) x
    (string? x) (try
                  (Double/parseDouble x)
                  (catch Exception e
                    :clojure.spec/invalid))
    :else :clojure.spec/invalid))




(defn x-boolean? [x]
  (cond
    (boolean? x) x
    (string? x) (cond
                  (= "true" x) true
                  (= "false" x) false
                  :else ::s/invalid)
    :else :clojure.spec/invalid))




(defn x-keyword? [x]
  (cond
    (keyword? x) x
    (string? x) (keyword x)
    :else :clojure.spec/invalid))



(def date-formater (SimpleDateFormat. "dd-MM-yyyy HH:mm:ss")) ;

(defn x-inst? [x]
  (cond
    (inst? x) x
    (string? x) (try
                  (.parse date-formater x)
                  (catch Exception e
                    :clojure.spec/invalid))
    :else :clojure.spec/invalid))




(defn x-uuid? [x]
  (cond
    (uuid? x) x
    (string? x) (try
                  (UUID/fromString x)
                  (catch Exception e
                    :clojure.spec/invalid))
    :else :clojure.spec/invalid))


(defn- matches-regex?
  "Returns true if the string matches the given regular expression"
  [v regex]
  (boolean (re-matches regex v)))


(defn email?
  "Returns true if v is an email address"
  [v]
  (if (nil? v)
    false
    (matches-regex? v
                    #"(?i)[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?")))



(s/def ::email (s/with-gen (s/and string? email?)
                           #(s/gen #{"test@test.de" "clojure@clojure.de" "fun@fun.de"})))

(defn x-int-gen []
  (gen/fmap #(str %) (gen/int)))
(defn x-integer-gen []
  (gen/fmap #(str %) (gen/large-integer)))
(defn x-double-gen []
  (gen/fmap #(str %) (gen/double)))
(defn x-boolean-gen []
  (gen/fmap #(str %) (gen/boolean)))
(defn x-keyword-gen [] (gen/fmap #(name %) (gen/keyword)))
(defn x-inst-gen [] (gen/fmap #(.format date-formater (java.util.Date. %)) (gen/large-integer)))
(defn x-uuid-gen [] (gen/fmap #(str %)
                          (gen/uuid)))


(s/def ::x-int (s/with-gen (s/conformer x-int? (fn [_ v] (str v))) x-int-gen))
(s/def ::x-integer (s/with-gen (s/conformer x-integer? (fn [_ v] (str v))) x-integer-gen))
(s/def ::x-double (s/with-gen (s/conformer x-double? (fn [_ v] (str v))) x-double-gen))
(s/def ::x-boolean (s/with-gen (s/conformer x-boolean? (fn [_ v] (str v))) x-boolean-gen))
(s/def ::x-keyword (s/with-gen (s/conformer x-keyword? (fn [_ v] (str v))) x-keyword-gen))
(s/def ::x-inst (s/with-gen (s/conformer x-inst? (fn [_ v] (str v))) x-inst-gen))
(s/def ::x-uuid (s/with-gen (s/conformer x-uuid? (fn [_ v] (str v))) x-uuid-gen))


(comment

  ;(name :asd)

  (gen/sample x-uuid-gen)

  (gen/sample x-inst-gen)

  (gen/sample x-int-gen)
  (gen/sample x-integer-gen)
  (gen/sample x-double-gen)
  )




