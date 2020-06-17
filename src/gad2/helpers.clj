(ns gad2.helpers
  (:require [clojure.set :as set])
  (:use [com.rpl.specter]))

(defn files [filetype rules]
  (select [MAP-VALS filetype (walker string?)] rules))

(defn file-targets
  "Return targets in pipeline."
  [rules]
  (let [infiles (files :in rules)
        outfiles (files :out rules)]
    (set/difference (set outfiles) (set infiles))))

(defn infile->rule
  "Return pairs of infile and rules."
  [rules]
  (let [r2f (select [MAP-VALS (collect :name) :in (walker string?)] rules)]
    (for [[[r] f] r2f] [f r])))

(defn outfile->rule
  "Return map of outfile to rule."
  [rules]
  (let [r2f (select [MAP-VALS (collect :name) :out (walker string?)] rules)]
    (apply merge (for [[[r] f] r2f] {f r}))))

(defn rule-targets
  "Return map of file target -> producing rule."
  [rules]
  (let [ot (vec (file-targets rules))
        f2r (outfile->rule rules)]
    (select-keys f2r ot)))
