(ns spec-model.join.core
  (:require [spec-model.join.util :as p]))


(defmulti join-acc-fn (fn [acc j] (nth j 2)))

(defmethod join-acc-fn
  :default
  [acc j]
  (let [[s _ _ d _] j
        d-n (p/target-key-identifier j)]
    (if-let [v (get acc d)]
      (assoc-in acc (conj s d-n) v)
      acc)))


(defmethod join-acc-fn
  :spec-model.core/rel-n-n
  [acc j]
  (let [[s _ _ d _ [rd _ _]] j
        d-n (p/target-key-identifier j)]
    (if-let [v (get acc rd)]
      (assoc-in acc (conj s d-n) v)
      acc)))


(defn do-join-impl
  [data join-coll]
  (if (empty? join-coll)
    data
    (let [j-coll (p/replace-source-entity-path join-coll data)
          target-name (distinct (apply concat (mapv #(list (get-in % [3])
                                                     (get-in % [5 0])) j-coll)) )]
      (apply dissoc
             (reduce join-acc-fn data j-coll)
             target-name))))




(defmulti dis-join-acc-fn (fn [acc j]
                            (nth j 2)))


(defmethod dis-join-acc-fn
  :default
  [acc j]
  (let [[s _ _ d _] j
        d-n (p/target-key-identifier j)]
    (if-let [w (get-in acc (conj s d-n))]
      (-> (assoc acc d w)
          (update-in s dissoc d-n))
      (update-in acc s dissoc d-n))))



(defmethod dis-join-acc-fn
  :spec-model.core/rel-n-n
  [acc j]
  (let [[s _ _ d _ [rd _ _]] j
        d-n (p/target-key-identifier j)]
    (if-let [w (get-in acc (conj s d-n))]
      (-> (assoc acc d w)
          (update-in s dissoc d-n))
      (update-in acc s dissoc d-n))))



(defn do-disjoin-impl
  "Assoc relation key and dis-join relation model "
  [data join-coll]
  (if (empty? join-coll)
    data
    (->> (p/replace-source-entity-path join-coll data)
         (reduce dis-join-acc-fn data))))

(comment

  (let [join [[:tab :id :spec-model.core/rel-n-n :tab1 :tab-id [:ntab :tab-id :tab1-id]]]
        data {:tab {:id        100
                    :tab1-list [{:tab-id 100}
                                {:tab-id 101}]}}

        ]
    (do-disjoin-impl data join)
    )


  )



