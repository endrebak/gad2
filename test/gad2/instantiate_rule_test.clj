;; (ns gad2.instantiate-rule-test
;;   (:require [gad2.instantiate-rule :as sut :refer :all]
;;             [gad2.examples.snakemake-example :refer [snakemake-rules wildcards]]
;;             [clojure.string :as str]
;;             [clojure.set :as set]
;;             [midje.sweet :as midje])
;;   (:use [midje.sweet]
;;         [selmer.parser]))




;; (def example-jobgraph
;;   #com.stuartsierra.dependency.MapDependencyGraph
;;   {:dependencies {["all.vcf" {:genome "hg19"}] #{[:bcftools-call {:genome "hg19"}]}
;;                   ["all.vcf" {:genome "hg38"}] #{[:bcftools-call {:genome "hg38"}]}
;;                   ["bam/sorted.bam" {:genome "hg19" :sample "A"}] #{[:samtools-sort
;;                                                                      {:genome "hg19"
;;                                                                       :sample "A"}]}
;;                   ["bam/sorted.bam" {:genome "hg19" :sample "B"}] #{[:samtools-sort
;;                                                                      {:genome "hg19"
;;                                                                       :sample "B"}]}
;;                   ["bam/sorted.bam" {:genome "hg38" :sample "A"}] #{[:samtools-sort
;;                                                                      {:genome "hg38"
;;                                                                       :sample "A"}]}
;;                   ["bam/sorted.bam" {:genome "hg38" :sample "B"}] #{[:samtools-sort
;;                                                                      {:genome "hg38"
;;                                                                       :sample "B"}]}
;;                   ["bam/sorted.bam.bai" {:genome "hg19" :sample "A"}] #{[:samtools-index
;;                                                                          {:genome "hg19"
;;                                                                           :sample "A"}]}
;;                   ["bam/sorted.bam.bai" {:genome "hg19" :sample "B"}] #{[:samtools-index
;;                                                                          {:genome "hg19"
;;                                                                           :sample "B"}]}
;;                   ["bam/sorted.bam.bai" {:genome "hg38" :sample "A"}] #{[:samtools-index
;;                                                                          {:genome "hg38"
;;                                                                           :sample "A"}]}
;;                   ["bam/sorted.bam.bai" {:genome "hg38" :sample "B"}] #{[:samtools-index
;;                                                                          {:genome "hg38"
;;                                                                           :sample "B"}]}
;;                   ["bwa-map.bam" {:genome "hg19" :sample "A"}] #{[:bwa-map
;;                                                                   {:genome "hg19"
;;                                                                    :sample "A"}]}
;;                   ["bwa-map.bam" {:genome "hg19" :sample "B"}] #{[:bwa-map
;;                                                                   {:genome "hg19"
;;                                                                    :sample "B"}]}
;;                   ["bwa-map.bam" {:genome "hg38" :sample "A"}] #{[:bwa-map
;;                                                                   {:genome "hg38"
;;                                                                    :sample "A"}]}
;;                   ["bwa-map.bam" {:genome "hg38" :sample "B"}] #{[:bwa-map
;;                                                                   {:genome "hg38"
;;                                                                    :sample "B"}]}
;;                   [:bcftools-call {:genome "hg19"}] #{["bam/sorted.bam"
;;                                                        {:genome "hg19"
;;                                                         :sample "A"}]
;;                                                       ["bam/sorted.bam"
;;                                                        {:genome "hg19"
;;                                                         :sample "B"}]
;;                                                       ["bam/sorted.bam.bai"
;;                                                        {:genome "hg19"
;;                                                         :sample "A"}]
;;                                                       ["bam/sorted.bam.bai"
;;                                                        {:genome "hg19"
;;                                                         :sample "B"}]}
;;                   [:bcftools-call {:genome "hg38"}] #{["bam/sorted.bam"
;;                                                        {:genome "hg38"
;;                                                         :sample "A"}]
;;                                                       ["bam/sorted.bam"
;;                                                        {:genome "hg38"
;;                                                         :sample "B"}]
;;                                                       ["bam/sorted.bam.bai"
;;                                                        {:genome "hg38"
;;                                                         :sample "A"}]
;;                                                       ["bam/sorted.bam.bai"
;;                                                        {:genome "hg38"
;;                                                         :sample "B"}]}
;;                   [:samtools-index {:genome "hg19" :sample "A"}] #{["bam/sorted.bam"
;;                                                                     {:genome "hg19"
;;                                                                      :sample "A"}]}
;;                   [:samtools-index {:genome "hg19" :sample "B"}] #{["bam/sorted.bam"
;;                                                                     {:genome "hg19"
;;                                                                      :sample "B"}]}
;;                   [:samtools-index {:genome "hg38" :sample "A"}] #{["bam/sorted.bam"
;;                                                                     {:genome "hg38"
;;                                                                      :sample "A"}]}
;;                   [:samtools-index {:genome "hg38" :sample "B"}] #{["bam/sorted.bam"
;;                                                                     {:genome "hg38"
;;                                                                      :sample "B"}]}
;;                   [:samtools-sort {:genome "hg19" :sample "A"}] #{["bwa-map.bam"
;;                                                                    {:genome "hg19"
;;                                                                     :sample "A"}]}
;;                   [:samtools-sort {:genome "hg19" :sample "B"}] #{["bwa-map.bam"
;;                                                                    {:genome "hg19"
;;                                                                     :sample "B"}]}
;;                   [:samtools-sort {:genome "hg38" :sample "A"}] #{["bwa-map.bam"
;;                                                                    {:genome "hg38"
;;                                                                     :sample "A"}]}
;;                   [:samtools-sort {:genome "hg38" :sample "B"}] #{["bwa-map.bam"
;;                                                                    {:genome "hg38"
;;                                                                     :sample "B"}]}}
;;    :dependents {["bam/sorted.bam" {:genome "hg19" :sample "A"}] #{[:bcftools-call
;;                                                                    {:genome "hg19"}]
;;                                                                   [:samtools-index
;;                                                                    {:genome "hg19"
;;                                                                     :sample "A"}]}
;;                 ["bam/sorted.bam" {:genome "hg19" :sample "B"}] #{[:bcftools-call
;;                                                                    {:genome "hg19"}]
;;                                                                   [:samtools-index
;;                                                                    {:genome "hg19"
;;                                                                     :sample "B"}]}
;;                 ["bam/sorted.bam" {:genome "hg38" :sample "A"}] #{[:bcftools-call
;;                                                                    {:genome "hg38"}]
;;                                                                   [:samtools-index
;;                                                                    {:genome "hg38"
;;                                                                     :sample "A"}]}
;;                 ["bam/sorted.bam" {:genome "hg38" :sample "B"}] #{[:bcftools-call
;;                                                                    {:genome "hg38"}]
;;                                                                   [:samtools-index
;;                                                                    {:genome "hg38"
;;                                                                     :sample "B"}]}
;;                 ["bam/sorted.bam.bai" {:genome "hg19" :sample "A"}] #{[:bcftools-call
;;                                                                        {:genome "hg19"}]}
;;                 ["bam/sorted.bam.bai" {:genome "hg19" :sample "B"}] #{[:bcftools-call
;;                                                                        {:genome "hg19"}]}
;;                 ["bam/sorted.bam.bai" {:genome "hg38" :sample "A"}] #{[:bcftools-call
;;                                                                        {:genome "hg38"}]}
;;                 ["bam/sorted.bam.bai" {:genome "hg38" :sample "B"}] #{[:bcftools-call
;;                                                                        {:genome "hg38"}]}
;;                 ["bwa-map.bam" {:genome "hg19" :sample "A"}] #{[:samtools-sort
;;                                                                 {:genome "hg19"
;;                                                                  :sample "A"}]}
;;                 ["bwa-map.bam" {:genome "hg19" :sample "B"}] #{[:samtools-sort
;;                                                                 {:genome "hg19"
;;                                                                  :sample "B"}]}
;;                 ["bwa-map.bam" {:genome "hg38" :sample "A"}] #{[:samtools-sort
;;                                                                 {:genome "hg38"
;;                                                                  :sample "A"}]}
;;                 ["bwa-map.bam" {:genome "hg38" :sample "B"}] #{[:samtools-sort
;;                                                                 {:genome "hg38"
;;                                                                  :sample "B"}]}
;;                 [:bcftools-call {:genome "hg19"}] #{["all.vcf" {:genome "hg19"}]}
;;                 [:bcftools-call {:genome "hg38"}] #{["all.vcf" {:genome "hg38"}]}
;;                 [:bwa-map {:genome "hg19" :sample "A"}] #{["bwa-map.bam"
;;                                                            {:genome "hg19"
;;                                                             :sample "A"}]}
;;                 [:bwa-map {:genome "hg19" :sample "B"}] #{["bwa-map.bam"
;;                                                            {:genome "hg19"
;;                                                             :sample "B"}]}
;;                 [:bwa-map {:genome "hg38" :sample "A"}] #{["bwa-map.bam"
;;                                                            {:genome "hg38"
;;                                                             :sample "A"}]}
;;                 [:bwa-map {:genome "hg38" :sample "B"}] #{["bwa-map.bam"
;;                                                            {:genome "hg38"
;;                                                             :sample "B"}]}
;;                 [:samtools-index {:genome "hg19" :sample "A"}] #{["bam/sorted.bam.bai"
;;                                                                   {:genome "hg19"
;;                                                                    :sample "A"}]}
;;                 [:samtools-index {:genome "hg19" :sample "B"}] #{["bam/sorted.bam.bai"
;;                                                                   {:genome "hg19"
;;                                                                    :sample "B"}]}
;;                 [:samtools-index {:genome "hg38" :sample "A"}] #{["bam/sorted.bam.bai"
;;                                                                   {:genome "hg38"
;;                                                                    :sample "A"}]}
;;                 [:samtools-index {:genome "hg38" :sample "B"}] #{["bam/sorted.bam.bai"
;;                                                                   {:genome "hg38"
;;                                                                    :sample "B"}]}
;;                 [:samtools-sort {:genome "hg19" :sample "A"}] #{["bam/sorted.bam"
;;                                                                  {:genome "hg19"
;;                                                                   :sample "A"}]}
;;                 [:samtools-sort {:genome "hg19" :sample "B"}] #{["bam/sorted.bam"
;;                                                                  {:genome "hg19"
;;                                                                   :sample "B"}]}
;;                 [:samtools-sort {:genome "hg38" :sample "A"}] #{["bam/sorted.bam"
;;                                                                  {:genome "hg38"
;;                                                                   :sample "A"}]}
;;                 [:samtools-sort {:genome "hg38" :sample "B"}] #{["bam/sorted.bam"
;;                                                                  {:genome "hg38"
;;                                                                   :sample "B"}]}}})




