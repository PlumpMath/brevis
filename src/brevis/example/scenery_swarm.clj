(ns brevis.example.scenery-swarm
  (:gen-class)
  (:use [brevis.graphics.basic-3D]
        [brevis.physics collision core space utils]
        [brevis.shape box sphere cone]
        [brevis core osd vector camera utils display image])
  (:import brevis.graphics.SceneryApplication))

(def app (SceneryApplication. "Brevis" 800 600))

(defn -main
  [& args]
  (.main app))

(autostart-in-repl -main)
