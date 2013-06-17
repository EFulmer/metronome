; TODO: nicer method of playing files than Applet. Look into Media/MediaPlayer?

(ns metronome.core
  (:gen-class :main true)
  (:import (java.applet Applet)
           (java.io File)
           (java.net URL)
           (java.util Timer TimerTask)))

; Source for play-url and play-file:
; http://www.gettingclojure.com/cookbook:system
(defn play-url [url-string]
  (.play (Applet/newAudioClip (URL. url-string))))

(defn play-file [file-name]
  (let [absolute-name (.getAbsolutePath (File. file-name))
        url-string (str "file://" absolute-name)]
    (play-url url-string)))
 
(def click 
  (proxy [TimerTask] [] 
             (run [] (play-file "audio/click.mp3"))))

(def clicky (new Timer))

(defn -main
  "Starts a metronome."
  [& args]
  ;; work around dangerous default behaviour in Clojure
  ; (alter-var-root #'*read-eval* (constantly false))
  ; (println "Hello, World!")
  (.scheduleAtFixedRate clicky (long 1000) (long 1000))
  )