;; (facts "about `filetarget-to-path`"
;;        (filetarget-to-path
;;         [["all.vcf" {:genome "hg38"}] [:bcftools-call {:genome "hg38"}]] "/mnt/work")

;;        =>

;;        "/mnt/work/bcftools-call/hg38/all.vcf"

;;        (filetarget-to-path
;;         [["quals.tsv" {:genome "hg38"}] [:plot-quals {:genome "hg38"}]] "/mnt/work")

;;        =>

;;        "/mnt/work/plot-quals/hg38/quals.tsv")



;; (facts "about `filetargets-to-path`"

;;        (filetargets-to-path
;;         '([["all.vcf" {:genome "hg38"}] [:bcftools-call {:genome "hg38"}]]
;;           [["all.vcf" {:genome "hg19"}] [:bcftools-call {:genome "hg19"}]]
;;           [["bam/sorted.bam.bai" {:genome "hg38", :sample "A"}] [:samtools-index {:genome "hg38", :sample "A"}]]
;;           [["bam/sorted.bam.bai" {:genome "hg38", :sample "B"}] [:samtools-index {:genome "hg38", :sample "B"}]]
;;           [["bam/sorted.bam.bai" {:genome "hg19", :sample "A"}] [:samtools-index {:genome "hg19", :sample "A"}]]
;;           [["bam/sorted.bam.bai" {:genome "hg19", :sample "B"}] [:samtools-index {:genome "hg19", :sample "B"}]]
;;           [["bam/sorted.bam" {:genome "hg38", :sample "A"}] [:samtools-sort {:genome "hg38", :sample "A"}]]
;;           [["bam/sorted.bam" {:genome "hg38", :sample "B"}] [:samtools-sort {:genome "hg38", :sample "B"}]]
;;           [["bam/sorted.bam" {:genome "hg19", :sample "A"}] [:samtools-sort {:genome "hg19", :sample "A"}]]
;;           [["bam/sorted.bam" {:genome "hg19", :sample "B"}] [:samtools-sort {:genome "hg19", :sample "B"}]]
;;           [["bwa-map.bam" {:genome "hg38", :sample "A"}] [:bwa-map {:genome "hg38", :sample "A"}]]
;;           [["bwa-map.bam" {:genome "hg38", :sample "B"}] [:bwa-map {:genome "hg38", :sample "B"}]]
;;           [["bwa-map.bam" {:genome "hg19", :sample "A"}] [:bwa-map {:genome "hg19", :sample "A"}]]
;;           [["bwa-map.bam" {:genome "hg19", :sample "B"}] [:bwa-map {:genome "hg19", :sample "B"}]])

;;         "/mnt/work")

