# redsql
[![Clojure Test CI](https://github.com/ZZGit/redsql/actions/workflows/test.yml/badge.svg)](https://github.com/ZZGit/redsql/actions/workflows/test.yml)
[![Clojars Project](https://img.shields.io/clojars/v/org.clojars.redcreation/redsql.svg)](https://clojars.org/org.clojars.redcreation/redsql)

redsql是基于[honeysql](https://github.com/seancorfield/honeysql)的数据库操作的工具，提高效率。

## 快速开始

我们将通过一个简单的Demo来演示redsql部分功能

首页准备一张`User`表，其表结构如下：

| id | name   | age | email        |
|----|--------|-----|--------------|
| 1  | Kevin  | 18  | test1@hc.com |
| 2  | Jack   | 20  | test2@hc.com |
| 3  | Tom    | 28  | test2@hc.com |
| 4  | Marvin | 19  | test2@hc.com |
| 5  | Serina | 24  | test2@hc.com |

### 添加依赖
在project.clj中添加redsql依赖
```clojure
[org.clojars.redcreation/redsql "0.1.0-SNAPSHOT"]
```

### 配置

数据源详情参数请查看[hikari-cp](https://github.com/tomekw/hikari-cp)

```clojure
(require '[redsql.core :as redsql])

;; 数据源配置
(def db-spec
  {:classname "com.p6spy.engine.spy.P6SpyDriver"
   :jdbcUrl     "jdbc:p6spy:h2:./demo"})

(redsql/connect! db-spec)
```

## 开始使用

### 查询多条数据

```clojure
(require '[redsql.core :as redsql])

(def sqlmap {:select [:*]
             :from [:user]
             :where [:> :age 20]})

(redsql/get-list sqlmap)

;; 或者 (redsql/get-simple-list :user {:age {:> 20}})
```
输出

```clojure
[
 {:id 3, :name "Tom", :age 28, :email "test3@hc.com"}
 {:id 5, :name "Serina", :age 24, :email "test5@hc.com"}
]
```

### 查询单条数据

```clojure
(require '[redsql.core :as redsql])

(def sqlmap {:select [:*]
             :from [:user]
             :where [:= :id 1]})

(redsql/get-one sqlmap)

;; 或者 (redsql/get-simple-one :user {:id 1})
```
输出

```clojure
{:id 1, :name "Kevin", :age 18, :email "test1@hc.com"}
```

### 分页查询
```clojure
(require '[redsql.core :as redsql])

(def sqlmap {:select [:*]
             :from [:user]
             :limit 3
             :offset 0})

(redsql/get-page sqlmap)
```

输出
```clojure
{:rows [{:id 1, :name "Kevin", :age 18, :email "test1@hc.com"}
        {:id 2, :name "Jack", :age 20, :email "test2@hc.com"}
        {:id 3, :name "Tom", :age 28, :email "test3@hc.com"}],
 :page 0,
 :size 3,
 :total-count 5,
 :total-page 2}
```


### 插入

```clojure
(require '[redsql.core :as redsql])

;; 插入一条记录
(redsql/insert!
     {:table :user
      :record {:id 6 :name "Mike" :age 30 :email "test6@hc.com"}})

;; 插入多条记录
(redsql/insert-multi!
     {:table :user
      :records [{:id 7 :name "Dirk" :age 24 :email "test7@hc.com"}
                {:id 8 :name "James" :age 25 :email "test8@hc.com"}]})
```

### 更新

```clojure
(require '[redsql.core :as redsql])

(def sqlmap {:update :user
             :set {:name "Kevin Li"
                   :email "Kevin@hc.com"}
                   :where [:= :id 1]})

(redsql/update! sqlmap)
```
