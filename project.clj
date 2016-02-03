(defproject net.phobot.datomic/migrator "0.1.0-SNAPSHOT"
  :description "Tool for running database migrations for Gravy databases."
  :dependencies [[org.clojure/clojure "1.7.0"]
                  [com.datomic/datomic-pro "0.9.5344"]
                  [io.rkn/conformity "0.4.0"]]
  :main net.phobot.datomic.migrator
  :aot :all)
