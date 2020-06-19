(ns gad2.jobgraph-test
  (:require [gad2.jobgraph :as sut]
            [gad2.rulegraph :refer [rulegraph]]
            [gad2.examples.snakemake-example :refer [snakemake-rules xs]]
            [midje.sweet :as midje])
  (:use [midje.sweet]))


(facts "about `jobgraph-map`"

       (sut/jobgraph-pairs (rulegraph snakemake-rules) snakemake-rules xs)

       =>

       [[["all.vcf" {:genome "hg19"}] [:bcftools-call {:genome "hg19"}]]
        [["all.vcf" {:genome "hg38"}] [:bcftools-call {:genome "hg38"}]]
        [[:bcftools-call {:genome "hg19"}]
         ["bam/sorted.bam.bai" {:genome "hg19", :sample "A"}]]
        [[:bcftools-call {:genome "hg19"}]
         ["bam/sorted.bam.bai" {:genome "hg19", :sample "B"}]]
        [[:bcftools-call {:genome "hg38"}]
         ["bam/sorted.bam.bai" {:genome "hg38", :sample "A"}]]
        [[:bcftools-call {:genome "hg38"}]
         ["bam/sorted.bam.bai" {:genome "hg38", :sample "B"}]]
        [[:bcftools-call {:genome "hg38"}]
         ["bam/sorted.bam.bai" {:genome "hg38", :sample "C"}]]
        [["all.vcf" {:genome "hg19"}] [:bcftools-call {:genome "hg19"}]]
        [["all.vcf" {:genome "hg38"}] [:bcftools-call {:genome "hg38"}]]
        [[:bcftools-call {:genome "hg19"}]
         ["bam/sorted.bam" {:genome "hg19", :sample "A"}]]
        [[:bcftools-call {:genome "hg19"}]
         ["bam/sorted.bam" {:genome "hg19", :sample "B"}]]
        [[:bcftools-call {:genome "hg38"}]
         ["bam/sorted.bam" {:genome "hg38", :sample "A"}]]
        [[:bcftools-call {:genome "hg38"}]
         ["bam/sorted.bam" {:genome "hg38", :sample "B"}]]
        [[:bcftools-call {:genome "hg38"}]
         ["bam/sorted.bam" {:genome "hg38", :sample "C"}]]
        [["quals.tsv" {:genome "hg19"}] [:plot-quals {:genome "hg19"}]]
        [["quals.svg" {:genome "hg19"}] [:plot-quals {:genome "hg19"}]]
        [["quals.tsv" {:genome "hg38"}] [:plot-quals {:genome "hg38"}]]
        [["quals.svg" {:genome "hg38"}] [:plot-quals {:genome "hg38"}]]
        [[:plot-quals {:genome "hg19"}] ["all.vcf" {:genome "hg19"}]]
        [[:plot-quals {:genome "hg38"}] ["all.vcf" {:genome "hg38"}]]
        [["bam/sorted.bam.bai" {:genome "hg19", :sample "A"}]
         [:samtools-index {:genome "hg19", :sample "A"}]]
        [["bam/sorted.bam.bai" {:genome "hg19", :sample "B"}]
         [:samtools-index {:genome "hg19", :sample "B"}]]
        [["bam/sorted.bam.bai" {:genome "hg38", :sample "A"}]
         [:samtools-index {:genome "hg38", :sample "A"}]]
        [["bam/sorted.bam.bai" {:genome "hg38", :sample "B"}]
         [:samtools-index {:genome "hg38", :sample "B"}]]
        [["bam/sorted.bam.bai" {:genome "hg38", :sample "C"}]
         [:samtools-index {:genome "hg38", :sample "C"}]]
        [[:samtools-index {:genome "hg19", :sample "A"}]
         ["bam/sorted.bam" {:genome "hg19", :sample "A"}]]
        [[:samtools-index {:genome "hg19", :sample "B"}]
         ["bam/sorted.bam" {:genome "hg19", :sample "B"}]]
        [[:samtools-index {:genome "hg38", :sample "A"}]
         ["bam/sorted.bam" {:genome "hg38", :sample "A"}]]
        [[:samtools-index {:genome "hg38", :sample "B"}]
         ["bam/sorted.bam" {:genome "hg38", :sample "B"}]]
        [[:samtools-index {:genome "hg38", :sample "C"}]
         ["bam/sorted.bam" {:genome "hg38", :sample "C"}]]
        [["bam/sorted.bam" {:genome "hg19", :sample "A"}]
         [:samtools-sort {:genome "hg19", :sample "A"}]]
        [["bam/sorted.bam" {:genome "hg19", :sample "B"}]
         [:samtools-sort {:genome "hg19", :sample "B"}]]
        [["bam/sorted.bam" {:genome "hg38", :sample "A"}]
         [:samtools-sort {:genome "hg38", :sample "A"}]]
        [["bam/sorted.bam" {:genome "hg38", :sample "B"}]
         [:samtools-sort {:genome "hg38", :sample "B"}]]
        [["bam/sorted.bam" {:genome "hg38", :sample "C"}]
         [:samtools-sort {:genome "hg38", :sample "C"}]]
        [[:samtools-sort {:genome "hg19", :sample "A"}]
         ["bwa-map.bam" {:genome "hg19", :sample "A"}]]
        [[:samtools-sort {:genome "hg19", :sample "B"}]
         ["bwa-map.bam" {:genome "hg19", :sample "B"}]]
        [[:samtools-sort {:genome "hg38", :sample "A"}]
         ["bwa-map.bam" {:genome "hg38", :sample "A"}]]
        [[:samtools-sort {:genome "hg38", :sample "B"}]
         ["bwa-map.bam" {:genome "hg38", :sample "B"}]]
        [[:samtools-sort {:genome "hg38", :sample "C"}]
         ["bwa-map.bam" {:genome "hg38", :sample "C"}]]])

