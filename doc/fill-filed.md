# 字段自动填充

很多情况下，数据表某些字段，设置值的逻辑是固定，比如下面的用户表

| id | name   | age | email        | create_time         | update_time         |
|----|--------|-----|--------------|---------------------|---------------------|
| 1  | Kevin  | 18  | test1@hc.com | 2020-10-30 11:51:47 | 2020-10-30 11:51:47 |
| 2  | Dirc   | 20  | test2@hc.com | 2020-10-31 11:51:47 | 2020-10-31 11:51:47 |
| 3  | Tom    | 28  | test2@hc.com | 2020-10-32 11:51:47 | 2020-10-33 11:51:47 |
| 4  | Marvin | 19  | test2@hc.com | 2020-10-33 11:51:47 | 2020-10-33 11:51:47 |
| 5  | Serina | 24  | test2@hc.com | 2020-10-34 11:51:47 | 2020-10-34 11:51:47 |

`create_time`和`update_time`字段,分别表示插入的时间和更新的时间

每次执行插入和更新都需要修改这两个字段的值

```clojure

;; 插入操作
(redsql/insert!
     {:table :user
      :record {:id "6"
               :username "liubei"
               :age 100
               :email "liubei@shu.com"
               :create_time (java.util.Date.)
               :update_user (java.util.Date.)}})

(redsql/insert!
     {:table :user
      :record {:id "7"
               :username "guanyu"
               :age70
               :email "guanyu@shu.com"
               :create_time (java.util.Date.)
               :update_user (java.util.Date.)}})

;; 更新操作
(redsql/update! {:update :user
                 :set {:name "tom"
                       :update_time (java.util.Date.)}
                 :where [:= :id 2]})

(redsql/update! {:update :user
                 :set {:name "tom"
                       :update_time (java.util.Date.)}
                 :where [:= :id 3]})
```

为此redsql提供字段自动填充功能，只需要统一进行配置，自动在插入或者更新时填充字段

```clojure
(require '[redsql.core :as redsql])

(defn- insert-fill-filed-fn []
  {:create_time (java.util.Date.)
  :update_time (java.util.Date.)})

(defn- update-fill-filed-fn []
  {:update_time (java.util.Date.)})

(def config
  {:insert-fill-filed-fn insert-fill-filed-fn
   :update-fill-filed-fn update-fill-filed-fn})

(redsql/set-config config)

;; 插入操作
(redsql/insert!
     {:table :user
      :record {:id "6"
               :username "liubei"
               :age 100
               :email "liubei@shu.com"}})

(redsql/insert!
     {:table :user
      :record {:id "7"
               :username "guanyu"
               :age70
               :email "guanyu@shu.com""}})

;; 更新操作
(redsql/update! {:update :user
                 :set {:name "tom"}
                 :where [:= :id 2]})

(redsql/update! {:update :user
                 :set {:name "tom"}
                 :where [:= :id 3]})
```

在上面的代码中，配置了`:insert-fill-filed-fn`，表示当执行insert!操作时，会执行`:insert-fill-filed-fn`配置的函数，把函数返回的结果自动填充到插入数据。而`:update-fill-filed-fn`会在执行update!操作时自动填充
