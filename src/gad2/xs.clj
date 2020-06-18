(ns gad2.xs)



(defn deps-with-xs
  "Four-tuples of dependency rule, its xs, the dependent rule and its xs."
  [rulegraph rules]
  (apply concat
         (for [[from tos] (:dependencies rulegraph)
               :let [from-xs (get-in rules [from :x])]]
           (for [to tos
                 :let [to-xs (get-in rules [to :x])]]
             [from from-xs to to-xs]))))


(defn precompute-xs
  [dwx xs]
  (let [fromx-tox (distinct (for [[_ fx _ tx] dwx] [fx tx]))]
    (apply merge (for [[fx tx] fromx-tox
                       :let [subsetted (mapv #(select-keys % (concat fx tx)) xs)
                             grouped (group-by #(select-keys % fx) subsetted)
                             subsetted (into {} (for [[k v] grouped] [k (vec (distinct (mapv #(select-keys % tx) v)))]))
                             ]]
                   {[fx tx] subsetted}))))