(facts "about `jobgraph`"

       (sut/jobgraph (rulegraph snakemake-rules) snakemake-rules xs)

       =>

        #com.stuartsierra.dependency.MapDependencyGraph
        {:dependencies {["all.vcf" {:genome "hg19"}] #{[:bcftools-call {:genome "hg19"}]}
                        ["all.vcf" {:genome "hg38"}] #{[:bcftools-call {:genome "hg38"}]}
                        ["bam/sorted.bam" {:genome "hg19" :sample "A"}] #{[:samtools-sort
                                                                          {:genome "hg19"
                                                                            :sample "A"}]}
                        ["bam/sorted.bam" {:genome "hg19" :sample "B"}] #{[:samtools-sort
                                                                          {:genome "hg19"
                                                                            :sample "B"}]}
                        ["bam/sorted.bam" {:genome "hg38" :sample "A"}] #{[:samtools-sort
                                                                          {:genome "hg38"
                                                                            :sample "A"}]}
                        ["bam/sorted.bam" {:genome "hg38" :sample "B"}] #{[:samtools-sort
                                                                          {:genome "hg38"
                                                                            :sample "B"}]}
                        ["bam/sorted.bam" {:genome "hg38" :sample "C"}] #{[:samtools-sort
                                                                          {:genome "hg38"
                                                                            :sample "C"}]}
                        ["bam/sorted.bam.bai" {:genome "hg19" :sample "A"}] #{[:samtools-index
                                                                              {:genome "hg19"
                                                                                :sample "A"}]}
                        ["bam/sorted.bam.bai" {:genome "hg19" :sample "B"}] #{[:samtools-index
                                                                              {:genome "hg19"
                                                                                :sample "B"}]}
                        ["bam/sorted.bam.bai" {:genome "hg38" :sample "A"}] #{[:samtools-index
                                                                              {:genome "hg38"
                                                                                :sample "A"}]}
                        ["bam/sorted.bam.bai" {:genome "hg38" :sample "B"}] #{[:samtools-index
                                                                              {:genome "hg38"
                                                                                :sample "B"}]}
                        ["bam/sorted.bam.bai" {:genome "hg38" :sample "C"}] #{[:samtools-index
                                                                              {:genome "hg38"
                                                                                :sample "C"}]}
                        ["quals.svg" {:genome "hg19"}] #{[:plot-quals {:genome "hg19"}]}
                        ["quals.svg" {:genome "hg38"}] #{[:plot-quals {:genome "hg38"}]}
                        ["quals.tsv" {:genome "hg19"}] #{[:plot-quals {:genome "hg19"}]}
                        ["quals.tsv" {:genome "hg38"}] #{[:plot-quals {:genome "hg38"}]}
                        [:bcftools-call {:genome "hg19"}] #{["bam/sorted.bam"
                                                            {:genome "hg19"
                                                              :sample "A"}]
                                                            ["bam/sorted.bam"
                                                            {:genome "hg19"
                                                              :sample "B"}]
                                                            ["bam/sorted.bam.bai"
                                                            {:genome "hg19"
                                                              :sample "A"}]
                                                            ["bam/sorted.bam.bai"
                                                            {:genome "hg19"
                                                              :sample "B"}]}
                        [:bcftools-call {:genome "hg38"}] #{["bam/sorted.bam"
                                                            {:genome "hg38"
                                                              :sample "A"}]
                                                            ["bam/sorted.bam"
                                                            {:genome "hg38"
                                                              :sample "B"}]
                                                            ["bam/sorted.bam"
                                                            {:genome "hg38"
                                                              :sample "C"}]
                                                            ["bam/sorted.bam.bai"
                                                            {:genome "hg38"
                                                              :sample "A"}]
                                                            ["bam/sorted.bam.bai"
                                                            {:genome "hg38"
                                                              :sample "B"}]
                                                            ["bam/sorted.bam.bai"
                                                            {:genome "hg38"
                                                              :sample "C"}]}
                        [:plot-quals {:genome "hg19"}] #{["all.vcf" {:genome "hg19"}]}
                        [:plot-quals {:genome "hg38"}] #{["all.vcf" {:genome "hg38"}]}
                        [:samtools-index {:genome "hg19" :sample "A"}] #{["bam/sorted.bam"
                                                                          {:genome "hg19"
                                                                          :sample "A"}]}
                        [:samtools-index {:genome "hg19" :sample "B"}] #{["bam/sorted.bam"
                                                                          {:genome "hg19"
                                                                          :sample "B"}]}
                        [:samtools-index {:genome "hg38" :sample "A"}] #{["bam/sorted.bam"
                                                                          {:genome "hg38"
                                                                          :sample "A"}]}
                        [:samtools-index {:genome "hg38" :sample "B"}] #{["bam/sorted.bam"
                                                                          {:genome "hg38"
                                                                          :sample "B"}]}
                        [:samtools-index {:genome "hg38" :sample "C"}] #{["bam/sorted.bam"
                                                                          {:genome "hg38"
                                                                          :sample "C"}]}
                        [:samtools-sort {:genome "hg19" :sample "A"}] #{["bwa-map.bam"
                                                                        {:genome "hg19"
                                                                          :sample "A"}]}
                        [:samtools-sort {:genome "hg19" :sample "B"}] #{["bwa-map.bam"
                                                                        {:genome "hg19"
                                                                          :sample "B"}]}
                        [:samtools-sort {:genome "hg38" :sample "A"}] #{["bwa-map.bam"
                                                                        {:genome "hg38"
                                                                          :sample "A"}]}
                        [:samtools-sort {:genome "hg38" :sample "B"}] #{["bwa-map.bam"
                                                                        {:genome "hg38"
                                                                          :sample "B"}]}
                        [:samtools-sort {:genome "hg38" :sample "C"}] #{["bwa-map.bam"
                                                                        {:genome "hg38"
                                                                          :sample "C"}]}}
        :dependents {["all.vcf" {:genome "hg19"}] #{[:plot-quals {:genome "hg19"}]}
                      ["all.vcf" {:genome "hg38"}] #{[:plot-quals {:genome "hg38"}]}
                      ["bam/sorted.bam" {:genome "hg19" :sample "A"}] #{[:bcftools-call
                                                                        {:genome "hg19"}]
                                                                        [:samtools-index
                                                                        {:genome "hg19"
                                                                          :sample "A"}]}
                      ["bam/sorted.bam" {:genome "hg19" :sample "B"}] #{[:bcftools-call
                                                                        {:genome "hg19"}]
                                                                        [:samtools-index
                                                                        {:genome "hg19"
                                                                          :sample "B"}]}
                      ["bam/sorted.bam" {:genome "hg38" :sample "A"}] #{[:bcftools-call
                                                                        {:genome "hg38"}]
                                                                        [:samtools-index
                                                                        {:genome "hg38"
                                                                          :sample "A"}]}
                      ["bam/sorted.bam" {:genome "hg38" :sample "B"}] #{[:bcftools-call
                                                                        {:genome "hg38"}]
                                                                        [:samtools-index
                                                                        {:genome "hg38"
                                                                          :sample "B"}]}
                      ["bam/sorted.bam" {:genome "hg38" :sample "C"}] #{[:bcftools-call
                                                                        {:genome "hg38"}]
                                                                        [:samtools-index
                                                                        {:genome "hg38"
                                                                          :sample "C"}]}
                      ["bam/sorted.bam.bai" {:genome "hg19" :sample "A"}] #{[:bcftools-call
                                                                            {:genome "hg19"}]}
                      ["bam/sorted.bam.bai" {:genome "hg19" :sample "B"}] #{[:bcftools-call
                                                                            {:genome "hg19"}]}
                      ["bam/sorted.bam.bai" {:genome "hg38" :sample "A"}] #{[:bcftools-call
                                                                            {:genome "hg38"}]}
                      ["bam/sorted.bam.bai" {:genome "hg38" :sample "B"}] #{[:bcftools-call
                                                                            {:genome "hg38"}]}
                      ["bam/sorted.bam.bai" {:genome "hg38" :sample "C"}] #{[:bcftools-call
                                                                            {:genome "hg38"}]}
                      ["bwa-map.bam" {:genome "hg19" :sample "A"}] #{[:samtools-sort
                                                                      {:genome "hg19"
                                                                      :sample "A"}]}
                      ["bwa-map.bam" {:genome "hg19" :sample "B"}] #{[:samtools-sort
                                                                      {:genome "hg19"
                                                                      :sample "B"}]}
                      ["bwa-map.bam" {:genome "hg38" :sample "A"}] #{[:samtools-sort
                                                                      {:genome "hg38"
                                                                      :sample "A"}]}
                      ["bwa-map.bam" {:genome "hg38" :sample "B"}] #{[:samtools-sort
                                                                      {:genome "hg38"
                                                                      :sample "B"}]}
                      ["bwa-map.bam" {:genome "hg38" :sample "C"}] #{[:samtools-sort
                                                                      {:genome "hg38"
                                                                      :sample "C"}]}
                      [:bcftools-call {:genome "hg19"}] #{["all.vcf" {:genome "hg19"}]}
                      [:bcftools-call {:genome "hg38"}] #{["all.vcf" {:genome "hg38"}]}
                      [:plot-quals {:genome "hg19"}] #{["quals.svg" {:genome "hg19"}]
                                                      ["quals.tsv" {:genome "hg19"}]}
                      [:plot-quals {:genome "hg38"}] #{["quals.svg" {:genome "hg38"}]
                                                      ["quals.tsv" {:genome "hg38"}]}
                      [:samtools-index {:genome "hg19" :sample "A"}] #{["bam/sorted.bam.bai"
                                                                        {:genome "hg19"
                                                                        :sample "A"}]}
                      [:samtools-index {:genome "hg19" :sample "B"}] #{["bam/sorted.bam.bai"
                                                                        {:genome "hg19"
                                                                        :sample "B"}]}
                      [:samtools-index {:genome "hg38" :sample "A"}] #{["bam/sorted.bam.bai"
                                                                        {:genome "hg38"
                                                                        :sample "A"}]}
                      [:samtools-index {:genome "hg38" :sample "B"}] #{["bam/sorted.bam.bai"
                                                                        {:genome "hg38"
                                                                        :sample "B"}]}
                      [:samtools-index {:genome "hg38" :sample "C"}] #{["bam/sorted.bam.bai"
                                                                        {:genome "hg38"
                                                                        :sample "C"}]}
                      [:samtools-sort {:genome "hg19" :sample "A"}] #{["bam/sorted.bam"
                                                                      {:genome "hg19"
                                                                        :sample "A"}]}
                      [:samtools-sort {:genome "hg19" :sample "B"}] #{["bam/sorted.bam"
                                                                      {:genome "hg19"
                                                                        :sample "B"}]}
                      [:samtools-sort {:genome "hg38" :sample "A"}] #{["bam/sorted.bam"
                                                                      {:genome "hg38"
                                                                        :sample "A"}]}
                      [:samtools-sort {:genome "hg38" :sample "B"}] #{["bam/sorted.bam"
                                                                      {:genome "hg38"
                                                                        :sample "B"}]}
                      [:samtools-sort {:genome "hg38" :sample "C"}] #{["bam/sorted.bam"
                                                                      {:genome "hg38"
                                                                        :sample "C"}]}}})
