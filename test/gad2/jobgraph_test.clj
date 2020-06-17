(ns gad2.jobgraph-test
  (:require [gad2.jobgraph :refer :all]
            [gad2.rulegraph :refer :all]
            [gad2.examples.snakemake-example :refer [snakemake-rules xs]]
            [midje.sweet :as midje])
  (:use [midje.sweet]))

(def dwx (deps-with-xs (rulegraph snakemake-rules) snakemake-rules))

(facts "about `deps-with-xs`"
       dwx

       =>

       '([:bcftools-call [:genome] :samtools-index [:sample :genome]]
         [:bcftools-call [:genome] :samtools-sort [:sample :genome]]
         [:plot-quals [:genome] :bcftools-call [:genome]]
         [:samtools-index [:sample :genome] :samtools-sort [:sample :genome]]
         [:samtools-sort [:sample :genome] :bwa-map [:sample :genome]])
       )

(facts "about `precompute-xs`"

       (precompute-xs dwx xs)

       =>
       1)
