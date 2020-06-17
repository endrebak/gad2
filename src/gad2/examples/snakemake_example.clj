(ns gad2.examples.snakemake-example
  (:require
   [hashp.core]
   [gad2.state :as state]
   [gad2.parse-workflow :refer [defrule]]))


(def streams
  {[:genome] ["hg19" "hg38"]})

(def xs [{:genome "hg19" :sample "A"}
         {:genome "hg19" :sample "B"}
         {:genome "hg38" :sample "A"}
         {:genome "hg38" :sample "B"}
         ;; {:genome "hg38" :sample "C"}
         ])




(defrule bwa-map
  "Map DNA sequences against a reference genome with BWA."
  {:x [:sample :genome]
   :file [:genome :fastq]
   :out "bwa-map.bam"
   :threads 8
   :params {:rg "@RG\tID:{sample}\tSM:{sample}"}
   :shell "bwa mem -R '{params.rg}' {threads} {ext.genome} {ext.fastq} | samtools view -Sb - > {out}"})


(defrule samtools-sort
  "Sort the bams."
  {:x [:sample :genome]
   :in "bwa-map.bam"
   :out "bam/sorted.bam"
   :shell "samtools sort -T {x.sample} -O bam {in} > {out}"})


(defrule samtools-index
  "Index read alignments for random access."
  {:x [:sample :genome]
   :in "bam/sorted.bam"
   :out "bam/sorted.bam.bai"
   :shell "samtools index {in}"})


(defrule bcftools-call
  "Aggregate mapped reads from all samples and jointly call genomic variants."
  {:in ["bam/sorted.bam" "bam/sorted.bam.bai"]
   :out "all.vcf"
   :x [:genome]
   :file :genome
   :shell "samtools mpileup -g -f {file.genome} {in.0} | bcftools call -mv - > {out}"})


(defrule plot-quals
  {:in "all.vcf"
   :x [:genome]
   :out {:plot "quals.svg" :data "quals.tsv"}
   :shell "plot {in} -o {out.0}"
   :script "plot-quals.py"})

;; #p state/rules

(def snakemake-rules {:bcftools-call {:doc "Aggregate mapped reads from all samples and jointly call genomic variants.",
                                      :file :genome,
                                      :in ["bam/sorted.bam" "bam/sorted.bam.bai"],
                                      :name :bcftools-call,
                                      :out "all.vcf",
                                      :shell "samtools mpileup -g -f {file.genome} {in.0} | bcftools call -mv - > {out}",
                                      :x [:genome]},
                      :bwa-map {:doc "Map DNA sequences against a reference genome with BWA.",
                                :file [:genome :fastq],
                                :name :bwa-map,
                                :out "bwa-map.bam",
                                :params {:rg "@RG\tID:{sample}\tSM:{sample}"},
                                :shell "bwa mem -R '{params.rg}' {threads} {ext.genome} {ext.fastq} | samtools view -Sb - > {out}",
                                :threads 8,
                                :x [:sample :genome]},
                      :plot-quals {:in "all.vcf",
                                   :name :plot-quals,
                                   :out {:data "quals.tsv", :plot "quals.svg"},
                                   :script "plot-quals.py",
                                   :shell "plot {in} -o {out.0}",
                                   :x [:genome]},
                      :samtools-index {:doc "Index read alignments for random access.",
                                       :in "bam/sorted.bam",
                                       :name :samtools-index,
                                       :out "bam/sorted.bam.bai",
                                       :shell "samtools index {in}",
                                       :x [:sample :genome]},
                      :samtools-sort {:doc "Sort the bams.",
                                      :in "bwa-map.bam",
                                      :name :samtools-sort,
                                      :out "bam/sorted.bam",
                                      :shell "samtools sort -T {x.sample} -O bam {in} > {out}",
                                      :x [:sample :genome]}})
