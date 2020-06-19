(ns gad2.jobgraph
  (:require [gad2.xs :refer [deps-with-xs precompute-xs]]))


(defn get-files
  [rule]
  (let [fs (rule :out)
        fs (if (map? fs) (vec (vals fs)) [fs])]
    fs))


(defn combine-xs-&-ruletargets
  [xs outfiles child]
  (apply concat
         (for [[xcs _] xs
               outfile outfiles]
           {[outfile xcs] [child xcs]})))


(defn combine-xs-&-targets
  [xs outfiles child infiles]
  (apply merge
         (for [[xcs xps] xs
               outfile outfiles
               infile infiles]
           {[outfile xcs] [child xcs]
            [child xcs] [infile xps]})))


(defn jobgraph
  [rulegraph rules xs]
  (let [dwx (deps-with-xs rulegraph rules)
        pxs (precompute-xs dwx xs)]
    (apply merge
           (for [[child childxs parent parentxs] dwx
                 :let [outfiles (get-files (rules child))
                       infiles (get-files (rules parent))
                       nxs (pxs [childxs parentxs])]]
             (combine-xs-&-targets nxs outfiles child infiles)))))
