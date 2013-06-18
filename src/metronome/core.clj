; TODO: 
; nicer/more idiomatic way to play sound - using Clojure's concurrency model?
; let user provide their own time and sound
; GUI

(ns metronome.core
  (:gen-class :main true)
  (:import (java.io File)
           (java.util Timer TimerTask)))

; Source: 
; http://comments.gmane.org/gmane.comp.java.clojure.user/18925
(defn play
  "Plays an audio file. Blocks until playback is complete."
  [file]
  (let [f (if (instance? java.io.File file) file (java.io.File. file))]
    (with-open [aif (javax.sound.sampled.AudioSystem/getAudioInputStream f)
                ais (javax.sound.sampled.AudioSystem/getAudioInputStream
                      javax.sound.sampled.AudioFormat$Encoding/PCM_SIGNED aif)]
      (let [af (.getFormat ais)]
        (with-open [line (javax.sound.sampled.AudioSystem/getSourceDataLine af)]
          (.open line af)
          (let [bufsize (.getBufferSize line)
                buf (into-array Byte/TYPE (repeat bufsize (byte 0)))]
            (.start line)
            (loop []
              (let [br (.read ais buf 0 bufsize)]
                (if-not (= -1 br)
                  (do
                    (.write line buf 0 br)
                    (recur))
                  (doto line (.drain) (.stop))))))))))) 

(def click 
  (proxy [TimerTask] [] 
             (run [] (play "./src/metronome/audio/click.wav"))))

(def clicky (new Timer))

(defn -main
  "Starts a metronome."
  [& args]
  ;; work around dangerous default behaviour in Clojure
  ; (alter-var-root #'*read-eval* (constantly false))
  ; (println "Hello, World!")
  (.scheduleAtFixedRate clicky click (long 1000) (long 1000)))

