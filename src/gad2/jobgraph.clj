(ns gad2.jobgraph
  (:require
   [com.stuartsierra.dependency :as dep]))

(defn deps-with-xs
  "Four-tuples of dependency rule, its xs, the dependent rule and its xs."
  [rulegraph rules]
  (apply concat
         (for [[from tos] (:dependencies rulegraph)
               :let [from-xs (get-in rules [from :x])]]
           (for [to tos
                 :let [to-xs (get-in rules [to :x])]]
             [from from-xs to to-xs]))))


(defn submap?
  "Checks whether m contains all entries in sub."
  [^java.util.Map m ^java.util.Map sub]
  (.containsAll (.entrySet m) (.entrySet sub)))


;; (defn compute-xs
;;   [inxs outxs allxs]
;;   (let [rows (filter #(submap? % inxs) allxs)
;;         row-entries (map #(select-keys % outxs) rows)]
;;     (vec (distinct row-entries))))

(defn precompute-xs
  [dwx xs]
  (let [fromx-tox (distinct (for [[_ fx _ tx] dwx] [fx tx]))]
    (for [[fx tx] fromx-tox
          :let [subsetted (mapv #(select-keys % (concat fx tx)) xs)
                grouped (group-by #(select-keys % fx) subsetted)
                subsetted (into {} (for [[k v] grouped] [k (mapv #(select-keys % tx) v)]))
                ]]
      {[fx tx] subsetted})))

;; (defn jobgraph
;;   [rulegraph rules xs]
;;   (let [dwx (deps-with-xs rulegraph rules)])
;;   (for [[f fxs t txs] dwx
;;         :let []]

;;      ))
