(ns gad2.find-paths-test
  (:require  [midje.sweet :as midje]
             [gad2.examples.snakemake-example :refer [snakemake-rules]]
             [gad2.find-paths :refer [jobs-to-outpath-stem
                                      jobs-to-outpath-base
                                      jobs-to-outpath]])
  (:use [midje.sweet]))

(def jobgraph
  #com.stuartsierra.dependency.MapDependencyGraph
  {:dependencies {[:samtools-sort {:sample "B", :genome "hg19"}] #{["bwa-map.bam" {:sample "B", :genome "hg19"}]}, ["bwa-map.bam" {:sample "A", :genome "hg19"}] #{[:bwa-map {:sample "A", :genome "hg19"}]}, [:samtools-sort {:sample "C", :genome "hg38"}] #{["bwa-map.bam" {:sample "C", :genome "hg38"}]}, ["bam/sorted.bam.bai" {:sample "B", :genome "hg19"}] #{[:samtools-index {:sample "B", :genome "hg19"}]}, ["bam/sorted.bam.bai" {:sample "C", :genome "hg38"}] #{[:samtools-index {:sample "C", :genome "hg38"}]}, ["bam/sorted.bam" {:sample "B", :genome "hg19"}] #{[:samtools-sort {:sample "B", :genome "hg19"}]}, ["bam/sorted.bam" {:sample "A", :genome "hg38"}] #{[:samtools-sort {:sample "A", :genome "hg38"}]}, ["bwa-map.bam" {:sample "B", :genome "hg19"}] #{[:bwa-map {:sample "B", :genome "hg19"}]}, [:bcftools-call {:genome "hg19"}] #{["bam/sorted.bam.bai" {:sample "B", :genome "hg19"}] ["bam/sorted.bam" {:sample "B", :genome "hg19"}] ["bam/sorted.bam" {:sample "A", :genome "hg19"}] ["bam/sorted.bam.bai" {:sample "A", :genome "hg19"}]}, ["bam/sorted.bam" {:sample "A", :genome "hg19"}] #{[:samtools-sort {:sample "A", :genome "hg19"}]}, [["quals.svg" "quals.tsv"] {:genome "hg19"}] #{[:plot-quals {:genome "hg19"}]}, [:samtools-sort {:sample "B", :genome "hg38"}] #{["bwa-map.bam" {:sample "B", :genome "hg38"}]}, ["bam/sorted.bam" {:sample "C", :genome "hg38"}] #{[:samtools-sort {:sample "C", :genome "hg38"}]}, [:samtools-index {:sample "A", :genome "hg19"}] #{["bam/sorted.bam" {:sample "A", :genome "hg19"}]}, [:bcftools-call {:genome "hg38"}] #{["bam/sorted.bam.bai" {:sample "C", :genome "hg38"}] ["bam/sorted.bam" {:sample "A", :genome "hg38"}] ["bam/sorted.bam" {:sample "C", :genome "hg38"}] ["bam/sorted.bam.bai" {:sample "A", :genome "hg38"}] ["bam/sorted.bam.bai" {:sample "B", :genome "hg38"}] ["bam/sorted.bam" {:sample "B", :genome "hg38"}]}, ["bam/sorted.bam.bai" {:sample "A", :genome "hg38"}] #{[:samtools-index {:sample "A", :genome "hg38"}]}, ["bwa-map.bam" {:sample "A", :genome "hg38"}] #{[:bwa-map {:sample "A", :genome "hg38"}]}, [:samtools-index {:sample "C", :genome "hg38"}] #{["bam/sorted.bam" {:sample "C", :genome "hg38"}]}, ["bam/sorted.bam.bai" {:sample "A", :genome "hg19"}] #{[:samtools-index {:sample "A", :genome "hg19"}]}, [:samtools-sort {:sample "A", :genome "hg19"}] #{["bwa-map.bam" {:sample "A", :genome "hg19"}]}, ["bwa-map.bam" {:sample "C", :genome "hg38"}] #{[:bwa-map {:sample "C", :genome "hg38"}]}, [:samtools-index {:sample "A", :genome "hg38"}] #{["bam/sorted.bam" {:sample "A", :genome "hg38"}]}, [["quals.svg" "quals.tsv"] {:genome "hg38"}] #{[:plot-quals {:genome "hg38"}]}, ["bwa-map.bam" {:sample "B", :genome "hg38"}] #{[:bwa-map {:sample "B", :genome "hg38"}]}, [:plot-quals {:genome "hg19"}] #{["all.vcf" {:genome "hg19"}]}, [:samtools-index {:sample "B", :genome "hg38"}] #{["bam/sorted.bam" {:sample "B", :genome "hg38"}]}, ["bam/sorted.bam.bai" {:sample "B", :genome "hg38"}] #{[:samtools-index {:sample "B", :genome "hg38"}]}, [:samtools-sort {:sample "A", :genome "hg38"}] #{["bwa-map.bam" {:sample "A", :genome "hg38"}]}, ["bam/sorted.bam" {:sample "B", :genome "hg38"}] #{[:samtools-sort {:sample "B", :genome "hg38"}]}, ["all.vcf" {:genome "hg38"}] #{[:bcftools-call {:genome "hg38"}]}, [:samtools-index {:sample "B", :genome "hg19"}] #{["bam/sorted.bam" {:sample "B", :genome "hg19"}]}, ["all.vcf" {:genome "hg19"}] #{[:bcftools-call {:genome "hg19"}]}, [:plot-quals {:genome "hg38"}] #{["all.vcf" {:genome "hg38"}]}}, :dependents {[:samtools-sort {:sample "B", :genome "hg19"}] #{["bam/sorted.bam" {:sample "B", :genome "hg19"}]}, ["bwa-map.bam" {:sample "A", :genome "hg19"}] #{[:samtools-sort {:sample "A", :genome "hg19"}]}, [:samtools-sort {:sample "C", :genome "hg38"}] #{["bam/sorted.bam" {:sample "C", :genome "hg38"}]}, ["bam/sorted.bam.bai" {:sample "B", :genome "hg19"}] #{[:bcftools-call {:genome "hg19"}]}, ["bam/sorted.bam.bai" {:sample "C", :genome "hg38"}] #{[:bcftools-call {:genome "hg38"}]}, ["bam/sorted.bam" {:sample "B", :genome "hg19"}] #{[:bcftools-call {:genome "hg19"}] [:samtools-index {:sample "B", :genome "hg19"}]}, ["bam/sorted.bam" {:sample "A", :genome "hg38"}] #{[:bcftools-call {:genome "hg38"}] [:samtools-index {:sample "A", :genome "hg38"}]}, ["bwa-map.bam" {:sample "B", :genome "hg19"}] #{[:samtools-sort {:sample "B", :genome "hg19"}]}, [:bcftools-call {:genome "hg19"}] #{["all.vcf" {:genome "hg19"}]}, ["bam/sorted.bam" {:sample "A", :genome "hg19"}] #{[:bcftools-call {:genome "hg19"}] [:samtools-index {:sample "A", :genome "hg19"}]}, [:bwa-map {:sample "B", :genome "hg19"}] #{["bwa-map.bam" {:sample "B", :genome "hg19"}]}, [:samtools-sort {:sample "B", :genome "hg38"}] #{["bam/sorted.bam" {:sample "B", :genome "hg38"}]}, ["bam/sorted.bam" {:sample "C", :genome "hg38"}] #{[:bcftools-call {:genome "hg38"}] [:samtools-index {:sample "C", :genome "hg38"}]}, [:samtools-index {:sample "A", :genome "hg19"}] #{["bam/sorted.bam.bai" {:sample "A", :genome "hg19"}]}, [:bcftools-call {:genome "hg38"}] #{["all.vcf" {:genome "hg38"}]}, ["bam/sorted.bam.bai" {:sample "A", :genome "hg38"}] #{[:bcftools-call {:genome "hg38"}]}, [:bwa-map {:sample "C", :genome "hg38"}] #{["bwa-map.bam" {:sample "C", :genome "hg38"}]}, ["bwa-map.bam" {:sample "A", :genome "hg38"}] #{[:samtools-sort {:sample "A", :genome "hg38"}]}, [:samtools-index {:sample "C", :genome "hg38"}] #{["bam/sorted.bam.bai" {:sample "C", :genome "hg38"}]}, ["bam/sorted.bam.bai" {:sample "A", :genome "hg19"}] #{[:bcftools-call {:genome "hg19"}]}, [:samtools-sort {:sample "A", :genome "hg19"}] #{["bam/sorted.bam" {:sample "A", :genome "hg19"}]}, ["bwa-map.bam" {:sample "C", :genome "hg38"}] #{[:samtools-sort {:sample "C", :genome "hg38"}]}, [:samtools-index {:sample "A", :genome "hg38"}] #{["bam/sorted.bam.bai" {:sample "A", :genome "hg38"}]}, [:bwa-map {:sample "A", :genome "hg38"}] #{["bwa-map.bam" {:sample "A", :genome "hg38"}]}, ["bwa-map.bam" {:sample "B", :genome "hg38"}] #{[:samtools-sort {:sample "B", :genome "hg38"}]}, [:plot-quals {:genome "hg19"}] #{[["quals.svg" "quals.tsv"] {:genome "hg19"}]}, [:samtools-index {:sample "B", :genome "hg38"}] #{["bam/sorted.bam.bai" {:sample "B", :genome "hg38"}]}, [:bwa-map {:sample "A", :genome "hg19"}] #{["bwa-map.bam" {:sample "A", :genome "hg19"}]}, ["bam/sorted.bam.bai" {:sample "B", :genome "hg38"}] #{[:bcftools-call {:genome "hg38"}]}, [:samtools-sort {:sample "A", :genome "hg38"}] #{["bam/sorted.bam" {:sample "A", :genome "hg38"}]}, ["bam/sorted.bam" {:sample "B", :genome "hg38"}] #{[:bcftools-call {:genome "hg38"}] [:samtools-index {:sample "B", :genome "hg38"}]}, ["all.vcf" {:genome "hg38"}] #{[:plot-quals {:genome "hg38"}]}, [:samtools-index {:sample "B", :genome "hg19"}] #{["bam/sorted.bam.bai" {:sample "B", :genome "hg19"}]}, ["all.vcf" {:genome "hg19"}] #{[:plot-quals {:genome "hg19"}]}, [:plot-quals {:genome "hg38"}] #{[["quals.svg" "quals.tsv"] {:genome "hg38"}]}, [:bwa-map {:sample "B", :genome "hg38"}] #{["bwa-map.bam" {:sample "B", :genome "hg38"}]}}})

(facts "about `jobs-to-outpath-stem`"
       (jobs-to-outpath-stem jobgraph)

       =>

       {[:bcftools-call {:genome "hg19"}] "bcftools-call/genome/hg19"
        [:bcftools-call {:genome "hg38"}] "bcftools-call/genome/hg38"
        [:bwa-map {:genome "hg19" :sample "A"}] "bwa-map/genome/hg19/sample/A"
        [:bwa-map {:genome "hg19" :sample "B"}] "bwa-map/genome/hg19/sample/B"
        [:bwa-map {:genome "hg38" :sample "A"}] "bwa-map/genome/hg38/sample/A"
        [:bwa-map {:genome "hg38" :sample "B"}] "bwa-map/genome/hg38/sample/B"
        [:bwa-map {:genome "hg38" :sample "C"}] "bwa-map/genome/hg38/sample/C"
        [:plot-quals {:genome "hg19"}] "plot-quals/genome/hg19"
        [:plot-quals {:genome "hg38"}] "plot-quals/genome/hg38"
        [:samtools-index {:genome "hg19" :sample "A"}] "samtools-index/genome/hg19/sample/A"
        [:samtools-index {:genome "hg19" :sample "B"}] "samtools-index/genome/hg19/sample/B"
        [:samtools-index {:genome "hg38" :sample "A"}] "samtools-index/genome/hg38/sample/A"
        [:samtools-index {:genome "hg38" :sample "B"}] "samtools-index/genome/hg38/sample/B"
        [:samtools-index {:genome "hg38" :sample "C"}] "samtools-index/genome/hg38/sample/C"
        [:samtools-sort {:genome "hg19" :sample "A"}] "samtools-sort/genome/hg19/sample/A"
        [:samtools-sort {:genome "hg19" :sample "B"}] "samtools-sort/genome/hg19/sample/B"
        [:samtools-sort {:genome "hg38" :sample "A"}] "samtools-sort/genome/hg38/sample/A"
        [:samtools-sort {:genome "hg38" :sample "B"}] "samtools-sort/genome/hg38/sample/B"
        [:samtools-sort {:genome "hg38" :sample "C"}] "samtools-sort/genome/hg38/sample/C"})

(facts "about `jobs-to-outpath-base`"
       (jobs-to-outpath-base snakemake-rules)

       =>

       {:bcftools-call "all.vcf"
        :bwa-map "bwa-map.bam"
        :plot-quals {:data "quals.tsv" :plot "quals.svg"}
        :samtools-index "bam/sorted.bam.bai"
        :samtools-sort "bam/sorted.bam"})


(facts "about `jobs-to-outpath`"
       (jobs-to-outpath snakemake-rules jobgraph)

       =>

       {["all.vcf" {:genome "hg19"}] "/"
 ["all.vcf" {:genome "hg38"}] "/"
 ["bam/sorted.bam" {:genome "hg19" :sample "A"}] "/"
 ["bam/sorted.bam" {:genome "hg19" :sample "B"}] "/"
 ["bam/sorted.bam" {:genome "hg38" :sample "A"}] "/"
 ["bam/sorted.bam" {:genome "hg38" :sample "B"}] "/"
 ["bam/sorted.bam" {:genome "hg38" :sample "C"}] "/"
 ["bam/sorted.bam.bai" {:genome "hg19" :sample "A"}] "/"
 ["bam/sorted.bam.bai" {:genome "hg19" :sample "B"}] "/"
 ["bam/sorted.bam.bai" {:genome "hg38" :sample "A"}] "/"
 ["bam/sorted.bam.bai" {:genome "hg38" :sample "B"}] "/"
 ["bam/sorted.bam.bai" {:genome "hg38" :sample "C"}] "/"
 ["bwa-map.bam" {:genome "hg19" :sample "A"}] "/"
 ["bwa-map.bam" {:genome "hg19" :sample "B"}] "/"
 ["bwa-map.bam" {:genome "hg38" :sample "A"}] "/"
 ["bwa-map.bam" {:genome "hg38" :sample "B"}] "/"
 ["bwa-map.bam" {:genome "hg38" :sample "C"}] "/"
 [:bcftools-call {:genome "hg19"}] "bcftools-call/genome/hg19/all.vcf"
 [:bcftools-call {:genome "hg38"}] "bcftools-call/genome/hg38/all.vcf"
 [:bwa-map {:genome "hg19" :sample "A"}] "bwa-map/genome/hg19/sample/A/bwa-map.bam"
 [:bwa-map {:genome "hg19" :sample "B"}] "bwa-map/genome/hg19/sample/B/bwa-map.bam"
 [:bwa-map {:genome "hg38" :sample "A"}] "bwa-map/genome/hg38/sample/A/bwa-map.bam"
 [:bwa-map {:genome "hg38" :sample "B"}] "bwa-map/genome/hg38/sample/B/bwa-map.bam"
 [:bwa-map {:genome "hg38" :sample "C"}] "bwa-map/genome/hg38/sample/C/bwa-map.bam"
 [:plot-quals {:genome "hg19"}] "plot-quals/genome/hg19/{:data \"quals.tsv\", :plot \"quals.svg\"}"
 [:plot-quals {:genome "hg38"}] "plot-quals/genome/hg38/{:data \"quals.tsv\", :plot \"quals.svg\"}"
 [:samtools-index {:genome "hg19" :sample "A"}] "samtools-index/genome/hg19/sample/A/bam/sorted.bam.bai"
 [:samtools-index {:genome "hg19" :sample "B"}] "samtools-index/genome/hg19/sample/B/bam/sorted.bam.bai"
 [:samtools-index {:genome "hg38" :sample "A"}] "samtools-index/genome/hg38/sample/A/bam/sorted.bam.bai"
 [:samtools-index {:genome "hg38" :sample "B"}] "samtools-index/genome/hg38/sample/B/bam/sorted.bam.bai"
 [:samtools-index {:genome "hg38" :sample "C"}] "samtools-index/genome/hg38/sample/C/bam/sorted.bam.bai"
 [:samtools-sort {:genome "hg19" :sample "A"}] "samtools-sort/genome/hg19/sample/A/bam/sorted.bam"
 [:samtools-sort {:genome "hg19" :sample "B"}] "samtools-sort/genome/hg19/sample/B/bam/sorted.bam"
 [:samtools-sort {:genome "hg38" :sample "A"}] "samtools-sort/genome/hg38/sample/A/bam/sorted.bam"
 [:samtools-sort {:genome "hg38" :sample "B"}] "samtools-sort/genome/hg38/sample/B/bam/sorted.bam"
 [:samtools-sort {:genome "hg38" :sample "C"}] "samtools-sort/genome/hg38/sample/C/bam/sorted.bam"}
)
