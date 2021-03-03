(defrule bwa-map
  "Map DNA sequences against a reference genome with BWA."
  {:wildcards [:sample :genome]
   :file [:genome :fastq]
   :output "bwa-map.bam"
   :threads 8
   :params {:rg "@RG\tID:{sample}\tSM:{sample}"}
   :shell "bwa mem -R '{params.rg}' {threads} {file.genome} {file.fastq} | samtools view -Sb - > {out}"})


(defrule samtools-sort
  "Sort the bams."
  {:wildcards [:sample :genome]
   :input "bwa-map.bam"
   :output "bam/sorted.bam"
   :shell "samtools sort -T {wildcards.sample} -O bam {in} > {out}"})


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
   :output ["quals.svg" "quals.tsv"]
   :shell "plot {in} -o {out.0}"
   :script "plot-quals.py"})
