(ns redsql.simple-query-test
  "简单查询功能测试"
  (:require
   [next.jdbc :as jdbc]
   [redsql.core :as redsql]
   [clojure.test :refer :all]
   [next.jdbc.result-set :as rs]
   [redsql.test-fixtures :as fixtures]))

(use-fixtures :each fixtures/each-test-fixtures)

(deftest simple-query-test
  (redsql/set-config
   {:builder-fn rs/as-unqualified-lower-maps})
  (testing "get-simple-one"
    (let [user (redsql/get-simple-one
                :t_user
                {:id "1"})]
      (is (= (:id user) "1"))))
  (testing "get-simple-list"
    (let [users (redsql/get-simple-list
                 :t_item
                 {:item_price {:> 100}
                  :create_user_id "1"})]
      (is (= (count users) 1))))
  (testing "get-simple-count"
    (let [ct (redsql/get-simple-count
              :t_item
              {:item_price {:> 100}})]
      (is (= ct 2)))))

(def logic-delete-opt
  {:logic-delete? true
   :logic-delete-field :delete_flag
   :logic-delete-value true
   :logic-not-delete-value false
   :builder-fn rs/as-unqualified-lower-maps})

(deftest test-delete-by-sqlmap
  (testing "物理删除"
    (redsql/simple-delete! :t_user {:id "1"})
    (let [user (redsql/get-simple-one :t_user {:id "1"})]
      (is (nil? user))))
  (testing "逻辑删除"
    (redsql/set-config logic-delete-opt)
    (redsql/simple-delete! :t_user  {:id "2"})
    (let [user (redsql/get-one
                (with-meta
                  {:select [:*]
                   :from [:t_user]
                   :where [:= :id "2"]}
                  {:builder-fn rs/as-unqualified-lower-maps
                   :logic-delete? false}))]
      (is (true? (:delete_flag user)))))
  )
