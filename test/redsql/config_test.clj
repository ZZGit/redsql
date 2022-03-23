(ns redsql.config-test
  "全局配置功能测试"
  (:require
   [next.jdbc :as jdbc]
   [redsql.core :as redsql]
   [clojure.test :refer :all]
   [migratus.core :as migratus]
   [next.jdbc.result-set :as rs]
   [redsql.test-fixtures :as fixtures]))

(use-fixtures :each fixtures/each-test-fixtures)

;;全局配置
(def global-config-opt
  {:logic-delete? true
   :logic-delete-field :delete_flag
   :logic-delete-value true
   :logic-not-delete-value false
   :builder-fn rs/as-unqualified-lower-maps})

;;局部配置
(def local-config-opt
  {:logic-delete? false
   :builder-fn rs/as-unqualified-lower-maps})

;; 测试全局配置功能
(deftest test-global-config
  ;;设置全局配置
  (redsql/set-config global-config-opt)
  (let [sqlmap {:select [:*]
                :from [:t_user]}
        gusers (redsql/get-list sqlmap)
        lusers (redsql/get-list
                (with-meta sqlmap local-config-opt))]
    (is (= 4 (count gusers)))
    (is (= 5 (count lusers)))))


