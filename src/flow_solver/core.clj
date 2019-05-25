(ns flow-solver.core
(:require [flow-solver.graph :as graph]
          [flow-solver.sat :as flow-sat]
          [clojure.edn :as edn]
          [rolling-stones.core :as sat]
          [ubergraph.core :as uber]))


(defn -main
  [map-file & args]
  (-> map-file
      slurp
      edn/read-string
      graph/init-graph
      (doto graph/draw)
      flow-sat/graph->sat
      sat/solve-symbolic-formula
      flow-sat/sat->graph
      (doto graph/draw)))
