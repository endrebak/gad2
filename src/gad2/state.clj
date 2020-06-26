(ns gad2.state
  (:require [hashp.core]
            [com.stuartsierra.dependency :as dep]))


(def rules (atom {}))


(def config (atom {:rule-file ""
                   :wildcard-config ""
                   :wildcard-file ""
                   :config-file ""}))

;; (def dag (atom #com.stuartsierra.dependency.MapDependencyGraph {}))
