(ns gad2.server
  (:require [ring.adapter.jetty :as jetty]))


(defn handler [request]
  {:status 200
   :headers {"Content-Type" "text/plain"}
   :body "Hello Clojure, Hello Ring!"})




;; need to check file for changes
;; rerun upon change


;; should have file that defines

;; wildcard functions

;; workflow as an edn-file?

;; whenever one changes, update
