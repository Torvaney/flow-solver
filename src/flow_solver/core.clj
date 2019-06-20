(ns flow-solver.core
  (:require [flow-solver.graph :as graph]
            [flow-solver.sat :as flow-sat]
            [clojure.edn :as edn]
            [clojure.java.io :as io]
            [rolling-stones.core :as sat]
            [ubergraph.core :as uber]))


(def solve
  (comp flow-sat/sat->graph
        sat/solve-symbolic-formula
        flow-sat/graph->sat))


(defn -main
  [map-file & args]
  ;; There must be a more elegant way than this...?
  (let [map-edn (-> map-file slurp edn/read-string)
        init    (graph/init-graph map-edn)
        solved  (future (solve init))]
    (do 
      (println "Attempting to solve a " (:dim map-edn) "-by-" (:dim map-edn) " puzzle...")
      (println (if @solved "Solved!" "No solution found. Are you _sure_ you gave the right map?"))
      (println "Attempting to save before/after images...")

      (graph/draw init    {:save {:filename (str (io/resource "output") "/before.png")
                                  :format   :png}})
      (graph/draw @solved {:save {:filename (str (io/resource "output") "/after.png")
                                  :format   :png}})

      (println "Done!"))))
