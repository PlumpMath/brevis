(ns brevis.example.distributed-computing.job-test
  (:gen-class)
  (:use [brevis.distributed-computing slurm]))

(defn -main
  [& args]
  (let [argmap (apply hash-map
                      (mapcat #(vector (read-string (first %)) (second %) #_(read-string (second %)))
                              (partition 2 args)))]
    (println "Test job")
    (doseq [[k v] argmap]
      (println k v))
    (spit (str "job_test_" (:test-arg argmap) ".txt") 
          "The job completed.")
    ))


