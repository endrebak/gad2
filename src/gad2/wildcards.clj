(ns gad2.wildcards)



(defn deps-with-wildcards
  "Four-tuples of dependency rule, its wildcards, the dependent rule and its wildcards."
  [rulegraph rules]
  (apply concat
         (for [[from tos] (:dependencies rulegraph)
               :let [from-wildcards (get-in rules [from :wildcards])]]
           (for [to tos
                 :let [to-wildcards (get-in rules [to :wildcards])]]
             [from from-wildcards to to-wildcards]))))


(defn precompute-wildcards
  [dwx wildcards]
  (let [fromx-tox (distinct (for [[_ fx _ tx] dwx] [fx tx]))]
    (apply merge (for [[fx tx] fromx-tox
          :let [subsetted (mapv #(select-keys % (concat fx tx)) wildcards)
                grouped (group-by #(select-keys % fx) subsetted)
                subsetted (vec (for [[k v] grouped] [k (vec (distinct (mapv #(select-keys % tx) v)))]))]]
      {[fx tx] subsetted}))))
