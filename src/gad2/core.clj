(ns gad2.core
  (:gen-class)
  (:use [hashp.core])
  (:require [gad2.jobgraph]
            [gad2.server :as server]
            [gad2.state :as state]
            [gad2.nrepl :refer [start-nrepl]]
            [gad2.parse-rulefiles :refer [defrule]]
            [hawk.core :as hawk]
            ;; [gad2.filewatch :refer [watcher]]
            [clojure.edn :as edn]
            [ring.adapter.jetty :as jetty]
            [clojure.java.io :as io]))


(defn read-from-file-with-trusted-contents [filename]
  (with-open [r (java.io.PushbackReader.
                 (clojure.java.io/reader filename))]
    (binding [*read-eval* false]
      (read r))))


(defn read-args
  [args]
  (into {} (for [[k v] (partition 2 args)] [(keyword k) v])))

(defn absolute-path
  [path]
  (.getAbsolutePath (io/as-file path)))

;; {:readers {'path }}

(defn config-files
  [args]
  (let [config-file (args :config-file)
        config (edn/read-string (slurp config-file))
        config-files (mapv config [:rule-file :wildcard-file])]
    (conj config-files config-file)))


(def watchers (atom {}))


(defn handler [ctx {:keys [file kind]}]
  ;; (println "event: " e)
  (println (str file " " kind))
  (println "context: " ctx)
  ctx)


(defn add-watcher [key paths handler]
  (swap! watchers assoc key
         (hawk/watch! [{:paths paths
                        :handler handler}])))

;; if

;; update-config-paths
;; collect-rules
;; create-rulegraph
;; read-wildcards
;; create jobgraph


; https://docs.oracle.com/javase/8/docs/

;; (defn )

(defn config-handler [ctx e]
  ctx)


(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (let [config-paths (config-files (read-args args))]
    (add-watcher :config-watcher config-paths handler)))