;;        =>

;;        {["all.vcf" {:genome "hg19"}] "/mnt/work/bcftools-call/hg19/all.vcf"
;;         ["all.vcf" {:genome "hg38"}] "/mnt/work/bcftools-call/hg38/all.vcf"
;;         ["bam/sorted.bam" {:genome "hg19" :sample "A"}] "/mnt/work/samtools-sort/hg19/A/bam/sorted.bam"
;;         ["bam/sorted.bam" {:genome "hg19" :sample "B"}] "/mnt/work/samtools-sort/hg19/B/bam/sorted.bam"
;;         ["bam/sorted.bam" {:genome "hg38" :sample "A"}] "/mnt/work/samtools-sort/hg38/A/bam/sorted.bam"
;;         ["bam/sorted.bam" {:genome "hg38" :sample "B"}] "/mnt/work/samtools-sort/hg38/B/bam/sorted.bam"
;;         ["bam/sorted.bam.bai" {:genome "hg19" :sample "A"}] "/mnt/work/samtools-index/hg19/A/bam/sorted.bam.bai"
;;         ["bam/sorted.bam.bai" {:genome "hg19" :sample "B"}] "/mnt/work/samtools-index/hg19/B/bam/sorted.bam.bai"
;;         ["bam/sorted.bam.bai" {:genome "hg38" :sample "A"}] "/mnt/work/samtools-index/hg38/A/bam/sorted.bam.bai"
;;         ["bam/sorted.bam.bai" {:genome "hg38" :sample "B"}] "/mnt/work/samtools-index/hg38/B/bam/sorted.bam.bai"
;;         ["bwa-map.bam" {:genome "hg19" :sample "A"}] "/mnt/work/bwa-map/hg19/A/bwa-map.bam"
;;         ["bwa-map.bam" {:genome "hg19" :sample "B"}] "/mnt/work/bwa-map/hg19/B/bwa-map.bam"
;;         ["bwa-map.bam" {:genome "hg38" :sample "A"}] "/mnt/work/bwa-map/hg38/A/bwa-map.bam"
;;         ["bwa-map.bam" {:genome "hg38" :sample "B"}] "/mnt/work/bwa-map/hg38/B/bwa-map.bam"})




