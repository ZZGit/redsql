(ns redsql.fill-filed-test
  (:require
   [redsql.core :as redsql]
   [clojure.test :refer :all]
   [next.jdbc.result-set :as rs]
   [redsql.test-fixtures :as fixtures]))


(use-fixtures :each fixtures/each-test-fixtures)

(defn- insert-fill-filed-fn []
  {:create_user_id "666"})

(defn- update-fill-filed-fn []
  {:update_user_id "888"})

(def fill-filed-opt
  {:insert-fill-filed-fn insert-fill-filed-fn
   :update-fill-filed-fn update-fill-filed-fn
   :builder-fn rs/as-unqualified-lower-maps})

(def ^:private fake-user
  {:id "999"
   :username "liubei"
   :password "123456"
   :realname "刘备"
   :email "liubei@shu.com"})

(deftest test-fill-filed!
  (redsql/set-config fill-filed-opt)
  (testing "insert-fill-filed"
    (redsql/insert!
     {:table :t_user
      :record fake-user})
    (let [user (redsql/get-simple-one :t_user {:id "999"})]
      (is (not (nil? user)))
      (is (= "666" (:create_user_id user)))))
  (testing "update-fill-filed"
    (redsql/update! {:update :t_user
                     :set {:realname "玄德"}
                     :where [:= :id "999"]})
    (let [user (redsql/get-simple-one :t_user {:id "999"})]
      (is (not (nil? user)))
      (is (= "888" (:update_user_id user)))))
  )
