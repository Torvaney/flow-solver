(ns flow-solver.graph
  (:require [clojure.math.combinatorics :as combo]
            [clojure.pprint]))

;; TODO: ensure that all IDs in nodes and edges are unique

(def example-nodes
  ; Looks like
  ; - - o
  ; - o x
  ; x - -
  [[:blue #{[2 0] [1 1]}]
   [:red  #{[2 1] [0 2]}]])


(defn create-node
  ([id xy]
   {:id id
    :coord (vec xy)})
  ([id xy colour]
   {:id id
    :coord (vec xy)
    :colour colour}))


(defn square-nodes
  "Create a set of nodes on a square, cartesian coordinate system"
  [dim]
  (->> (combo/cartesian-product (range dim) (range dim))
       (map-indexed create-node)
       (reduce #(assoc %1 (:id %2) %2) {})))


(defn- create-edge
  [id nodes]
  {:id id
   :<-> (set (map :id nodes))})


(defn connect-edges
  "Connect nodes to each other according to a predicate function.

   If (p node-1 node-2) is true, then an edge between them will be created"
  [p nodes]
  (->> (combo/combinations nodes 2)
       (filter #(apply p %))
       (map-indexed create-edge)))


(defn connect-square
  "Connect two nodes if they are adjacent (not including diagonals)"
  [{[x1 y1] :coord} {[x2 y2] :coord}]
  (or (and (= x1 x2)
           (<= -1 (- y2 y1) 1))
      (and (= y1 y2)
           (<= -1 (- x2 x1) 1))))


(defn square-graph
  "Create an empty square graph"
  [dim]
  (let [nodes (square-nodes dim)
        edges (connect-edges connect-square (vals nodes))]
    {:nodes nodes
     :edges edges}))


; (defn replace-node
;   [graph id node]
;   {:nodes
;    :edges ()})


(defn init-graph
  "Create a new graph from a graph spec"
  [x]
  1)


(defn draw-graph
  "Draw a graph"
  [x]
  (println x "Hello, World!"))


(defn -main
  []
  (clojure.pprint/pprint (square-graph 3)))
