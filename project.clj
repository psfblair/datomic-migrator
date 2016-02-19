(defproject net.phobot.datomic/migrator "2.1.1"
  :description "Tool for running database migrations for Gravy databases."
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [com.datomic/datomic-pro "0.9.5344"]
                 [io.rkn/conformity "0.4.0"]
                 [org.clojure/tools.cli "0.3.3"]]
  :main net.phobot.datomic.migrator
  :license {:name "MIT License"
            :url "http://opensource.org/licenses/MIT"
            :year 2015
            :key "mit"}
  :url "https://github.com/psfblair/datomic-migrator"
  :aot :all)
