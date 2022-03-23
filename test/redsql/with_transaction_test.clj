(ns redsql.with-transaction-test
  (:require
   [next.jdbc :as jdbc]
   [redsql.core :as redsql]
   [clojure.test :refer :all]
   [migratus.core :as migratus]
   [next.jdbc.result-set :as rs]
   [redsql.test-fixtures :as fixtures]))

(use-fixtures :each fixtures/each-test-fixtures)

(defn- create-fake-user [id]
  {:id id
   :username (str "user" id)
   :password "123456"
   :realname (str "用户" id)
   :email (format "user%s@hc.com" id)
   :create_user_id "0"
   :update_user_id "0"})

(deftest with-transaction-test
  (redsql/set-config
   {:builder-fn rs/as-unqualified-lower-maps})
  (testing "test-transaction-ok"
    (let [id1 "1000"
          id2 "1001"]
      (redsql/with-transaction
        (redsql/insert!
         {:table :t_user
          :record (create-fake-user id1)})
        (redsql/insert!
         {:table :t_user
          :record (create-fake-user id2)}))
      (let [user1 (redsql/get-simple-one :t_user {:id id1})
            user2 (redsql/get-simple-one :t_user {:id id2})]
        (is (= id1 (:id user1)))
        (is (= id2 (:id user2))))))
  (testing "test-transaction-throw"
    (let [id3 "1003"
          id4 "1004"]
      (try
        (redsql/with-transaction
          (redsql/insert!
           {:table :t_user
            :record (create-fake-user id3)})
          (redsql/insert!
           {:table :t_user
            :record (create-fake-user id4)})
          (throw (Exception. "事务异常")))
        (catch Exception e
          (let [user3 (redsql/get-simple-one :t_user {:id id3})
                user4 (redsql/get-simple-one :t_user {:id id4})]
            (is (nil? user3))
            (is (nil? user4))))))))

