(ns gad2.parse-rulefiles
  (:import [java.io PushbackReader])
  (:require
   [clojure.java.io :as io]
   [gad2.state :refer [rules]]))

(defn handle-docs [& body]
  (if (string? (first body))
    (assoc (second body) :doc (first body))
    (first body)))

(defmacro defrule
  "TODO: test me."
  [name & body]
  `(do
     (let [kw-name# ~(keyword name)
           body# (assoc (handle-docs ~@body) :name kw-name#)
           body# (handle-docs body#)]
       (swap! rules assoc kw-name# (handle-docs body#))
       (def ~name body#))))

(defn read-all-string
  [file]
  (let [rdr (-> file io/file io/reader PushbackReader.)]
    (loop [forms []]
      (let [form (try (read-string rdr) (catch Exception e nil))]
        (print form)
        (if form
          (recur (conj forms form))
          forms)))))

(defn read-all
  [file]
  (let [rdr (-> file io/file io/reader PushbackReader.)]
    (loop [forms []]
      (let [form (try (read rdr) (catch Exception e nil))]
        (if form
          (recur (conj forms form))
          forms)))))

(defn parse-rule
  [[_ name & body]]
  (let [name (keyword name)]
    (if (= 2 (count body))
      (assoc (second body) :doc (first body) :name name)
      (assoc (first body) :name name))))

(defn read-rules
  [rule-file]
  (let [rules (read-all rule-file)]
    (into {}
          (for [rule rules
                :let [rulemap (parse-rule rule)
                      name (:name rulemap)]]
            [name rulemap]))))




;; also add an input-transform?

;; (defn read-wildcards
;;   [wildcards-file]
;;   (edn/read-string (slurp wildcards-file))
