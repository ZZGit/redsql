(ns redsql.test-fixtures
  (:require
   [redsql.core :as redsql]
   [clojure.test :refer :all]
   [migratus.core :as migratus]
   [next.jdbc.result-set :as rs]))

(def ^:private migrate-config
  {:store                :database
   :migration-dir        "migrations/"
   :init-script          "init.sql"
   :db {:connection-uri "jdbc:h2:./demo"}})

(def ^:private db-spec
  {:classname "com.p6spy.engine.spy.P6SpyDriver"
   :jdbcUrl     "jdbc:p6spy:h2:./demo"})

(defn each-test-fixtures [f]
  (migratus/migrate migrate-config)
  (migratus/init migrate-config)
  (redsql/connect! db-spec)
  (f)
  (redsql/disconnect!)
  (redsql/clear-config)
  (migratus/rollback migrate-config))
