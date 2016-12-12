(ns brevis.graphics.core
  (:require [brevis.parameters :as parameters]))

(defn enable-video-recording
  "Turn on video recording."
  [video-name]
  (parameters/set-param :record-video true)
  (parameters/set-param :video-name video-name)
  (parameters/set-param :video-counter 0))

(defn disable-video-recording
  "Turn off video recording."
  []
  (parameters/set-param :record-video false))

