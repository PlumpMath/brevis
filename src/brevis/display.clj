(ns brevis.display
  (:use [brevis globals image])
  (:require [clojure.java.io :as io])
  )


#_(defn screenshot-image
    "Take a screenshot and return an image (BufferedImage for now)."
    []
    (let [img (Basic3D/screenshotImage)]     
      (end-with-graphics-thread)
      img))

#_(defn screenshot
     "Take a screenshot."
     [filename]
     (write-image filename (screenshot-image)))

#_(defn regen-mesh
   "Regenerate a mesh's openGL list."
   [msh]
   (begin-with-graphics-thread)
   (.opengldrawtolist ^BrMesh msh)
   (end-with-graphics-thread))


