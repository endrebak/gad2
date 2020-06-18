(ns gad2.jobgraph
  (:require [gad2.xs :refer :all]))

(defn get-files
  [rule]
  (let [fs (rule :out)
        fs (if (map? fs) (vec (vals fs)) [fs])]
    fs))

(defn jobgraph
  [rulegraph rules xs]
  (let [dwx (deps-with-xs rulegraph rules)
        pxs (precompute-xs dwx xs)]
    (for [[child childxs parent parentxs] dwx
          :let [xmap #p (pxs [childxs parentxs])
                outfiles (get-files (rules child))
                infiles (get-files (rules parent))]]
      [outfiles infiles])))
