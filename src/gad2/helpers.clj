(ns gad2.helpers
  (:require [clojure.set :as set]
            [com.stuartsierra.dependency :as dep])
  (:use [com.rpl.specter]))


(defn add-dependency [g [a b]]
  (dep/depend g a b))


(defn files [filetype rules]
  (select [MAP-VALS filetype (walker string?)] rules))


(defn file-targets
  "Return targets in pipeline."
  [rules]
  (let [infiles (files :input rules)
        outfiles (files :output rules)]
    (set/difference (set outfiles) (set infiles))))


(defn infile->rule
  "Return pairs of infile and rules."
  [rules]
  (let [r2f (select [MAP-VALS (collect :name) :input (walker string?)] rules)]
    (for [[[r] f] r2f] [f r])))


(defn outfile->rule
  "Return map of outfile to rule."
  [rules]
  (let [r2f (select [MAP-VALS (collect :name) :output (walker string?)] rules)]
    (apply merge (for [[[r] f] r2f] {f r}))))


(defn rule-targets
  "Return map of file target -> producing rule."
  [rules]
  (let [ot (vec (file-targets rules))
        f2r (outfile->rule rules)]
    (select-keys f2r ot)))
