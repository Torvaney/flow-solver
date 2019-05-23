(defproject flow-solver "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [org.clojure/math.combinatorics "0.1.5"]
                 [org.clojure/test.check "0.10.0-alpha4"]
                 [rolling-stones "1.0.0"]
                 [ubergraph "0.5.3"]]

  :main flow-solver.core

  :repl-options {:init-ns flow-solver.core})
