(ns brevis.vector
  (:use [brevis.math])
  (:import [cleargl GLVector]))

(defn vec3
  "Make a GLVector"
  [x y z]
  ^GLVector (GLVector. (float-array [(float x) (float y) (float z)])))

(defn vec3?
  "Test if this is a vec3."
  [v]
  (and (= (class v) cleargl.GLVector)
       (= (- (.hashCode v) 31) 3)))

(defn vec4
  "Make a GLVector"
  [x y z w]
  ^GLVector (GLVector. (float-array [(float x) (float y) (float z) (float w)])))

(defn vec4? 
  "Test if this a 4D vector."
  [v]
  (and (= (class v) cleargl.GLVector)
       (= (- (.hashCode v) 31) 4)))

(defn vec4-to-vec3
  "convert a vec4 to a vec3"
  [^GLVector v]
  (GLVector. (float-array [(.x v) (.y v) (.z v)])))

(defn vec3-to-vec4
  "Convert a vec3 to a vec4 by padding the 4th dim with 1."
  [^GLVector v]
  (GLVector. (float-array [(.x v) (.y v) (.z v) 1])))

(defn sub
   "Wrap's GLVector sub."
   [v1 v2]
   (.minus ^GLVector v1 ^GLVector v2))

(def sub-vec3 sub) 
(def sub-vec4 sub)

(defn div
  "Divide a vector by a scalar."
  [v s]
  (let [vr (GLVector. v)]    
    (dotimes [k (if (vec3? vr) 3 4)]
      (.set vr k (/ (.get v k) s)))
    vr))

(def div-vec3 div) 
(def div-vec4 div)
    
(defn add
  "Add GLVector's"
  ([v]
    v)
  ([v1 v2]
    (.plus v1 v2))
  ([v1 v2 & vs]
    (loop [vs vs
           v (add v1 v2)]
      (if (empty? vs)
        v
        (recur (rest vs)
               (add v (first vs)))))))

(def add-vec3 add)
(def add-vec4 add)

(defn mul
  "Multiply a GLVector by a scalar."
  [v s]
  (let [vr (GLVector. v)]    
    (dotimes [k (if (vec3? vr) 3 4)]
      (.set vr k (* (.get v k) s)))
    vr))

(def mul-vec3 mul)
(def mul-vec4 mul)

(defn elmul-vec3
  "Multiply a GLVector by a scalar."
  [^GLVector v ^GLVector w]
  (let [vr ^GLVector (GLVector. v)]
    (.set vr 0 (float (* (.x w) (.x v))))
    (.set vr 1 (float (* (.y w) (.y v))))
    (.set vr 2 (float (* (.z w) (.z v))))
    vr))

(defn elmul-vec4
  "Multiply a GLVector by a scalar."
  [^GLVector v ^GLVector w]
  (let [vr ^GLVector (GLVector. v)]
    (.set vr 0 (float (* (.x w) (.x v))))
    (.set vr 1 (float (* (.y w) (.y v))))
    (.set vr 2 (float (* (.z w) (.z v))))
    (.set vr 3 (float (* (.w w) (.w v))))
    vr))

(defn elmul
  "Multiply a GLVector by a scalar."
  [v w]  
  (if (vec3? v)
    (elmul-vec3 v w)
    (elmul-vec4 v w)))

(defn dot
  "Dot product of 2 vectors."
  [v1 v2]
  (.times v1 v2)) 

(def dot-vec3 dot)
(def dot-vec4 dot)

(defn length
  "Return the length of a vector."
  [v] 
  (.magnitude v))

(def length-vec3 length)
(def length-vec4 length)  

(defn cross
  "Cross product of vectors."
  [^GLVector v1 ^GLVector v2]  
  (.cross v1 v2))

(defn normalize
  "Normalize a vector."
  [v]
  (.getNormalized v))

(def normalize-vec3 normalize)
(def normalize-vec4 normalize)

(defn map-vec3
  "Map over a vec3"
  [f ^GLVector v]
  (vec3 (f (.x v)) (f (.y v)) (f (.z v))))

(defn map-vec4
  "Map over a vec4"
  [f ^GLVector v]
  (vec4 (f (.x v)) (f (.y v)) (f (.z v)) (f (.w v))))

(defn vec3-to-seq
  "Quick hacks for seq-ing vectors."
  [^GLVector v]
  [(.x v) (.y v) (.z v)])

(defn vec4-to-seq
  "Quick hacks for seq-ing vectors."
  [^GLVector v]
  [(.x v) (.y v) (.z v) (.w v)])

(defn x-val
  "Return the x-value of a vector."
  [v]
  (.x ^GLVector v))

(def x-val-vec3 x-val)
(def x-val-vec4 x-val)

(defn y-val
  "Return the y-value of a vector."
  [v]
  (.y ^GLVector v))

(def y-val-vec3 y-val)
(def y-val-vec4 y-val)

(defn z-val
  "Return the z-value of a vector."
  [v]
  (.z ^GLVector v))

(def z-val-vec3 z-val)
(def z-val-vec4 z-val)
