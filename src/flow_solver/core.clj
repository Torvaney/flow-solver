(ns flow-solver.core
(:require [flow-solver.graph :as graph]
          [flow-solver.sat :as flow-sat]
          [clojure.edn :as edn]
          [clojure.java.io :as io]
          [rolling-stones.core :as sat]
          [ubergraph.core :as uber]))


(defn -main
  [map-file & args]
  (-> map-file slurp edn/read-string
      graph/init-graph
      (graph/draw {:save {:filename (str (io/resource "output") "/before.png")
                          :format   :png}})
      flow-sat/graph->sat
      sat/solve-symbolic-formula
      flow-sat/sat->graph
      (graph/draw {:save {:filename (str (io/resource "output") "/after.png")
                          :format   :png}})))
