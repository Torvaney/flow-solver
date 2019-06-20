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
  "Enumerate all the possible colours that an edge could have"
  [colours edge]
  (->> (map #(edge->sat edge %) colours) (cons (edge->sat edge))))


(defn one-hot
  "One-hot encodes a variable as a SAT expression"
  [variants]
  (->> variants
       combo/permutations
       (mapv #(apply sat/AND (first %) (mapv sat/negate (rest %))))
       (apply sat/OR)))


(defn one-hot-edge-colour
  "One-hot encodes an edge's colour"
  [colours edge]
  (->> edge (edge-colours colours) one-hot))


(defn edges-have-only-one-colour
  [colours g]
  (->> (uber/edges g) (mapv #(one-hot-edge-colour colours %)) (apply sat/AND)))


(defn edges-such-that
  "Find all possible edge combinations that satisfy predicate function p"
  [p all-colours edges]
  (let [possible-edges    (map #(edge-colours all-colours %) edges)
        edge-combinations (apply combo/cartesian-product possible-edges)]
    (->> edge-combinations
         (filter p)
         (mapv #(apply sat/AND %))
         one-hot)))


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


(defn node-has-valid-connections
  "Creates a SAT expression for the edges connected to a given node.
   Each terminal (coloured) node must have exactly one coloured edge.
   Each connector node must have exactly two coloured edges (of the same colour)"
  [colours g node]
  (let [edges (uber/find-edges g {:src node})]
    (if-let [colour (get-node-colour g node)]
      (edges-such-that #(exactly-one-colour colour %) colours edges)
      (edges-such-that   exactly-two-colours          colours edges))))


(defn nodes-have-valid-connections
  [colours g]
  (->> g uber/nodes (mapv #(node-has-valid-connections colours g %)) (apply sat/AND)))


(defn graph->sat
  "Convert a graph to a SAT expression"
  [g]
  (let [colours (get-graph-colours g)]
     (sat/AND
      (edges-have-only-one-colour colours g)
      (nodes-have-valid-connections colours g))))


(defn sat->edge
  "Converts an edge to a SAT symbol"
  [{nodes :<-> colour :colour :as edge}]
  [(first nodes) (second nodes) {:color colour}])


(defn sat->graph
  "Convert a solved SAT into a graph"
  [solution]
  (->> solution
       (filter sat/positive?)
       (mapv sat->edge)
       (apply uber/graph)))
