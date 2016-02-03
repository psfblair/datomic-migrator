(ns net.phobot.datomic.migrator
  (:gen-class)
  (:require [clojure.string :as str]
            [net.phobot.datomic.edn.edn-lister :as edn-lister]
            [io.rkn.conformity :as conformity]
            [datomic.api :as datomic]
  ))

(defn- run-migration [connection file] 
  (let [norms (conformity/read-resource file)]
    (conformity/ensure-conforms connection norms)))

(defn run-migrations [datastore-uri migration-dir logger-fn]
  (do 
    (logger-fn "Creating database:" datastore-uri)
    (datomic/create-database datastore-uri)
    (let [connection (datomic/connect datastore-uri)
          migration-files (edn-lister/list-migration-files migration-dir)]
      (doseq [migration migration-files]
        (logger-fn "Processing migration file:" migration)
        (run-migration connection migration)))))

(defn- usage []
  (->> ["Runs database migrations in order into the specified database."
        ""
        "Usage: lein run database_url"
        "or use java on the main class: net.phobot.datomic.migrator database_url"
        ""
        "Database URL should be of form: datomic:dev://localhost:4334/hello"
        ""]
       (str/join \newline)
       (.println *err*)))

(defn -main [& args] 
  (do
    (if (= 1 (count args))
      (let [datastore (nth args 0)
            log-fn #(apply println %&)]
        (log-fn "Migrating data store:" datastore)
        (run-migrations datastore "schema-and-ref-data" log-fn)
        (datomic/shutdown true))
      (usage))))
