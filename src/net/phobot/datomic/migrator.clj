(ns net.phobot.datomic.migrator
  (:gen-class)
  (:require [clojure.string :as str]
            [clojure.java.io :as io]
            [clojure.tools.cli :as cli]
            [datomic.api :as datomic]
            [io.rkn.conformity :as conformity]
            [net.phobot.datomic.edn.edn-lister :as edn-lister]
  ))

(defn- read-edn-file [filepath]
  (->> (io/reader filepath)
       (java.io.PushbackReader.)  
       (clojure.edn/read {:readers *data-readers*})))
       
(defn- run-migration [connection file] 
  (let [norms (read-edn-file file)]
    (conformity/ensure-conforms connection norms)))

(defn run-migrations [connection migration-dir logger-fn]
  (let [migration-files (edn-lister/list-edn-files migration-dir)]
    (doseq [migration migration-files]
      (logger-fn "Processing migration file (if not already processed):" migration)
      (run-migration connection migration))))
  
(defn migrate [datastore-uri migration-dir logger-fn]
  (do 
    (logger-fn "Creating database (if it doesn't exist):" datastore-uri)
    (datomic/create-database datastore-uri)
    (let [connection (datomic/connect datastore-uri)]
      (run-migrations connection migration-dir logger-fn))))
        
(def cli-options
  [ ["-s" "--schema-dir DIR" "Directory where migration files may be found"
      :default-desc "schema-and-ref-data"]
    ["-h" "--help"]
    ])

(defn- usage [options-summary]
  (->> ["Runs database migrations in order into the specified database."
        ""
        "Usage: lein run [options] database_url"
        "or use java on the main class: net.phobot.datomic.migrator [options] database_url"
        ""
        "Options:"
        options-summary        
        ""
        "Database URL should be of form: datomic:dev://localhost:4334/hello"
        ""]
       (str/join \newline)
       (.println *err*)))
       
(defn- error-msg [errors]
 (str "The following errors occurred while parsing your command:\n\n"
      (str/join \newline errors)))
      
(defn- exit [status msg]
  (println msg)
  (System/exit status))
      
(defn -main [& args] 
  (let [{:keys [options arguments errors summary]} (cli/parse-opts args cli-options)]
    (cond
      (:help options) (exit 0 (usage summary))
      errors (exit 1 (error-msg errors))
      (empty? arguments) (exit 1 (usage summary))
      :else 
        (let [datastore (nth arguments 0)
              migration-path (:schema-dir options)
              log-fn #(apply println %&)] 
          (do
            (log-fn "Full path of migration directory:" (-> migration-path clojure.java.io/file .getAbsolutePath))
            (migrate datastore migration-path log-fn)
            (datomic/shutdown true))))))
