(ns gad2.jobgraph
  (:require [gad2.xs :refer :all]))


(defn get-files
  [rule]
  (let [fs (rule :out)
        fs (if (map? fs) (vec (vals fs)) [fs])]
    fs))


(defn combine-xs-&-targets
  [xs outfiles child infiles parent]
  (apply concat
         (for [[xcs xps] xs
               infile infiles
               outfile outfiles]
           {[outfile xcs] [child xcs]
            [child xcs] (vec (for [xp xps] [infile xp]))})))


(defn jobgraph
  [rulegraph rules xs]
  (let [dwx (deps-with-xs rulegraph rules)
        pxs (precompute-xs dwx xs)]
    (for [[child childxs parent parentxs] dwx
          :let [xmap (pxs [childxs parentxs])
                outfiles (get-files (rules child))
                infiles (get-files (rules parent))
                nxs (pxs [childxs parentxs])]]
      (combine-xs-&-targets nxs outfiles child infiles parent))))
