(ns gad2.jobgraph
  (:require [gad2.wildcards :refer [deps-with-wildcards precompute-wildcards]]
            [gad2.helpers :refer [add-dependency]]
            [com.stuartsierra.dependency :as dep]
            [clojure.set :as set]))


(defn get-files
  [rule]
  (let [fs (rule :output)
        fs (if (map? fs) (vec (vals fs)) [fs])]
    fs))


(defn combine-wildcards-&-outfile->child
  [wildcards outfiles child]
  (for [[xcs _] wildcards
        outfile outfiles]
    [[outfile xcs] [child xcs]]))


(defn combine-wildcards-&-infile->parent
  [wildcards child infiles]
  (apply concat (for [[xcs xps] wildcards
                      infile infiles]
                  (for [xp xps]
                    [[child xcs] [infile xp]]))))


(defn combine-wildcards-&-targets
  [nwildcards outfiles child infiles]
  (let [o->c (combine-wildcards-&-outfile->child nwildcards outfiles child)
        i->p (combine-wildcards-&-infile->parent nwildcards child infiles)]
  (concat o->c i->p)))


(defn jobgraph-pairs
  [rulegraph rules wildcards]
  (let [dwx (deps-with-wildcards rulegraph rules)
        pwildcards (precompute-wildcards dwx wildcards)]
    (apply concat
           (for [[child childwildcards parent parentwildcards] dwx
                 :let [outfiles (get-files (rules child))
                       infiles (get-files (rules parent))
                       nwildcards (pwildcards [childwildcards parentwildcards])]]
             (combine-wildcards-&-targets nwildcards outfiles child infiles)))))


(defn jobs-without-dependencies-pairs
  [rulegraph rules wildcards]
  (let [dependencyless-rules
        (set/difference
         (set (keys (:dependents rulegraph)))
         (set (keys (:dependencies rulegraph))))]
    (apply concat
           (for [r dependencyless-rules
                 :let [rule (rules r)
                       outfiles (get-files rule)
                       rule-wildcards (:wildcards rule)
                       wildcards-subset (mapv #(select-keys % rule-wildcards) wildcards)]]
             (for [wc wildcards-subset
                   outfile outfiles]
               [[outfile wc] [r wc]])))))


(defn jobgraph
  [rulegraph rules wildcards]
  (let [pairs (jobgraph-pairs rulegraph rules wildcards)
        pairs2 (jobs-without-dependencies-pairs rulegraph rules wildcards)]
    (reduce add-dependency (dep/graph) (concat pairs pairs2))))
