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


(defn init-graph
  "Create a new graph from a graph spec"
  [{:keys [dim nodes]}]
  (apply uber/add-nodes-with-attrs (square-graph dim) nodes))


;; Display

(defn infer-colour
  "Infer the colour of a node from its edges"
  [g node]
  (some->> (uber/in-edges g node)
           (map #(uber/attrs g %))
           (keep :color)
           distinct
           first
           (assoc {} :color)))


(defn merge-attrs
  "Returns a node with new attributes derived from f. Keeping any attributes that
   already exist."
  [f g node]
  (let [current  (uber/attrs g node)
        new      (f g node)
        combined (merge new current)]
    [node combined]))


(defn add-node-attrs
  [f g]
  (->> (uber/nodes g)
       (map #(merge-attrs f g %))
       (apply uber/add-nodes-with-attrs g)))


(def default-node-attrs
  {:style    :filled
   :color    :black
   :fontname "courier"
   :shape    :circle
   :ratio    1})


(defn style-graph
  [g]
  (->> g
       (add-node-attrs infer-colour)
       (add-node-attrs (constantly default-node-attrs))))


(def default-viz-opts
  {:layout    :neato
   :ratio     1
   :bgcolor   :black})


(defn draw
  "Draw a graph"
  ([g]
   (draw g {}))
  ([g opts]
   (let [viz-opts (merge opts default-viz-opts)]
     (do (-> g 
             style-graph 
             (uber/viz-graph viz-opts))
         g))))
