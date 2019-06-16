(ns flow-solver.core-test
  (:require [clojure.test :as test]
            [flow-solver.core :as core]
            [ubergraph.core :as uber]))	


(def puzzle-3-by-3
  (-> (uber/graph
       [[0 0] [0 1]]
       [[0 0] [1 0]]
       [[1 0] [1 1]]
       [[1 0] [2 0]]
       [[1 1] [1 2]]
       [[1 1] [2 1]]
       [[0 2] [1 2]]
       [[2 0] [2 1]]
       [[2 1] [2 2]]
       [[1 2] [2 2]]
       [[0 1] [0 2]]
       [[0 1] [1 1]])
      (uber/add-nodes-with-attrs
       [[1 1] {:color :blue}]
       [[0 2] {:color :red}]
       [[2 0] {:color :blue}]
       [[2 1] {:color :red}])))


(def solution-3-by-3
  (uber/add-nodes-with-attrs
   puzzle-3-by-3   
   [[2 2] {:color :red}]
   [[0 0] {:color :blue}]
   [[1 0] {:color :blue}]
   [[1 1] {:color :blue}]
   [[0 2] {:color :red}]
   [[2 0] {:color :blue}]
   [[2 1] {:color :red}]
   [[1 2] {:color :red}]
   [[0 1] {:color :blue}]))


(test/deftest solve-test
  (test/testing "That a simple map can be solved"
    (test/is (= (uber/nodes solution-3-by-3) 
                (uber/nodes (core/solve puzzle-3-by-3))))))
