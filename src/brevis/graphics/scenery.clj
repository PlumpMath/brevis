(ns brevis.graphics.scenery
  (:import [java.lang.Math]
           [java.nio ByteBuffer ByteOrder]
           [org.lwjgl.opengl GL11]
           [brevis BrObject])
  (:use [brevis.physics utils])
  (:require [clojure.java.io]
            [brevis.parameters :as parameters]
            [brevis.globals :as globals])) 

(defn get-scene
  "Return the Scenery scene."
  []
  @globals/scene)

(defn set-scene
  "Change the scene."
  [new-scene]
  (reset! globals/scene new-scene))

(defn get-renderer
  "Return the Scenery renderer."
  []
  ^scenery.backends.Renderer @globals/renderer)

(defn set-renderer
  "Change the current renderer."
  [new-renderer]
  (reset! globals/renderer new-renderer))

(defn get-hub
  "Return the Scenery hub."
  []
  @globals/hub)

(defn set-hub
  "Change the Scenery hub."
  [new-hub]
  (reset! globals/hub new-hub))

(defn initialize-scenery
  "Initialize the scenery variable."
  []
  (parameters/set-param :frame-count 0)
  (set-scene (scenery.Scene.))
  (set-hub (scenery.Hub.))

  #_(set-renderer 
     (scenery.backends.opengl.OpenGLRenderer. "Brevis" (get-scene) 512 512)
     ;(scenery.backends.opengl.DeferredLightingRenderer. "Brevis" (get-scene) 512 512)
     #_(scenery.backends.Renderer/createRenderer "Brevis" (get-scene) 512 512))
  
  (set-renderer
    (.createRenderer scenery.backends.Renderer/Companion "Brevis" (get-scene) 512 512)
    ;(scenery.backends.opengl.OpenGLRenderer. "Brevis" (get-scene) 512 512)
    #_(scenery.backends.Renderer/createRenderer "Brevis" (get-scene) 512 512))
  
  (.add (get-hub)
    scenery.SceneryElement/RENDERER
    (get-renderer)))

(defn make-point-light
  "Make a Scenery point light."
  []
  (scenery.PointLight.))

(defn add-child
  "Add a child to the scene."
  [child]
  (.addChild (get-scene) child))

(defn make-detached-head-camera
 "Make a detached head camera."
 []
 (scenery.DetachedHeadCamera.)) 


;; ## Shape handling code
;;

(defn vector3d-to-seq
  [v]
  "Return a seq that contains the vector3d's data"
  [(.x v) (.y v) (.z v)])

(defn vector4d-to-seq
  [v]
  "Return a seq that contains the vector3d's data"
  [(.x v) (.y v) (.z v) (.w v)])

(defn use-camera
  "Set the camera parameters."
  [cam]
  (.orthographicMatrix cam)
  (.perspectiveMatrix cam)
  (.translate cam))

(defn camera-set-position
  [cam new-position]
  (.setPosition cam new-position))

(defn camera-look-at
  "Camera look at a given location from the current camera location."
  [cam target-vec]
  (println "camera-look-at" (.getPosition cam) target-vec)
  (.lookAt cam (.getPosition cam) target-vec))

#_(defn draw-shape
   "Draw a shape. Call this after translating, scaling, and setting color."
   [^BrObject obj]
   (Basic3D/drawShape obj (.getDimension (.getShape obj)))
   #_(Basic3D/drawShape obj (double-array [0 1 0 0]) (.getDimension (.getShape obj))))

#_(defn add-light
   "Add a GL light."
   []
   (Basic3D/addLight))

#_(defn move-light
   "Move the n-th light."
   [n ^org.lwjgl.util.vector.Vector4f pos]
   (Basic3D/lightMove (int n) (float-array [(.x pos) (.y pos) (.z pos) (.w pos)])))

#_(defn light-diffuse 
   "Set the diffuse lighting for a GlLight"
   [n ^org.lwjgl.util.vector.Vector4f col]
   (Basic3D/lightDiffuse (int n) (float-array [(.x col) (.y col) (.z col) (.w col)])))

#_(defn light-specular
   "Set the specular lighting for a GlLight"
   [n ^org.lwjgl.util.vector.Vector4f col]
   (Basic3D/lightSpecular (int n) (float-array [(.x col) (.y col) (.z col) (.w col)])))

#_(defn light-ambient
   "Set the ambient lighting for a GlLight"
   [n ^org.lwjgl.util.vector.Vector4f col]
   (Basic3D/lightAmbient (int n) (float-array [(.x col) (.y col) (.z col) (.w col)])))

(defn disable-skybox
  "Disable rendering of the skybox."
  []
  (parameters/set-param :disable-skybox true))

(defn enable-skybox
  "Enable rendering of the skybox."
  []
  (parameters/set-param :disable-skybox false))

#_(defn change-skybox
   "Files must contain: front, left, back, right, up, down"
   [files]
   (.changeSkybox brevis.graphics.basic-3D/*sky* files))
