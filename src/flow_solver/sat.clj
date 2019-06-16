(ns flow-solver.sat
  (:require [flow-solver.graph :as graph]
            [clojure.math.combinatorics :as combo]
            [rolling-stones.core :as sat]
            [ubergraph.core :as uber]))


(defn create-node
  "Create a new node from coordinates and (optionally) attributes."
  ([coords]
   (create-node coords nil))
  ([[x y :as coords] attrs]
   (if attrs
     [coords attrs]
     [coords])))


(defn attach-attrs
  "Attach attributes to a coll of nodes"
  [g nodes]
  (mapv #(create-node % (get (:attrs g) %)) nodes))


(defn nodes-with-attrs
  [g]
  (->> (uber/nodes g) (attach-attrs g)))


(defn connected-nodes
  "Get any nodes connected by an edge to a given node."
  [g [coord colour :as node]]
  (->> (uber/successors g coord) (attach-attrs g)))


(defn possible-node-colours
  "Generate a vector of nodes enumerating all the possible colours that a node could be."
  [colours [coord attrs :as node]]
  (if attrs
    [node]
    (mapv #(create-node coord {:color %}) colours)))


(defn one-hot
  "One-hot encodes a variable as a SAT expression"
  [variants]
  (->> variants
       combo/permutations
       (map #(apply sat/AND (first %) (map sat/negate (rest %))))
       (apply sat/OR)))


(defn one-hot-node-colour
  "Creates a SAT expression for the edges connected to all nodes."
  [colours node]
  (->> node (possible-node-colours colours) one-hot))


(defn node-colours
  "Creates a SAT expression asserting that each node should have exactly one colour"
  [colours g]
  (->> (nodes-with-attrs g)
       (map #(one-hot-node-colour colours %))
       (apply sat/AND)))


(defn count-nodes-of-colour
  "Counts the number of nodes of a given colour"
  [colour nodes]
  (->> nodes (filter #(= (:color (second %)) colour)) count))


(defn connected-to-n-of-same-colour
  "Creates a SAT expression asserting that a given node is part of a valid pipe of a specific colour.

   This means checking that the correct number of adjacent nodes are the same colour as the
   node in question.

   Correct number of nodes:
     * 1 for terminal nodes
     * 2 for connector nodes"
  [colours n g [_ {colour :color} :as node]]
  (or (some->> (connected-nodes g node)
               (map #(possible-node-colours colours %))
               (apply combo/cartesian-product)
               (filter #(<= n (count-nodes-of-colour colour %)))
               seq
               (map #(apply sat/AND node %))
               (apply sat/OR))
      ;; If there are no valid combinations of neighbours, then the node cannot exist
      ;; with the colour supplied
      (sat/negate node)))


(defn node-in-pipe
  "Creates a SAT expression asserting that a given node is part of a valid pipe of 
   any colour by comparing it to neighbouring nodes' colours."
  [colours g node]
  (if-let [colour (second node)]
    (connected-to-n-of-same-colour colours 1 g node)
    (->> (possible-node-colours colours node)
         (map #(connected-to-n-of-same-colour colours 2 g %))
         (apply sat/OR))))


(defn nodes-in-pipes
  "Creates a SAT expression asserting that each node is part of a valid pipe
   by comparing it to neighbouring nodes' colours."
  [colours g]
  (->> (nodes-with-attrs g)
       (map #(node-in-pipe colours g %))
       (apply sat/AND)))


(defn graph->sat
  "Convert a graph to a SAT expression"
  [g]
  (let [colours (->> g :attrs vals (map :color) distinct)]
    (sat/AND
     (node-colours colours g)
     (nodes-in-pipes colours g))))


(defn sat->graph
  "Convert a solved SAT into a graph"
  [init solution]
  (->> solution
       (filter sat/positive?)
       (apply uber/add-nodes-with-attrs init)))
