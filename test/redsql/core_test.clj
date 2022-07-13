(ns redsql.core-test
  (:require
   [next.jdbc :as jdbc]
   [redsql.core :as redsql]
   [redsql.helper :as helper]
   [clojure.test :refer :all]
   [migratus.core :as migratus]
   [next.jdbc.result-set :as rs]
   [redsql.test-fixtures :as fixtures]))

(use-fixtures :each fixtures/each-test-fixtures)

(def logic-delete-opt
  {:logic-delete? true
   :logic-delete-field :delete_flag
   :logic-delete-value true
   :logic-not-delete-value false
   :builder-fn rs/as-unqualified-lower-maps})

(deftest test-query
  (testing "test query"
    (let [sqlmap {:select [:*]
                  :from [:t_user]}]
      (let [rows (redsql/get-list sqlmap)]
        (is (= (count rows) 5)))
      (let [rows (redsql/get-list (with-meta sqlmap logic-delete-opt))]
        (is (= (count rows) 4)))))

  (testing "test = query"
    (let [sqlmap {:select [:*]
                  :from [:t_user]
                  :where [:= :username "sundonghe"]}]
      (let [rows (redsql/get-list sqlmap)]
        (is (= (count rows) 1)))
      (let [rows (redsql/get-list (with-meta sqlmap logic-delete-opt))]
        (is (zero? (count rows))))))

  (testing "test not= query"
    (let [sqlmap {:select [:*]
                  :from [:t_user]
                  :where [:not= :username "yuzhuangzhuang"]}]
      (let [rows (redsql/get-list sqlmap)]
        (is (= (count rows) 4)))
      (let [rows (redsql/get-list (with-meta sqlmap logic-delete-opt))]
        (is (= (count rows) 3)))))

  (testing "test like query"
    (let [sqlmap {:select [:*]
                  :from [:t_user]
                  :where [:like :realname "孙%"]}]
      (let [rows (redsql/get-list sqlmap)]
        (is (= (count rows) 2)))
      (let [rows (redsql/get-list (with-meta sqlmap logic-delete-opt))]
        (is (= (count rows) 1)))))

  (testing "test > query"
    (let [sqlmap {:select [:*]
                  :from [:t_item]
                  :where [:> :item_price 100]}]
      (let [rows (redsql/get-list sqlmap)]
        (is (= (count rows) 2)))
      (let [rows (redsql/get-list (with-meta sqlmap logic-delete-opt))]
        (is (= (count rows) 1)))))

  (testing "test < query"
    (let [sqlmap {:select [:*]
                  :from [:t_item]
                  :where [:< :item_price 30]}]
      (let [rows (redsql/get-list sqlmap)]
        (is (= (count rows) 3)))
      (let [rows (redsql/get-list (with-meta sqlmap logic-delete-opt))]
        (is (= (count rows) 2)))))

  (testing "test and query"
    (let [sqlmap {:select [:*]
                  :from [:t_item]
                  :where [:and
                          [:< :item_price 50]
                          [:= :create_user_id "3"]]}]
      (let [rows (redsql/get-list sqlmap)]
        (is (= (count rows) 2)))
      (let [rows (redsql/get-list (with-meta sqlmap logic-delete-opt))]
        (is (= (count rows) 1)))))

  (testing "test or query"
    (let [sqlmap {:select [:*]
                  :from [:t_item]
                  :where [:or
                          [:> :item_price 100]
                          [:< :item_price 30]]}]
      (let [rows (redsql/get-list sqlmap)]
        (is (= (count rows) 5)))
      (let [rows (redsql/get-list (with-meta sqlmap logic-delete-opt))]
        (is (= (count rows) 3)))))

  (testing "test or and query"
    (let [sqlmap {:select [:*]
                  :from [:t_item]
                  :where [:and
                          [:= :create_user_id "3"]
                          [:or
                           [:> :item_price 100]
                           [:< :item_price 30]]]}]
      (let [rows (redsql/get-list sqlmap)]
        (is (= (count rows) 2)))
      (let [rows (redsql/get-list (with-meta sqlmap logic-delete-opt))]
        (is (= (count rows) 1)))))

  )

