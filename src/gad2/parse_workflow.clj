(ns gad2.parse-workflow
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



(defn read-all
  [file]
  (let [rdr (-> file io/file io/reader PushbackReader.)]
    (loop [forms []]
      (let [form (try (read rdr) (catch Exception e nil))]
        (if form
          (recur (conj forms form))
          forms)))))


(defn workflow-to-rules []) ; must take in rulefile, parse it, eval it
