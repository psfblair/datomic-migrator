(ns net.phobot.datomic.edn.edn-lister
  (:gen-class)
  (:require [clojure.string :as str]))
  
(defn- regex-filename-filter
  "Given a regex, return a FilenameFilter that matches."
  [regex-str]
  (let [regex (java.util.regex.Pattern/compile regex-str)]
    (reify java.io.FilenameFilter
      (accept [_ dir name] (not (nil? (re-find regex name)))))))

(defn- migration-filename-filter [] 
  (regex-filename-filter ".*\\.edn$"))

(defn list-migration-files [migration-dir]
  (-> migration-dir 
      clojure.java.io/file 
      (.list (migration-filename-filter)) 
      sort))
