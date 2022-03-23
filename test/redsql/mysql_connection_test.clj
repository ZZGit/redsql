(ns redsql.mysql-connection-test
  (:require
   [next.jdbc :as jdbc]
   [redsql.core :as redsql]
   [clojure.test :refer :all]
   [migratus.core :as migratus]
   [next.jdbc.result-set :as rs]))

(def migrate-config
  {:store                :database
   :migration-dir        "migrations/"
   :init-script          "init.sql"
   :db {:connection-uri "jdbc:p6spy:mysql://localhost:3306/test?user=root&password=root&useSSL=false&autoReconnect=true&useUnicode=true&characterEncoding=UTF-8&serverTimezone=Hongkong"}})

(def db-spec
  {:classname "com.p6spy.engine.spy.P6SpyDriver"
   :jdbcUrl     "jdbc:p6spy:mysql://localhost:3306/test?user=root&password=root&useSSL=false&autoReconnect=true&useUnicode=true&characterEncoding=UTF-8&serverTimezone=Hongkong"})


(defn- once-test-fixtures [f]
  (migratus/migrate migrate-config)
  (migratus/init migrate-config)
  (redsql/connect! db-spec)
  (f)
  (redsql/disconnect!)
  (redsql/clear-config)
  (migratus/rollback migrate-config))

(use-fixtures :each once-test-fixtures)

#_(deftest mysql-connection-test
    (let [users (redsql/get-simple-list :t_user)]
      (prn "$$$")
      (is (pos? (count users)))))

