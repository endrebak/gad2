(ns gad2.wildcards-test
  (:require [gad2.wildcards :refer :all]
            [gad2.rulegraph :refer :all]
            [gad2.examples.snakemake-example :refer [snakemake-rules wildcards]]
            [midje.sweet :as midje])
  (:use [midje.sweet]))

(def dwx (deps-with-wildcards (rulegraph snakemake-rules) snakemake-rules))

(facts "about `deps-with-wildcards`"
       dwx

       =>

       '([:bcftools-call [:genome] :samtools-index [:sample :genome]]
         [:bcftools-call [:genome] :samtools-sort [:sample :genome]]
         [:plot-quals [:genome] :bcftools-call [:genome]]
         [:samtools-index [:sample :genome] :samtools-sort [:sample :genome]]
         [:samtools-sort [:sample :genome] :bwa-map [:sample :genome]])
       )


(facts "about `precompute-wildcards`"

       (precompute-wildcards dwx wildcards)

       =>

       {[[:genome] [:genome]] [[{:genome "hg19"} [{:genome "hg19"}]]
                               [{:genome "hg38"} [{:genome "hg38"}]]]
        [[:genome] [:sample :genome]] [[{:genome "hg19"}
                                        [{:genome "hg19" :sample "A"}
                                         {:genome "hg19" :sample "B"}]]
                                       [{:genome "hg38"}
                                        [{:genome "hg38" :sample "A"}
                                         {:genome "hg38" :sample "B"}
                                         {:genome "hg38" :sample "C"}]]]
        [[:sample :genome] [:sample :genome]] [[{:genome "hg19" :sample "A"}
                                                [{:genome "hg19" :sample "A"}]]
                                               [{:genome "hg19" :sample "B"}
                                                [{:genome "hg19" :sample "B"}]]
                                               [{:genome "hg38" :sample "A"}
                                                [{:genome "hg38" :sample "A"}]]
                                               [{:genome "hg38" :sample "B"}
                                                [{:genome "hg38" :sample "B"}]]
                                               [{:genome "hg38" :sample "C"}
                                                [{:genome "hg38" :sample "C"}]]]})
