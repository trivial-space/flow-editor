(ns entity-editor-cljs.css
  (:require [garden.def :refer [defstyles]]))

(defstyles screen

  [:body
   {:color "red"
    :background "tomato"}]

  [:.level1
   {:color "purple"}])
