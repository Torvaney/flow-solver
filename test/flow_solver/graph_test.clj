(ns flow-solver.graph-test
  (:require [clojure.test :as test]
            [flow-solver.graph :as graph]))


(test/deftest test-square-nodes
  (test/testing "That nodes for a square graph are created as expected"
    (test/is (= {0 {:id 0 :coord [0 0]}
                 1 {:id 1 :coord [0 1]}
                 2 {:id 2 :coord [0 2]}
                 3 {:id 3 :coord [1 0]}
                 4 {:id 4 :coord [1 1]}
                 5 {:id 5 :coord [1 2]}
                 6 {:id 6 :coord [2 0]}
                 7 {:id 7 :coord [2 1]}
                 8 {:id 8 :coord [2 2]}}
                (graph/square-nodes 3)))))



(test/deftest test-square-graph
  (test/testing "That square graphs are created as expected"
    (test/is (= {:nodes {0 {:id 0 :coord [0 0]}
                         1 {:id 1 :coord [0 1]}
                         2 {:id 2 :coord [0 2]}
                         3 {:id 3 :coord [1 0]}
                         4 {:id 4 :coord [1 1]}
                         5 {:id 5 :coord [1 2]}
                         6 {:id 6 :coord [2 0]}
                         7 {:id 7 :coord [2 1]}
                         8 {:id 8 :coord [2 2]}}
                 :edges {0  {:id 0  :<-> #{0 1}}
                         1  {:id 1  :<-> #{0 3}}
                         2  {:id 2  :<-> #{1 2}}
                         3  {:id 3  :<-> #{1 4}}
                         4  {:id 4  :<-> #{2 5}}
                         5  {:id 5  :<-> #{4 3}}
                         6  {:id 6  :<-> #{6 3}}
                         7  {:id 7  :<-> #{4 5}}
                         8  {:id 8  :<-> #{7 4}}
                         9  {:id 9  :<-> #{5 8}}
                         10 {:id 10 :<-> #{7 6}}
                         11 {:id 11 :<-> #{7 8}}}}
                (graph/square-graph 3)))))
