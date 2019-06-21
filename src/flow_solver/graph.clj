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
  "Returns true if two nodes are adjacent (not including diagonals). Else false."
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


(defn init-graph
  "Create a new graph from a graph spec"
  [{:keys [dim nodes]}]
  (->> nodes
       (map (fn [[xy attrs]] [xy (assoc attrs :node-type :terminal)]))
       (apply uber/add-nodes-with-attrs (square-graph dim))))


;; Display


(defn merge-attrs
  "Returns a node-or-edge with new attributes derived from f. Keeping any attributes 
   that already exist."
  [f g node-or-edge]
  (let [current  (uber/attrs g node-or-edge)
        new      (f g node-or-edge)
        combined (merge new current)]
    (if (uber/edge? node-or-edge)
      [(:src node-or-edge) (:dest node-or-edge) combined]
      [node-or-edge combined])))


(defn add-node-attrs
  [f g]
  (->> (uber/nodes g)
       (map #(merge-attrs f g %))
       (filter some?)
       (apply uber/add-nodes-with-attrs g)))


(defn add-edge-attrs
  [f g]
  (->> (uber/edges g)
       (map #(merge-attrs f g %))
       (filter some?)
       (apply uber/add-undirected-edges g)))


(defn set-coordinates
  [g [x y :as node]]
  {:pos (str x "," y)})


(defn- get-node-colours
  [g node]
  (->> (uber/in-edges g node)
       (map #(uber/attrs g %))
       (keep :color)))


(defn infer-node-colour
  "Infer the colour of a node from its edges"
  [g node]
  (some->> (get-node-colours g node)
           seq
           distinct
           first
           (assoc {} :color)))


(defn infer-node-type
  [g node]
  (let [node-colours  (get-node-colours g node)
        n-connections (count node-colours)]
    (case n-connections
      0 {:node-type :empty}
      1 {:node-type :terminal}
      2 {:node-type :connector}
      {})))


(defn highlight-terminal-nodes
  "Make terminal nodes clear"
  [g node]
  (let [{:keys [color node-type]} (uber/attrs g node)]
    (if (= :terminal node-type)
      {:fillcolor color}
      {})))


(defn set-penwidth
  [n g node-or-edge]
  (if (:color (uber/attrs g node-or-edge)) {:penwidth n} {}))


(def default-node-attrs
  {:style     :filled
   :fillcolor :black
   :color     :dimgray
   :label     ""
   :fontname  "courier"
   :shape     :circle
   :width     0.75
   :ratio     1})


(defn style-graph
  [g]
  (->> g
       (add-node-attrs set-coordinates)
       (add-node-attrs infer-node-type)
       (add-node-attrs infer-node-colour)
       (add-node-attrs highlight-terminal-nodes)
       (add-node-attrs (partial set-penwidth 3))
       (add-node-attrs (constantly default-node-attrs))
       (add-edge-attrs (partial set-penwidth 5))))


(def default-viz-opts
  {:layout    :neato
   :ratio     1
   :bgcolor   :black})


(defn draw
  "Draw a graph"
  ([g] (draw g {}))
  ([g opts]
   (let [viz-opts (merge opts default-viz-opts)]
     (-> g style-graph
         (uber/viz-graph viz-opts)))))
