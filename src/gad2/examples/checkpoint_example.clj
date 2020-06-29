(ns gad2.examples.checkpoint-example
  (:require
   [hashp.core]
   [gad2.state :as state]
   [gad2.parse-rulefiles :refer [defrule]]))


(def wildcards [:j])

;; when this rule is done, the DAG should be recomputed
(defrule scatter
  {:out "scatter/j.txt"
   :wildcards [:j]
   :script "create_random_number_of_files"})

(defrule checkpoint
  {:in "scatter/j.txt"})

(defrule scatter-copy
  {:in "scatter/j.txt"
   :out "scatter/j_copy.txt"
   :wildcards [:j]
   :shell "cp"})

(defrule collect-copies
  {:in "scatter/j_copy.txt"
   :out "collected.txt"
   :shell "cat {in} > {out}"})
