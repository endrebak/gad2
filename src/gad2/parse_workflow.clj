(ns gad2.parse-workflow
  (:require
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
