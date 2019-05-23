(ns flow-solver.core
  (:require [flow-solver.graph :as graph]
            [clojure.pprint]))


(def example-nodes
  ; Looks like
  ; - - o
  ; - o x
  ; x - -
  [[:blue #{[2 0] [1 1]}]
   [:red  #{[2 1] [0 2]}]])


(defn draw-graph
  "Draw a graph"
  [x]
  (println x "Hello, World!"))


(defn -main
  []
  (clojure.pprint/pprint (graph/square-graph 3)))
