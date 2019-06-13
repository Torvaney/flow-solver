(ns flow-solver.sat
  (:require [clojure.math.combinatorics :as combo]
            [rolling-stones.core :as sat]
            [ubergraph.core :as uber]))


(defn get-graph-colours
  [g]
  (->> g :attrs vals (map :color) distinct))


(defn get-node-colour
  [g node]
  (-> g :attrs (get node) :color))


(defn edge->sat
  "Converts an edge to a SAT symbol"
  ([edge]
   {:<-> #{(uber/src edge) (uber/dest edge)}})
  ([edge colour]
   {:<-> #{(uber/src edge) (uber/dest edge)}
    :colour colour}))


(defn edge-colours
  [colours edge]
  (->> (map #(edge->sat edge %) colours) (cons (edge->sat edge))))


(defn edges-such-that
  "Find all possible edge combinations that satisfy predicate function p"
  [p all-colours edges]
  (let [possible-edges    (map #(edge-colours all-colours %) edges)
        edge-combinations (apply combo/cartesian-product possible-edges)]
    (->> edge-combinations
         (filter p)
         (map #(apply sat/AND %))
         (apply sat/OR))))


(defn exactly-one-colour
  "Each terminal (coloured) node must have exactly one coloured edge, of a
    pre-specified colour."
  [colour edges]
  (let [coloured-edges (keep :colour edges)]
    (and (= 1 (count coloured-edges))
         (= colour (first coloured-edges)))))


(defn exactly-two-colours
  "Each connector node must have exactly two coloured edges (of the same colour)"
  [edges]
  (let [edge-colours (keep :colour edges)]
    (and (= 2 (count edge-colours))
         (apply = edge-colours))))


(defn one-hot-edge-colour
  "One-hot encodes an edge's colour as a SAT expression"
  [colours edge]
  (->> (edge-colours colours edge)
       combo/permutations
       (map #(apply sat/AND (first %) (mapv sat/negate (rest %))))
       (apply sat/OR)))


(defn one-hot-edges
  "One-hot encodes a set of edges' colours as a SAT expression"
  [colours edges]
  (apply sat/AND (mapv #(one-hot-edge-colour colours %) edges)))


(defn node->sat
  "Creates a SAT expression for the edges connected to a given node.

   Each terminal (coloured) node must have exactly one coloured edge.
   Each connector node must have exactly two coloured edges (of the same colour)"
  [g node]
  (let [colours     (get-graph-colours g)
        edges       (uber/find-edges g {:src node})]
    (if-let [colour (get-node-colour g node)]
      (edges-such-that #(exactly-one-colour colour %) colours edges)
      (edges-such-that exactly-two-colours            colours edges))))


(defn nodes->sat
  "Create a SAT expression for the edges connected to all nodes."
  [g]
  (->> (uber/nodes g)
       ;; We have to use mapv to actualise the collection, here. 
       ;; If we just use map, only the first 2 nodes are used for some reason?
       ;; ¯\_(ツ)_/¯
       (mapv #(node->sat g %))
       (apply sat/AND)))


(defn graph->sat
  "Convert a graph to a SAT expression"
  [g]
  (let [colours (get-graph-colours g)]
    (sat/AND
     (one-hot-edges colours (uber/edges g))
     (nodes->sat g))))


(defn sat->edge
  "Converts an edge to a SAT symbol"
  [{nodes :<-> colour :colour}]
  [(first nodes) (second nodes) {:color colour}])


(defn sat->graph
  "Convert a solved SAT into a graph"
  [solution]
  (->> solution
       (filter sat/positive?)
       (mapv sat->edge)
       (apply uber/graph)))
