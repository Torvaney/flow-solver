(ns flow-solver.core
  (:require [clojure.math.combinatorics :as combo]
            [clojure.spec.alpha :as s]))


(defn pair? [xs] (= 2 (count xs)))

(s/def ::id int?) ;; And gte zero?

(s/def ::colour keyword?)

(s/def ::coord (s/and pair? (s/coll-of int?)))

(s/def ::node (s/keys :req-un [::id ::coord]
                      :opt-un [::colour]))

(s/def ::endpoints (s/and set? pair? (s/coll-of int?)))

(s/def ::edge (s/keys :req-un [::id ::endpoints]
                      :opt-un [::colour]))

(s/def ::nodes (s/coll-of ::node))

(s/def ::edges (s/coll-of ::edge))

(s/def ::graph (s/keys :req-un [::nodes ::edges]
                       :opt-un []))

;; TODO: ensure that all IDs in nodes and edges are unique


(def example-nodes
  ; Looks like
  ; - - o
  ; - o x
  ; x - -
  [[:blue #{[2 0] [1 1]}]
   [:red  #{[2 1] [0 2]}]])


(defn- create-node
  ([id xy]
   {:id id
    :coord xy})
  ([id xy colour]
   {:id id
    :coord xy
    :colour colour}))


(defn square-nodes
  "Create a set of nodes on a square, cartesian coordinate system"
  [dim]
  (->> (combo/cartesian-product (range dim) (range dim))
       (map-indexed create-node))))


(defn- create-edge
  [id endpoints]
  {:id id
   :endpoints (set endpoints)})


(defn connect-edges
  "Connect nodes to each other according to a predicate function.

   If (p node-1 node-2) is true, then an edge between them will be created"
  [p nodes]
  (->> (combo/combinations nodes 2)
       (filter #(apply p %))
       (map-indexed create-edge)))


(defn square-graph
  "Create an empty square graph"
  [dim]
  (let [nodes (square-nodes dim)
        edges (connect-edges connect-square nodes)]
    {:nodes nodes
     :edges edges}))


; (defn replace-node
;   [graph id node]
;   )

; (defn init-graph
;   "Create a new graph from ")


(defn draw-graph
  "Draw a graph"
  [x]
  (println x "Hello, World!"))


(defn -main
  []
  (println "Main"))
