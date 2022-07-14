# 快速开始

我们将通过一个简单的Demo来演示redsql部分功能

首页准备一张`User`表，其表结构如下：

| id | name   | age | email        |
|----|--------|-----|--------------|
| 1  | Kevin  | 18  | test1@hc.com |
| 2  | Jack   | 20  | test2@hc.com |
| 3  | Tom    | 28  | test2@hc.com |
| 4  | Marvin | 19  | test2@hc.com |
| 5  | Serina | 24  | test2@hc.com |

## 添加依赖
在project.clj中添加redsql依赖

[![Clojars Project](https://img.shields.io/clojars/v/org.clojars.redcreation/redsql.svg)](https://clojars.org/org.clojars.redcreation/redsql)

## 配置

数据源详情参数请查看[hikari-cp](https://github.com/brettwooldridge/HikariCP)

```clojure
(require '[redsql.core :as redsql])

;; 数据源配置
(def db-spec
  {:classname "com.p6spy.engine.spy.P6SpyDriver"
   :jdbcUrl "jdbc:h2:./demo"})

(redsql/connect! db-spec)
```

## 开始使用

### 查询多条数据

```clojure
(require '[redsql.core :as redsql])

(def sqlmap {:select [:*]
             :from [:user]
             :where [:and
                      [:= :name "Tom"]
                      [:> :age 20]]})

(redsql/get-list sqlmap)
```

或者

```clojure
(require '[redsql.core :as redsql])

(redsql/get-simple-list
    :user
    {:name "Tom"
     :age {:> 20}})
```

打印sql

```sql
SELECT * FROM user WHERE name = 'Tom' AND age > 20
```

输出结果

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
```

或者

```clojure
(require '[redsql.core :as redsql])

(redsql/get-simple-one :user {:id 1})
```

打印sql

```sql
SELECT * FROM user WHERE id = 1
```

输出结果

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

打印两条sql

```sql
SELECT * FROM user LIMIT 3 OFFSET 0
SELECT COUNT(*) AS count FROM (SELECT * FROM user)
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

### 插入单条记录

```clojure
(require '[redsql.core :as redsql])

(def record {:id 6 :name "Mike" :age 30 :email "test6@hc.com"})

(redsql/insert! {:table :user
                 :record record})
```

打印sql

```sql
INSERT INTO user (id, name, age, email) VALUES (6, Mike, 30, 'test6@hc.com')
```

### 插入多条记录

```clojure
(require '[redsql.core :as redsql])

(def records [{:id 7 :name "Dirk" :age 24 :email "test7@hc.com"}
            {:id 8 :name "James" :age 25 :email "test8@hc.com"}])

(redsql/insert-multi! {:table :user
                       :records records})
```

打印sql

```sql
INSERT INTO user (id, name, age, email) VALUES
(7, 'Dirk', 24, 'test7@hc.com'),
(8, 'James', 24, 'test7@hc.com')

```

### 更新

```clojure
(require '[redsql.core :as redsql])

(def sqlmap {:update :user
             :set {:name "Kevin"
                   :email "Kevin@hc.com"}}
             :where [:= :id 1])

(redsql/update! sqlmap)
```

打印sql

```sql
UPDATE user SET name = 'Kevin', email = 'Kevin@hc.com'  WHERE id = 1
```

### 删除

```clojure
(require '[redsql.core :as redsql])

(def sqlmap {:delete-from [:user]
             :where [:= :id 1]})

(redsql/delete! sqlmap)
```

或者

```clojure
(require '[redsql.core :as redsql])

(redsql/simple-delete! :user {:id 1})
```

打印sql

```sql
DELETE FROM user WHERE id = '1' 
```
