(ns flow-solver.graph
  (:require [clojure.math.combinatorics :as combo]
            [clojure.pprint]
            [ubergraph.core :as uber]))


(defn square-nodes
  "Create a set of nodes (coordinates) on a square, cartesian coordinate system"
  [dim]
  (->> (combo/cartesian-product (range dim) (range dim)) (mapv vec)))


(defn connect-edges
  "Connect nodes to each other according to a predicate function.

   If (p node-1 node-2) is true, then an edge between them will be created"
  [p nodes]
  (->> (combo/combinations nodes 2)
       (filter #(apply p %))
       (mapv vec)))


(defn square?
  "Returns true two nodes if they are adjacent (not including diagonals). Else false."
  [[x1 y1] [x2 y2]]
  (or (and (= x1 x2)
           (<= -1 (- y2 y1) 1))
      (and (= y1 y2)
           (<= -1 (- x2 x1) 1))))


(defn square-graph
  "Create an empty square graph"
  [dim]
  (let [nodes (square-nodes dim)
        edges (connect-edges square? nodes)]
    (apply uber/graph edges)))


(defn replace-node
  [{:keys [nodes edges]} id node]
  {:nodes (assoc nodes id node)
   :edges edges})


(defn init-graph
  "Create a new graph from a graph spec"
  [{:keys [dim nodes]}]
  (apply uber/add-nodes-with-attrs (square-graph dim) nodes))


(defn draw
  "Draw a graph"
  [graph]
  ;; Set the Nodes' fill colour
  ;; Set the edges' colour
  ;; Set the background colour
  (uber/viz-graph graph {:layout :neato}))
