(ns gad2.examples.ldivide
  (:require [gad2.parse-workflow :refer [defrule]]))


(def wildcards
  [{:chromosome "chr21" :genome "hg38" :population "CEU"}
   {:chromosome "chr21" :genome "hg38" :population "YRI"}])


(def files
  {"sample-info" "data/sample_info.tsv"})


(defrule fetch-variants
  {:wildcards [:chromosome :genome]
   :output "1kg/chromosome.vcf.gz"
   :script "axel https://ftp.1000genomes.ebi.ac.uk/vol1/ftp/release/20130502/ALL.chromosome.phase3_shapeit2_mvncall_integrated_v5a.20130502.genotypes.vcf.gz -q -o {out}"})


(defrule index-variants
  {:wildcards [:chromosome :genome]
   :input "1kg/chromosome.vcf.gz"
   :out "1kg/chromosome.vcf.gz.tbi"
   :shell "tabix {input}"})


(defrule variant-samples
  {:wildcards [:chromosome :genome]
   :input ["1kg/chromosome.vcf.gz" "1kg/chromosome.vcf.tbi"]
   :output "variant_samples.tsv"
   :shell "bcftools view -h {input.0} | tail -1 | tr '\t' '\n' | tail -n +10 > {output}"})


(defrule individuals-in-reference-panel
  {:wildcards [:chromosome :genome :population]
   :input "variant_samples.tsv"
   :output {:number-individuals "number_individuals.tsv" :samples "samples.tsv"}
   :files "sample-info"
   :script "scripts/individuals_in_reference_panel.py"})


(defrule subset-vcf-on-population
  {:wildcards [:chromosome :genome :population]
   :input {:samples "samples.tsv"
           :variants "1kg/chromosome.vcf.gz"
           :index "1kg/chromosome.vcf.gz.tbi"}
   :output "population_subset_vcf.pq"
   :shell "bcftools view --threads 48 --force-samples -S {inputput.samples} {inputput.variants} |
           bcftools query -f '%POS\t[%GT\t]\n' |
           tr '|' '\t' |
           python {scripts.to_parquet} {output} {input.samples}"})


;; (defrule theta
;;   {:wildcards [:chromosome :genome :population]
;;    })
