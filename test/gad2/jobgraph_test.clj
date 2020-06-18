(ns gad2.jobgraph-test
  (:require [gad2.jobgraph :as sut]
            [gad2.rulegraph :refer [rulegraph]]
            [gad2.examples.snakemake-example :refer [snakemake-rules xs]]
            [midje.sweet :as midje])
  (:use [midje.sweet]))


(facts "about `jobgraph`"

       (sut/jobgraph (rulegraph snakemake-rules) snakemake-rules xs)

       =>

       0)
