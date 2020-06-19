(ns gad2.jobgraph
  (:require [gad2.xs :refer [deps-with-xs precompute-xs]]
            [gad2.helpers :refer [add-dependency]]
            [com.stuartsierra.dependency :as dep]))


(defn get-files
  [rule]
  (let [fs (rule :out)
        fs (if (map? fs) (vec (vals fs)) [fs])]
    fs))


(defn combine-xs-&-outfile->child
  [xs outfiles child]
  (for [[xcs _] xs
        outfile outfiles]
    [[outfile xcs] [child xcs]]))


(defn combine-xs-&-infile->parent
  [xs child infiles]
  (apply concat (for [[xcs xps] xs
                      infile infiles]
                  (for [xp xps]
                    [[child xcs] [infile xp]]))))


(defn combine-xs-&-targets
  [nxs outfiles child infiles]
  (let [o->c (combine-xs-&-outfile->child nxs outfiles child)
        i->p (combine-xs-&-infile->parent nxs child infiles)]
  (concat o->c i->p)))


(defn jobgraph-pairs
  [rulegraph rules xs]
  (let [dwx (deps-with-xs rulegraph rules)
        pxs (precompute-xs dwx xs)]
    (apply concat
           (for [[child childxs parent parentxs] dwx
                 :let [outfiles (get-files (rules child))
                       infiles (get-files (rules parent))
                       nxs (pxs [childxs parentxs])]]
             (combine-xs-&-targets nxs outfiles child infiles)))))


(defn jobgraph
  [rulegraph rules xs]
  (let [pairs (jobgraph-pairs rulegraph rules xs)]
    (reduce add-dependency (dep/graph) pairs)))
