;;  Copyright © Stephen C. Gilardi. All rights reserved. The use and
;;  distribution terms for this software are covered by the Eclipse Public
;;  License 1.0 (http://opensource.org/licenses/eclipse-1.0.php) which can be
;;  found in the file epl-v10.html at the root of this distribution. By using
;;  this software in any fashion, you are agreeing to be bound by the terms of
;;  this license. You must not remove this notice, or any other, from this
;;  software.
;;
;;  Internal functions for artem.layout
;;
;;  scgilardi (gmail)
;;  Created 13 October 2008
;;  migrated from clojure.contrib.miglayout 15 May 2011

(ns artem.internal
  (:import java.awt.Component
           javax.swing.JComponent
           net.miginfocom.layout.ConstraintParser
           net.miginfocom.swing.MigLayout))

(defn- as-str [x]
  ((if (keyword? x) name str) x))

(declare format-constraints)

(defn format-constraint
  "Returns a vector of vectors representing one or more constraints
  separated by commas. Constraints may be specified in Clojure using
  strings, keywords, vectors, maps, and/or sets."
  [c]
  [[", "]
   (cond
    (string? c)  [c]
    (keyword? c) [c]
    (vector? c)  (interpose " " c)
    (map? c)     (apply concat (interpose [", "] (map #(interpose " " %) c)))
    (set? c)     (apply concat (interpose [", "] (map format-constraints c)))
    :else
    (throw (IllegalArgumentException.
            (format "unrecognized constraint: %s (%s)" c (class c)))))])

(defn format-constraints
  "Returns a string representing all the constraints for one keyword-item
  or component formatted for miglayout."
  [& constraints]
  (->> constraints
       (mapcat format-constraint)
       (reduce concat [])
       (rest)
       (map as-str)
       (apply str)))

(defn component?
  "Returns true if x is a java.awt.Component"
  [x]
  (instance? Component x))

(defn constraint?
  "Returns true if x is not a keyword-item or component"
  [x]
  (not
   (or (component? x)
       (#{:layout :column :row} x))))

(defn parse-item-constraints
  "Iterates over args and builds a map containing values associated with
  :keywords and :components. The value for :keywords is a map from keyword
  items to constraints strings. The value for :components is a vector of
  vectors each associating a component with its constraints string."
  [& args]
  (loop [[item & args] args
         item-constraints {:keywords {} :components []}]
    (if item
      (let [[constraints args] (split-with constraint? args)]
        (recur args
               (update-in
                item-constraints
                [(if (component? item) :components :keywords)]
                conj [item (apply format-constraints constraints)])))
      item-constraints)))

(defn add-components
  "Adds components with constraints to a container"
  [^JComponent container components]
  (loop [[[^Component component constraint] & components] components
         id-map nil]
    (if component
      (let [cc (ConstraintParser/parseComponentConstraint constraint)]
        (.add container component cc)
        (recur components (if-let [id (.getId cc)]
                            (assoc id-map (keyword id) component)
                            id-map)))
      (doto container (.putClientProperty ::components id-map)))))

(defn get-components
  "Returns a map from id to component for all components with an id"
  [^JComponent container]
  (.getClientProperty container ::components))

(defn set-layout!
  "Attaches a MigLayout layout manager to container and adds components
  with constraints"
  [^JComponent container layout column row components]
  (doto container
    (.setLayout (MigLayout. layout column row))
    (add-components components)))
