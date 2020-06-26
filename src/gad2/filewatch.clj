(ns gad2.filewatch
  (:require
   [hawk.core :as hawk]
   [gad2.state :as state]))


;; need watchers:
;;   - workflow-files (rules, wildcards, configfile)
;;   - the datafiles the workflow needs or can produce


;; (def watcher
;;   (hawk/watch! [{:paths ["."]
;;                  :handler (fn [ctx e]
;;                             (println "event: " e)
;;                             (println "context: " ctx)
;;                             ctx)}]))


;; (def config-watcher
;;   (hawk/watch! [{:paths ["/Users/endrebakkenstovner/code/gad2/src/gad2/filewatch.clj"]
;;                  :handler (fn [ctx e]
;;                             (println "event: " e)
;;                             (println "context: " ctx)
;;                             ctx)}]))




;; (defn update-config
;;   "Should lead to new rules, jobgraph, files (produced & code)"
;;   [config-map]
;;   (let [relevant-parts #p (select-keys config-map (keys @config))]
;;     (reset! @config relevant-parts)))


;; (defn stop [watcher]
;;   (hawk/stop! watcher))
