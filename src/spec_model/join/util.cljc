(ns spec-model.join.util)


(defn empty-path
  []
  [[]])


(defn conj-index
  [data c-path]
  (let [path-value (get-in data c-path)]
    (if (sequential? path-value)
      (->> (count path-value)
           (range 0)
           (mapv #(conj c-path %)))
      [c-path])))


(defn get-path
  ([data name]
   (get-path data (empty-path) name))
  ([data p-path-coll name]
   (for [c-path p-path-coll
         i-path (conj-index data c-path)
         :let [n-path (conj i-path name)]
         w (conj-index data n-path)]
     w)))


(defn as-sequential
  [input]
  (when name
    (if (sequential? input)
      input
      [input])))



(defn replace-source-entity-path
  [j-coll data-m]
  (for [[g-key coll] (group-by first j-coll)
        j coll
        em (get-path data-m g-key)]
    (assoc j 0 em)))


(defn target-key-identifier [[s-tab s-id join-key d-tab d-id [r-tab r-id r-id2]]]
  (condp = join-key
    :spec-model.core/rel-1-n (keyword (str (name d-tab)  "-list"))
    :spec-model.core/rel-n-n (keyword (str (name d-tab) "-list"))
     d-tab))


(defn rename-join-key [join-coll]
  (mapv (fn [[s-tab s-id join-key d-tab d-id [r-tab r-id r-id2] :as j]]
          (condp = join-key
            :spec-model.core/rel-1-n [s-tab s-id join-key (keyword (str (name d-tab)  "-list")) d-id]
            :spec-model.core/rel-n-1 [s-tab s-id :spec-model.core/rel-1-n d-tab d-id]
            :spec-model.core/rel-n-n [s-tab s-id :spec-model.core/rel-n-n (keyword (str (name d-tab) "-list")) d-id [r-tab r-id2 r-id]]
            j)
          ) join-coll))
