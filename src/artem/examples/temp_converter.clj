;;  Copyright © Stephen C. Gilardi. All rights reserved. The use and
;;  distribution terms for this software are covered by the Eclipse Public
;;  License 1.0 (http://opensource.org/licenses/eclipse-1.0.php) which can be
;;  found in the file epl-v10.html at the root of this distribution. By using
;;  this software in any fashion, you are agreeing to be bound by the terms of
;;  this license. You must not remove this notice, or any other, from this
;;  software.
;;
;;  A temperature converter using miglayout. Demonstrates accessing
;;  components by their id constraint.
;;
;;  scgilardi (gmail)
;;  Created 31 May 2009
;;  migrated from clojure.contrib.miglayout 15 May 2011

(ns artem.examples.temp-converter
  (:use [artem.miglayout :only [miglayout components]])
  (:import (javax.swing JButton JFrame JLabel JPanel JTextField
                        SwingUtilities)
           (java.awt.event KeyAdapter)))

(defn- add-key-typed-listener
  "Adds a KeyListener to component that only responds to KeyTyped events.
  When a key is typed, f is invoked with the KeyEvent as its first
  argument followed by args. Returns the listener. (copied from
  clojure.contrib.swing-utils)"
  [component f & args]
  (let [listener (proxy [KeyAdapter] []
                   (keyTyped [event] (apply f event args)))]
    (.addKeyListener component listener)
    listener))

(defn fahrenheit
  "Converts a Celsius temperature to Fahrenheit. Input and output are
  strings. Returns \"input?\" if the input can't be parsed as a Double."
  [celsius]
  (try
   (format "%.2f" (+ 32 (* 1.8 (Double/parseDouble celsius))))
   (catch NumberFormatException _ "input?")))

(defn- handle-key
  "Clears output on most keys, shows conversion on \"Enter\""
  [event out]
  (.setText out
    (if (= (.getKeyChar event) \newline)
      (fahrenheit (-> event .getComponent .getText))
      "")))

(defn converter-ui
  "Lays out and shows a Temperature Converter UI"
  []
  (let [panel
        (miglayout (JPanel.)
         (JTextField. 6) {:id :input}
         (JLabel. "\u00b0Celsius") :wrap
         (JLabel.) {:id :output}
         (JLabel. "\u00b0Fahrenheit"))
        {:keys [input output]} (components panel)]
    (add-key-typed-listener input handle-key output)
    (doto (JFrame. "Temperature Converter")
      (.setDefaultCloseOperation JFrame/DISPOSE_ON_CLOSE)
      (.add panel)
      (.pack)
      (.setVisible true))))

(defn main
  "Invokes converter-ui in the AWT Event thread"
  []
  (SwingUtilities/invokeLater converter-ui))
