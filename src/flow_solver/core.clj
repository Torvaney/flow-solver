(ns flow-solver.core
(:require [flow-solver.graph :as graph]
          [flow-solver.sat :as flow-sat]
          [rolling-stones.core :as sat]
          [ubergraph.core :as uber]))


(def example-2x2
; Looks like
; x o
; x o
  {:dim   2
   :nodes [[[0 0] {:color :blue}]
           [[0 1] {:color :blue}]
           [[1 1] {:color :red}]
           [[1 0] {:color :red}]]})


(def example-3x3
; Looks like
; - - o
; - o x
; x - -
  {:dim   3
   :nodes [[[2 0] {:color :blue}]
           [[1 1] {:color :blue}]
           [[2 1] {:color :red}]
           [[0 2] {:color :red}]]})


(def example-hard
  {:dim   9
   :nodes [[[6 0] {:color :red}]
           [[7 0] {:color :cyan}]
           [[8 0] {:color :darkgreen}]
           [[1 1] {:color :blue}]
           [[0 2] {:color :brown}]
           [[0 2] {:color :red}]
           [[3 3] {:color :cyan}]
           [[1 4] {:color :darkgreen}]
           [[7 4] {:color :orange}]
           [[4 5] {:color :pink}]
           [[6 6] {:color :yellow}]
           [[8 6] {:color :yellow}]
           [[4 7] {:color :brown}]
           [[6 7] {:color :pink}]
           [[7 7] {:color :orange}]
           [[7 8] {:color :deeppink4}]
           [[0 8] {:color :blue}]
           [[5 8] {:color :deeppink4}]]})


(defn -main
  []
  (let [g (graph/init-graph example-hard)]
    (graph/draw g)
    (->> g 
         flow-sat/graph->sat 
         sat/solve-symbolic-formula
         flow-sat/sat->graph
         graph/draw)))