;; (def ds [{"bam/sorted.bam" ["/mnt/work/samtools-sort/hg19/A/bam/sorted.bam"]}
;;          {"bam/sorted.bam" ["/mnt/work/samtools-sort/hg19/B/bam/sorted.bam"]}
;;          {"bam/sorted.bam.bai" ["/mnt/work/samtools-index/hg19/A/bam/sorted.bam.bai"]}
;;          {"bam/sorted.bam.bai" ["/mnt/work/samtools-index/hg19/B/bam/sorted.bam.bai"]}])


;; (def outfiles
;;   {["all.vcf" {:genome "hg19"}] "/mnt/work/bcftools-call/hg19/all.vcf"})


;; (def infiles
;;   {["bam/sorted.bam" {:genome "hg19", :sample "A"}] "/mnt/work/samtools-sort/hg19/A/bam/sorted.bam",
;;    ["bam/sorted.bam" {:genome "hg19", :sample "B"}] "/mnt/work/samtools-sort/hg19/B/bam/sorted.bam",
;;    ["bam/sorted.bam.bai" {:genome "hg19", :sample "A"}] "/mnt/work/samtools-index/hg19/A/bam/sorted.bam.bai",
;;    ["bam/sorted.bam.bai" {:genome "hg19", :sample "B"}] "/mnt/work/samtools-index/hg19/B/bam/sorted.bam.bai"})