(deftest test-group-by-query
  (testing "test group by single column"
    (let [sqlmap {:select [:item_sort [:%sum.item_price :total-price]]
                  :from [:t_item]
                  :group-by [:item_sort]}]
      (let [rows (redsql/get-list (with-meta sqlmap {:builder-fn rs/as-unqualified-lower-maps}))
            row (first (filter #(= (:item_sort %) 2) rows))]
        (is (= (count rows) 3))
        (is (= (:total_price row) (+ 128.00 79.00 20.00 198.00))))
      (let [rows (redsql/get-list (with-meta sqlmap logic-delete-opt))
            row (first (filter #(= (:item_sort %) 2) rows))]
        (is (= (count rows) 3))
        (is (= (:total_price row) (+ 128.00 79.00)))))))

(deftest test-order-by-query
  (testing "test single column order by asc"
    (let [sqlmap {:select [:*]
                  :from [:t_item]
                  :order-by [[:item_price :asc]]}]
      (let [rows (redsql/get-list (with-meta sqlmap {:builder-fn rs/as-unqualified-lower-maps}))]
        (is (= (count rows) 12))
        (is (= (:item_price (first rows)) 20.00)))
      (let [rows (redsql/get-list (with-meta sqlmap logic-delete-opt))]
        (is (= (count rows) 10))
        (is (= (:item_price (first rows)) 25.00)))))
  (testing "test single column order by desc"
    (let [sqlmap {:select [:*]
                  :from [:t_item]
                  :order-by [[:item_price :desc]]}]
      (let [rows (redsql/get-list (with-meta sqlmap {:builder-fn rs/as-unqualified-lower-maps}))]
        (is (= (count rows) 12))
        (is (= (:item_price (first rows)) 198.00)))
      (let [rows (redsql/get-list (with-meta sqlmap logic-delete-opt))]
        (is (= (count rows) 10))
        (is (= (:item_price (first rows)) 128.00)))))
  (testing "test multi columns order asc"
    (let [sqlmap {:select [:*]
                  :from [:t_item]
                  :order-by [[:item_sort :asc] [:item_price :asc]]}]
      (let [rows (redsql/get-list (with-meta sqlmap {:builder-fn rs/as-unqualified-lower-maps}))]
        (is (= (count rows) 12))
        (is (= (:item_name (first rows)) "三体")))
      (let [rows (redsql/get-list (with-meta sqlmap logic-delete-opt))]
        (is (= (count rows) 10))
        (is (= (:item_name (first rows)) "三体")))))
  (testing "test multi columns order desc"
    (let [sqlmap {:select [:*]
                  :from [:t_item]
                  :order-by [[:item_sort :desc] [:item_price :desc]]}]
      (let [rows (redsql/get-list (with-meta sqlmap {:builder-fn rs/as-unqualified-lower-maps}))]
        (is (= (count rows) 12))
        (is (= (:item_price (first rows)) 198.00)))
      (let [rows (redsql/get-list (with-meta sqlmap logic-delete-opt))]
        (is (= (count rows) 10))
        (is (= (:item_price (first rows)) 128.00)))))
  (testing "test multi columns order desc asc"
    (let [sqlmap {:select [:*]
                  :from [:t_item]
                  :order-by [[:item_sort :desc] [:item_price :asc]]}]
      (let [rows (redsql/get-list (with-meta sqlmap {:builder-fn rs/as-unqualified-lower-maps}))]
        (is (= (count rows) 12))
        (is (= (:item_price (first rows)) 20.00)))
      (let [rows (redsql/get-list (with-meta sqlmap logic-delete-opt))]
        (is (= (count rows) 10))
        (is (= (:item_price (first rows)) 79.00)))))
  )


(deftest test-query-page
  (let [sqlmap {:select [:*]
                :from [:t_item]
                :limit 5
                :offset 0}]
    (let [{:keys [rows page size total-count total-page] :as p} (redsql/get-page sqlmap)]
      (is (and (= (count rows) 5)
               (= page 0)
               (= size 5)
               (= total-count 12)
               (= total-page 3))))
    (let [page-result (redsql/get-page sqlmap)
          result (helper/convert-rows
                  page-result
                  (fn [row] "1"))]
      (is (:rows result) '("1" "1" "1" "1" "1")))
    (let [{:keys [rows page size total-count total-page] :as result} (redsql/get-page (with-meta sqlmap logic-delete-opt))]
      (prn result)
      (is (and (= (count rows) 5)
               (= page 0)
               (= total-count 10)
               (= total-page 2)))))
  (let [sqlmap {:select [:*]
                :from [:t_item]
                :limit 5
                :offset 9}]
    (let [{:keys [rows page size total-count total-page]} (redsql/get-page sqlmap)]
      (is (and (= (count rows) 3)
               (= page 1)
               (= total-count 12)
               (= total-page 3))))
    (let [{:keys [rows page size total-count total-page]} (redsql/get-page (with-meta sqlmap logic-delete-opt))]
      (is (and (= (count rows) 1)
               (= page 1)
               (= total-count 10)
               (= total-page 2)))))
  (let [sqlmap {:select [:*]
                :from [:t_item]
                :limit 5
                :offset 0
                :order-by [[:item_price :desc]]}]
    (let [{:keys [rows page size total-count total-page]} (redsql/get-page sqlmap)]
      (is (and (= (count rows) 5)
               (= page 0)
               (= total-count 12)
               (= total-page 3))))
    (let [{:keys [rows page size total-count total-page]} (redsql/get-page (with-meta sqlmap logic-delete-opt))]
      (is (and (= (count rows) 5)
               (= page 0)
               (= total-count 10)
               (= total-page 2)))))
  )

(deftest test-join-query
  (let [sqlmap {:select [:*]
                :from [[:t_item :i]]
                :join [[:t_user :u] [:= :u.id :i.create_user_id]]
                :where [:= :u.id "1"]}
        items (redsql/get-list sqlmap)]
    (is (= 4 (count items))))
  (let [sqlmap {:select [:*]
                :from [[:t_item :i]]
                :join [[:t_user :u] [:= :u.id :i.create_user_id]]
                :where [:= :u.id "1"]}
        items (redsql/get-list (with-meta sqlmap logic-delete-opt))]
    (is (= 3 (count items)))))

(deftest test-insert!
  (let [fake-user {:id "999"
                   :username "liubei"
                   :password "123456"
                   :realname "刘备"
                   :email "liubei@shu.com"
                   :create_user_id "0"
                   :update_user_id "0"}]
    (redsql/insert!
     {:table :t_user
      :record fake-user})
    (let [user (redsql/get-one
                (with-meta
                  {:select [:*]
                   :from [:t_user]
                   :where [:= :id "999"]}
                  {:builder-fn rs/as-unqualified-lower-maps}))]
      (is (not (nil? user)))
      (is (= (:username user) "liubei"))
      (is (nil? (:delete_flag user)))))
  (let [fake-user {:id "1000"
                   :username "zhugeliang"
                   :password "123456"
                   :realname "诸葛亮"
                   :email "zhugeliang@shu.com"
                   :create_user_id "0"
                   :update_user_id "0"}]
    (redsql/insert!
     (with-meta
       {:table :t_user
        :record fake-user}
       logic-delete-opt))
    (let [sqlmap {:select [:*]
                  :from [:t_user]
                  :where [:= :id "1000"]}]
      (let [user (redsql/get-one
                  (with-meta
                    sqlmap
                    {:builder-fn rs/as-unqualified-lower-maps}))]
        (is (not (nil? user)))
        (is (= (:username user) "zhugeliang"))
        (is (false? (:delete_flag user))))))
  )


(deftest test-insert-multi!
  (let [fake-users [{:id "999"
                     :username "liubei"
                     :password "123456"
                     :realname "multi-user"
                     :email "liubei@shu.com"
                     :create_user_id "0"
                     :update_user_id "0"}
                    {:id "1000"
                     :username "zhugeliang"
                     :password "123456"
                     :realname "multi-user"
                     :email "zhugeliang@shu.com"
                     :create_user_id "0"
                     :update_user_id "0"}]]
    (redsql/insert-multi!
     {:table :t_user
      :records fake-users})
    (let [user-count (redsql/get-simple-count
                      :t_user
                      {:realname "multi-user"})]
      (is (= (count fake-users) user-count)))))

(deftest test-update!
  (redsql/update! {:update :t_category
                   :set {:category_name "数码"
                         :category_code "SHUMA"
                         :remark "数码分类"}
                   :where [:= :id 2]})
  (let [row (redsql/get-one (with-meta
                              {:select [:*]
                               :from [:t_category]
                               :where [:= :id 2]}
                              {:builder-fn rs/as-unqualified-lower-maps}))]
    (is (= (:category_name row) "数码"))
    (is (= (:category_code row) "SHUMA"))
    (is (= (:remark row) "数码分类")))
  (redsql/update!
   (with-meta
     {:update :t_category
      :set {:category_name "美食"
            :category_code "MEISHI"
            :remark "美食分类"}
      :where [:= :id 3]}
     logic-delete-opt))
  (let [row (redsql/get-one (with-meta
                              {:select [:*]
                               :from [:t_category]
                               :where [:= :id 3]}
                              {:builder-fn rs/as-unqualified-lower-maps}))]
    (is (not= (:category_name row) "美食"))
    (is (not= (:category_code row) "MEISHI"))
    (is (not= (:remark row) "美食分类"))))


(deftest test-delete-by-sqlmap
  (testing "物理删除"
    (let [sqlmap {:delete-from [:t_user]
                  :where [:= :id "1"]}]
      (redsql/delete! sqlmap)
      (let [user (redsql/get-one
                  {:select [:*]
                   :from [:t_user]
                   :where [:= :id "1"]})]
        (is (nil? user)))))
  (testing "逻辑删除"
    (let [sqlmap {:delete-from [:t_user]
                  :where [:= :id "2"]}]
      (redsql/delete! (with-meta sqlmap logic-delete-opt))
      (let [user (redsql/get-one
                  (with-meta
                    {:select [:*]
                     :from [:t_user]
                     :where [:= :id "2"]}
                    {:builder-fn rs/as-unqualified-lower-maps}))]
        (is (not (nil? user)))
        (is (true? (:delete_flag user))))))
  )
