(ns gad2.find-paths
  (:require [clojure.string :as str]))


(defn jobs-to-outpath-stem [jobgraph]
  (into {}
        (for [rule (:dependents jobgraph)
              :when (-> rule first first keyword?)]
          (let [rulename (-> rule first first)
                wildcards (->> rule second first second)
                wildcards-as-strings (->> wildcards vec sort flatten (map name))
                parts (flatten [(name rulename) wildcards-as-strings])]
            [[rulename wildcards] (str/join "/" parts)]))))

(defn jobs-to-outpath-base [rules]
  (into {}
        (for [[rulename rule] rules
              output (rule :output)]
          [rulename (-> rule :output)])))


(defn jobs-to-outpath [rules jobgraph]
  (into {}
        (let [bases (jobs-to-outpath-base rules)
              stems (jobs-to-outpath-stem jobgraph)]
          (for [[job path] (:dependents jobgraph)
                :let [rulename (first job)
                      base (bases rulename)
                      stem (stems job)]]
            [job (str/join "/" [stem base])]))))
