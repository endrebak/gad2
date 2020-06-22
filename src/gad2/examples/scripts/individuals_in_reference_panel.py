import pandas as pd

df = pd.read_table(in[0])
in_vcf = pd.read_table(files["sample-info"], header=None, squeeze=True).to_list()
print(in_vcf[:5])

# want number of individuals in the population
pop = df[(df.Population.isin(config["populations"])) & (df.Sample.isin(in_vcf))]

pop_size = len(pop.drop_duplicates("Sample"))

with open(output["number-individuals"], "w+") as o:
    o.write(str(pop_size) + "\n")

pop.Sample.to_frame().to_csv(output["samples"], index=False, sep="\t", header=False)
