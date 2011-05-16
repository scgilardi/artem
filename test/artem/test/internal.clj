;;  Copyright © Stephen C. Gilardi. All rights reserved. The use and
;;  distribution terms for this software are covered by the Eclipse Public
;;  License 1.0 (http://opensource.org/licenses/eclipse-1.0.php) which can be
;;  found in the file epl-v10.html at the root of this distribution. By using
;;  this software in any fashion, you are agreeing to be bound by the terms of
;;  this license. You must not remove this notice, or any other, from this
;;  software.
;;
;;  artem.test.internal
;;
;;  scgilardi (gmail)
;;  Created 5 October 2008
;;  migrated from clojure.contrib.miglayout 15 May 2011

(ns artem.test.internal
  (:import (javax.swing JButton JFrame JLabel JList JPanel
                        JScrollPane JTabbedPane JTextField JSeparator))
  (:use [artem.internal]
        [clojure.test]))

(deftest test-format-constraints
  (is (= (format-constraints "string") "string"))
  (is (= (format-constraints :keyword) "keyword"))
  (is (= (format-constraints [:key :val] "key val")))
  (is (= (format-constraints (sorted-map :key1 :val1 :key2 :val2)
                             "key1 val1, key2 val2")))
  (is (= (format-constraints (sorted-set-by
                              #(apply compare (map (memfn hashCode) %&))
                              "string"
                              :keyword
                              [:key :val]
                              (sorted-map :key1 :val1 :key2 :val2)))
         "key val, string, key1 val1, key2 val2, keyword")))