;; (facts "about `files-as-in-rule`"
;;        (files-as-in-rule infiles ["bam/sorted.bam" "bam/sorted.bam.bai"])

;;        =>

;;        [["/mnt/work/samtools-sort/hg19/A/bam/sorted.bam"
;;          "/mnt/work/samtools-sort/hg19/B/bam/sorted.bam"]
;;         ["/mnt/work/samtools-index/hg19/A/bam/sorted.bam.bai"
;;          "/mnt/work/samtools-index/hg19/B/bam/sorted.bam.bai"]]

;;        (files-as-in-rule
;;         {["quals.tsv" {:genome "hg38"}] "/mnt/work/plot-quals/hg38/quals.tsv"
;;          ["quals.svg" {:genome "hg38"}] "/mnt/work/plot-quals/hg38/quals.svg"}
;;         {:plot "quals.svg" :data "quals.tsv"})

;;        =>

;;        {:plot ["/mnt/work/plot-quals/hg38/quals.svg"]
;;         :data ["/mnt/work/plot-quals/hg38/quals.tsv"]})



;; (facts "about `render-code`"
;;        (render-code

;;         "samtools mpileup -g -f {file.genome} {in.0} | bcftools call -mv - > {out}"

;;         {:file {:genome ["data/hg19/genome.fa"]}
;;          :in [["/mnt/work/samtools-sort/hg19/A/bam/sorted.bam"
;;               "/mnt/work/samtools-sort/hg19/B/bam/sorted.bam"]
;;              ["/mnt/work/samtools-index/hg19/A/bam/sorted.bam.bai"
;;               "/mnt/work/samtools-index/hg19/B/bam/sorted.bam.bai"]],
;;         :out [["/mnt/work/bcftools-call/hg19/all.vcf"]],
;;         :params {}}

;;         )

;;        =>

;;        "samtools mpileup -g -f data/hg19/genome.fa /mnt/work/samtools-sort/hg19/A/bam/sorted.bam /mnt/work/samtools-sort/hg19/B/bam/sorted.bam | bcftools call -mv - > /mnt/work/bcftools-call/hg19/all.vcf")


;; (facts "about `fill-code`"

;;        (fill-code (snakemake-rules :bcftools-call) infiles outfiles {:genome "hg19"} {:genome ["data/hg19/genome.fa"]} {})

;;        =>

;;        {:shell "samtools mpileup -g -f data/hg19/genome.fa /mnt/work/samtools-sort/hg19/A/bam/sorted.bam /mnt/work/samtools-sort/hg19/B/bam/sorted.bam | bcftools call -mv - > /mnt/work/bcftools-call/hg19/all.vcf"})
