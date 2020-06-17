(ns gad2.rulegraph-test
  (:require [gad2.rulegraph :refer :all]
            [gad2.examples.snakemake-example :refer [snakemake-rules]]
            [hashp.core]
            [midje.sweet :as midje])
  (:use midje.sweet))


(facts "about `rulegraph`"
       (rulegraph snakemake-rules)

       =>

       #com.stuartsierra.dependency.MapDependencyGraph
       {:dependencies {:bcftools-call #{:samtools-index :samtools-sort}
                       :plot-quals #{:bcftools-call}
                       :samtools-index #{:samtools-sort}
                       :samtools-sort #{:bwa-map}}
        :dependents {:bcftools-call #{:plot-quals}
                     :bwa-map #{:samtools-sort}
                     :samtools-index #{:bcftools-call}
                     :samtools-sort #{:bcftools-call :samtools-index}}})

(def r (rulegraph snakemake-rules))

;; #p snakemake-rules
