(ns gad2.instantiate-rule
  (:require
   [clojure.string :as str]
   [clojure.set :as set])
  (:use [selmer.parser]))


(defn jobs-to-paths
  [jobgraph rules])


(defn filetarget-to-path
  [[[f xs] [rule _]] prefix]
  (let [ks (sort (keys xs))
        vs (map xs ks)]
    (str/join "/" (concat [prefix (name rule)] vs [f]))))



(defn filetargets-to-path [targets prefix]
  (zipmap (map first targets) (map #(filetarget-to-path % prefix) targets)))



(defn filemap
  [files-to-path template]
  (apply merge-with into (for [[k v] template] {k (files-to-path v)})))



(defn filelist
  [files-to-path template]
  (vec (for [f template] (files-to-path f))))


(defn files-as-in-rule
  [targets-to-path template]
  (let [_ [targets-to-path template]
        files-to-path (for [[[f _] p] targets-to-path] {f [p]})
        files-to-path (apply merge-with into files-to-path)
        template (if (string? template) [template] template)]
    (if (map? template)
      (filemap files-to-path template)
      (filelist files-to-path template))))



(defn render-code
  "TODO: the transformations of the code-templates need to be run only once. Perhaps in the defmacro?"
  [code d]
  (let [template (-> code
                     (str/replace "{out}" "{outfiles-as-string}")
                     (str/replace "{in}" "{infiles-as-string}")
                     (str/replace "{" "{{")
                     (str/replace "}" "|join:\" \"}}"))
        d (merge {:outfiles-as-string (flatten (d :out)) :infiles-as-string (flatten (d :in))} d)
        template (render template d)]
    template))



(defn fill-code
  [rule intargets outtargets xs files params]
  (let [[codetype code] (first (select-keys rule [:shell :script]))
        infiles (files-as-in-rule intargets (rule :in))
        outfiles (files-as-in-rule outtargets (rule :out))
        template (str/replace (str/replace code "{" "{{") "}" "}}")
        code (render-code code {:in infiles :out outfiles :file files :params params})]
    {codetype code}))
