(ns gad2.core
  (:gen-class)
  (:require [gad2.jobgraph]
            [gad2.server :as server]
            [ring.adapter.jetty :as jetty]))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (jetty/run-jetty server/handler {:port 3000}))
