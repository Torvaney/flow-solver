(ns flow-solver.core
  (:require [flow-solver.graph :as graph]
            [flow-solver.sat :as flow-sat]
            [clojure.edn :as edn]
            [clojure.java.io :as io]
            [rolling-stones.core :as sat]
            [ubergraph.core :as uber]))


(defn solve
  [init]
  (->> init 
       flow-sat/graph->sat 
       sat/solve-symbolic-formula 
       (flow-sat/sat->graph init)))


(defn -main
  [map-file & args]
  (let [init   (-> map-file slurp edn/read-string graph/init-graph)
        solved (solve init)]
    (do (graph/draw init   {:save {:filename (str (io/resource "output") "/before.png")
                                   :format   :png}})
        (graph/draw solved {:save {:filename (str (io/resource "output") "/after.png")
                                   :format   :png}}))))
