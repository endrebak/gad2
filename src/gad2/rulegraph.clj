(ns gad2.rulegraph
  (:require
   [hashp.core]
   [gad2.helpers :refer [infile->rule outfile->rule add-dependency]]
   [com.stuartsierra.dependency :as dep]))

(defn rulegraph
  [rules]
  (let [in (infile->rule rules)
        out (outfile->rule rules)
        r2r (for [[f r] in] [r (get out f)])]
    (reduce add-dependency (dep/graph) r2r)))
