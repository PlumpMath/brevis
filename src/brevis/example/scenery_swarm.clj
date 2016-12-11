(ns brevis.example.scenery-swarm
  (:gen-class)
  (:import brevis.graphics.SceneryApplication))

(def app (SceneryApplication. "Brevis" 800 600))

(.main app)
