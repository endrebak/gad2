;; (ns gad2.core
;;   (:gen-class)
;;   (:use [hashp.core])
;;   (:require [gad2.jobgraph]
;;             ;; [gad2.server :as server]
;;             [gad2.rulegraph :as rg]
;;             [gad2.jobgraph :as jg]
;;             [gad2.state :as state]
;;             [gad2.nrepl :refer [start-nrepl]]
;;             [gad2.parse-rulefiles :refer [read-rules]]
;;             [hawk.core :as hawk]
;;             [nrepl.server :refer [start-server stop-server]]
;;             ;; [gad2.filewatch :refer [watcher]]
;;             [clojure.edn :as edn]
;;             [ring.adapter.jetty :as jetty]
;;             [clojure.java.io :as io]))


;; (defn read-args
;;   [args]
;;   (into {} (for [[k v] (partition 2 args)] [(keyword k) v])))


;; (defn absolute-path
;;   [path]
;;   (.getAbsolutePath (io/as-file path)))


;; (defn config-files
;;   [config-file]
;;   (let [config (edn/read-string (slurp config-file))
;;         config-files (mapv config [:rule-file :wildcards-file])]
;;     (conj config-files config-file)))


;; (def watchers (atom {}))

;; (def config-file (atom ""))


;; (defn add-watcher [key paths handler]
;;   (swap! watchers assoc key
;;          (hawk/watch! [{:paths paths
;;                         :handler handler}])))


;; (declare config-handler)


;; (defn reset-watcher
;;   []
;;   (let []
;;     (hawk/stop! (@watchers :config-watcher))
;;     (add-watcher :config-watcher (config-files @config-file) config-handler)))


;; (defn jobgraph-from-config
;;   [config-file]
;;   (let [config-map (edn/read-string (slurp config-file))
;;         rule-file (config-map :rule-file)
;;         wildcards-file (config-map :wildcards-file)
;;         rules (read-rules rule-file)
;;         wildcards (edn/read-string (slurp wildcards-file))
;;         rulegraph (rg/rulegraph rules)]
;;         (jg/jobgraph rulegraph rules wildcards)))


;; ;; update-config-paths
;; ;; collect-rules
;; ;; create-rulegraph
;; ;; read-wildcards
;; ;; create jobgraph


;; (defn config-handler [ctx {:keys [file kind]}]
;;   (reset-watcher)
;;   (jobgraph-from-config @config-file))


;; (defn -main
;;   "I don't do a whole lot ... yet."
;;   [& args]
;;   (let [argmap (read-args args)]

;;     (reset! config-file (argmap :config-file))

;;     (add-watcher :config-watcher (config-files @config-file) config-handler)

;;     (server/start!)))
