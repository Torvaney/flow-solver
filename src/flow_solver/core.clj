(ns flow-solver.core
(:require [flow-solver.graph :as graph]
          [flow-solver.sat :as flow-sat]
          [rolling-stones.core :as sat]
          [ubergraph.core :as uber]))


(def example-graph-spec-2
; Looks like
; x o
; x o
  {:dim   2
   :nodes [[[0 0] {:color :blue}]
           [[0 1] {:color :blue}]
           [[1 1] {:color :red}]
           [[1 0] {:color :red}]]})


(def example-graph-spec-3
; Looks like
; - - o
; - o x
; x - -
  {:dim   3
   :nodes [[[2 0] {:color :blue}]
           [[1 1] {:color :blue}]
           [[2 1] {:color :red}]
           [[0 2] {:color :red}]]})


(defn -main
  []
  (let [g (graph/init-graph example-graph-spec-3)]
    (graph/draw g)
    (->> g 
         flow-sat/graph->sat 
         sat/solve-symbolic-formula
         flow-sat/sat->graph
         graph/draw)))
