(ns gad2.simple.find-paths-test
  (:require [gad2.core :refer [jobgraph-from-config]]
            [gad2.find-paths :refer [jobs-to-outpath-stem
                                     jobs-to-outpath-base
                                     jobs-to-outpath]])
  (:use [midje.sweet]))

(def jobgraph (gad2.core/jobgraph-from-config "examples/simple_example/config.edn"))

(def rules (gad2.parse-rulefiles/read-rules "examples/simple_example/rules.clj"))

(facts "about `jobs-to-outpath`"
       (jobs-to-outpath rules jobgraph)
       =>
       nil)
