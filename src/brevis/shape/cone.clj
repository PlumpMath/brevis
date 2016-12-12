(ns brevis.shape.cone
  (:import [brevis BrShape])
  (:require [brevis.parameters :as parameters])
  (:use [brevis vector]
        [brevis.shape.core])) 

(defn create-cone
  "Create a cone object."
  ([]
     (create-cone 1 1))
  ([length base]
    (BrShape/createCone length base (parameters/get-param :gui) #_(:gui @brevis.globals/*gui-state*))))
      
