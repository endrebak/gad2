(ns gad2.find-paths
  (:require [clojure.string :as str]
            [hashp.core]))

(defn jobs-to-outpath-stem [dependents]
  (into {}
        (for [rule dependents
              :when (-> rule first first keyword?)]
          (let [rulename (-> rule first first)
                wildcards (->> rule second first second)
                wildcards-as-strings (->> wildcards vec sort flatten (map name))
                parts (flatten [(name rulename) wildcards-as-strings])]
            [[rulename wildcards] (str/join "/" parts)]))))

(defn jobs-to-outpath-base [rules]
  (apply merge-with concat
         (for [[rulename rule] rules
               :let [rule-output (rule :output)
                     output (cond
                              (string? rule-output) [rule-output]
                              (map? rule-output) (vals rule-output)
                              :default rule-output)]
               :when (-> rule-output? #(not= % nil))]
           {rulename output})))

(defn merge-with-concat [& args]
  (apply merge-with concat args))

(defn jobs-to-outpath [rules jobgraph]
  (apply merge-with merge-with-concat
         (apply concat
                (let [dependents (:dependents jobgraph)
                      bases (jobs-to-outpath-base rules)
                      stems (jobs-to-outpath-stem dependents)]
                  (for [[job path] dependents
                        :let [rulename (first job)
                              stem (stems job)]
                        :when (keyword? rulename)]
                    (for [base (bases rulename)]
                      {job {base [(str/join "/" [stem base])]}}))))))
