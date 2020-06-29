(ns gad2.core
  (:gen-class)
  (:require [gad2.jobgraph]
            [gad2.server :as server]
            [gad2.state :as state]
            [gad2.nrepl :refer [start-nrepl]]
            [gad2.parse-rulefiles :refer [defrule]]
            [hawk.core :as hawk]
            ;; [gad2.filewatch :refer [watcher]]
            [ring.adapter.jetty :as jetty]))


;; (defn jobgraph
;;   [rule-file wildcard-file]
;;   )



(defn read-from-file-with-trusted-contents [filename]
  (with-open [r (java.io.PushbackReader.
                 (clojure.java.io/reader filename))]
    (binding [*read-eval* false]
      (read r))))



(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (let [argmap (into {} (for [[k v] (partition 2 args)] [(keyword k) v]))
        config-files (vals (select-keys argmap [:rule-file :wildcard-file]))]

      (hawk/watch! [{:paths config-files
                     :handler (fn [ctx e]
                                (println "event: " e)
                                (println "context: " ctx)
                                ctx)}]))


    ;; (swap! state/config #(merge state/config (select-keys [:rule-file :wildcard-file] args))))
    )
  ; (jetty/run-jetty server/handler {:port 3000}))
