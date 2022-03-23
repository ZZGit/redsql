(ns redsql.multi-datasource-test
  "多数据源测试"
  (:require
   [clojure.test :refer :all]
   [migratus.core :as migratus]
   [redsql.master-db :as master]
   [redsql.slaver-db :as slaver]
   [next.jdbc.result-set :as rs]))

(def master-migrate-config
  {:store                :database
   :migration-dir        "migrations/"
   :db {:connection-uri "jdbc:h2:./master"}})

(def slaver-migrate-config
  {:store                :database
   :migration-dir        "migrations/"
   :db {:connection-uri "jdbc:h2:./slaver"}})

(def master-db-spec
  {:classname "com.p6spy.engine.spy.P6SpyDriver"
   :jdbcUrl     "jdbc:p6spy:h2:./master"})

(def slaver-db-spec
  {:classname "com.p6spy.engine.spy.P6SpyDriver"
   :jdbcUrl     "jdbc:p6spy:h2:./slaver"})

(defn- each-test-fixtures [f]
  (migratus/migrate master-migrate-config)
  (migratus/migrate slaver-migrate-config)
  (master/connect! master-db-spec)
  (slaver/connect! slaver-db-spec)
  (f)
  (master/disconnect!)
  (slaver/disconnect!)
  (migratus/rollback master-migrate-config)
  (migratus/rollback slaver-migrate-config))

(use-fixtures :each each-test-fixtures)

(def master-user
  {:id "999"
   :username "master-user1"
   :password "123456"
   :realname "主库用户1"
   :email "master-user1@shu.com"
   :create_user_id "0"
   :update_user_id "0"})

(def slaver-user
  {:id "999"
   :username "slaver-user1"
   :password "123456"
   :realname "从库用户1"
   :email "slaver-user1@shu.com"
   :create_user_id "0"
   :update_user_id "0"})

(deftest test-multi-ds
  (master/insert!
   {:table :t_user
    :record master-user})
  (slaver/insert!
   {:table :t_user
    :record slaver-user})
  (let [opt {:builder-fn rs/as-unqualified-lower-maps}
        sqlmap {:select [:*]
                :from [:t_user]
                :where [:= :id "999"]}
        params (with-meta sqlmap opt)
        muser (master/get-one params)
        suser (slaver/get-one params)]
    (is (= "master-user1" (:username muser)))
    (is (= "slaver-user1" (:username suser)))))
