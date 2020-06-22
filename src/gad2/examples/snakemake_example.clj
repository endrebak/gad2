(ns gad2.examples.snakemake-example
  (:require
   [hashp.core]
   [gad2.state :as state]
   [gad2.parse-workflow :refer [defrule]]))


(def streams
  {[:genome] ["hg19" "hg38"]})

(def wildcards [{:genome "hg19" :sample "A"}
                {:genome "hg19" :sample "B"}
                {:genome "hg38" :sample "A"}
                {:genome "hg38" :sample "B"}
                {:genome "hg38" :sample "C"}
                ])




(defrule bwa-map
  "Map DNA sequences against a reference genome with BWA."
  {:wildcards [:sample :genome]
   :file [:genome :fastq]
   :output "bwa-map.bam"
   :threads 8
   :params {:rg "@RG\tID:{sample}\tSM:{sample}"}
   :shell "bwa mem -R '{params.rg}' {threads} {ext.genome} {ext.fastq} | samtools view -Sb - > {out}"})


(defrule samtools-sort
  "Sort the bams."
  {:wildcards [:sample :genome]
   :input "bwa-map.bam"
   :output "bam/sorted.bam"
   :shell "samtools sort -T {x.sample} -O bam {in} > {out}"})


(defrule samtools-index
  "Index read alignments for random access."
  {:wildcards [:sample :genome]
   :input "bam/sorted.bam"
   :output "bam/sorted.bam.bai"
   :shell "samtools index {in}"})


(defrule bcftools-call
  "Aggregate mapped reads from all samples and jointly call genomic variants."
  {:input ["bam/sorted.bam" "bam/sorted.bam.bai"]
   :output "all.vcf"
   :wildcards [:genome]
   :file :genome
   :shell "samtools mpileup -g -f {file.genome} {in.0} | bcftools call -mv - > {out}"})


(defrule plot-quals
  {:input "all.vcf"
   :wildcards [:genome]
   :output {:plot "quals.svg" :data "quals.tsv"}
   :shell "plot {in} -o {out.0}"
   :script "plot-quals.py"})

;; #p state/rules

(def snakemake-rules {:bcftools-call {:doc "Aggregate mapped reads from all samples and jointly call genomic variants.",
                                      :file :genome,
                                      :input ["bam/sorted.bam" "bam/sorted.bam.bai"],
                                      :name :bcftools-call,
                                      :output "all.vcf",
                                      :shell "samtools mpileup -g -f {file.genome} {in.0} | bcftools call -mv - > {out}",
                                      :wildcards [:genome]},
                      :bwa-map {:doc "Map DNA sequences against a reference genome with BWA.",
                                :file [:genome :fastq],
                                :name :bwa-map,
                                :output "bwa-map.bam",
                                :params {:rg "@RG\tID:{sample}\tSM:{sample}"},
                                :shell "bwa mem -R '{params.rg}' {threads} {ext.genome} {ext.fastq} | samtools view -Sb - > {out}",
                                :threads 8,
                                :wildcards [:sample :genome]},
                      :plot-quals {:input "all.vcf",
                                   :name :plot-quals,
                                   :output {:data "quals.tsv", :plot "quals.svg"},
                                   :script "plot-quals.py",
                                   :shell "plot {in} -o {out.0}",
                                   :wildcards [:genome]},
                      :samtools-index {:doc "Index read alignments for random access.",
                                       :input "bam/sorted.bam",
                                       :name :samtools-index,
                                       :output "bam/sorted.bam.bai",
                                       :shell "samtools index {in}",
                                       :wildcards [:sample :genome]},
                      :samtools-sort {:doc "Sort the bams.",
                                      :input "bwa-map.bam",
                                      :name :samtools-sort,
                                      :output "bam/sorted.bam",
                                      :shell "samtools sort -T {x.sample} -O bam {in} > {out}",
                                      :wildcards [:sample :genome]}})
