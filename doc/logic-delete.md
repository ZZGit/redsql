# 逻辑删除

逻辑删除的功能，想必在很多系统设计中都有体现，通过一个约定的字段的值来表示删除的标志

比如下面的表

| id | name   | age | email        | delete_flag |
|----|--------|-----|--------------|-------------|
| 1  | Kevin  | 18  | test1@hc.com | 0           |
| 2  | Dirc   | 20  | test2@hc.com | 1           |
| 3  | Tom    | 28  | test2@hc.com | 0           |
| 4  | Marvin | 19  | test2@hc.com | 0           |
| 5  | Serina | 24  | test2@hc.com | 0           |

上面的用户表，使用`delete_flag`作为逻辑删除的属性, `1`表示删除,`0`表示不删除

增加了逻辑删除属性，会伴随着代码的改动，比如之前的删除操作变成了更新操作

```clojure
(require '[redsql.core :as redsql])

;; 增加逻辑删除之前的删除操作
(redsql/simple-delete! :user {:id "3"})

;; 增加逻辑删除之后的删除操作
(redsql/update! {:update :user
                 :set {:delete_flag 1}
                 :where [:= :id "3"]})
```

同样查询操作也需要小的改动

```clojure
(require '[redsql.core :as redsql])


;; 增加逻辑删除之前的查询操作
(redsql/get-list {:select [:*]
                  :from [:user]
                  :where [:> :age 10]})

;; 增加逻辑删除之后的查询操作
(redsql/get-list {:select [:*]
                  :from [:user]
                  :where [[:> :age 10] [:= :delete_flag 1]]})
```

## redsql逻辑删除

为此redsql提供了逻辑删除的配置, 开启或关闭这个功能只需要简单的改改配置，不需要其他的改动，比如

默认情况下，逻辑删除的功能是关闭的

```clojure
(require '[redsql.core :as redsql])

(redsql/simple-delete! :user {:id "3"})

(redsql/get-simple-one :user {:id "3"})
```

打印的sql

```sql
DELETE FROM user WHERE id='3'

SELECT * FROM user WHERE id='3'
```

下面通过配置来启用逻辑删除功能

```clojure
(require '[redsql.core :as redsql])

;; 逻辑删除的配置
(def config
  {:logic-delete? true
   :logic-delete-field :delete_flag
   :logic-delete-value 1
   :logic-not-delete-value 0})

;; 设置配置
(redsql/set-config config)

(redsql/simple-delete! :user {:id "3"})

(redsql/get-simple-one :user {:id "3"})
```

打印的sql

```sql
UPDATE user SET delete_flag=1 WHERE id='3'

SELECT * FROM user WHERE id='3' AND delete_flag=0
```

从上面代码可以看到，我们只是增加了配置，其他的操作逻辑都不需要修改


## 某几个表不配置逻辑删除

在实际情况中，可能大部分表都是逻辑删除，但是某几个表不是逻辑删除。这种情况下，我们可以通过[配置一节](config.md)提到的配置级别优先级，使用api级配置进行覆盖全局或者数据源级的配置

```clojure
(require '[redsql.core :as redsql])

;; 逻辑删除的配置
(def config
  {:logic-delete? true
   :logic-delete-field :delete_flag
   :logic-delete-value 1
   :logic-not-delete-value 0})
;; 数据源级配置
(redsql/set-config config)

(redsql/get-simple-one :user {:id "1"})
(redsql/get-simple-one :order {:id "1"})
;; product表不使用逻辑删除
(redsql/get-simple-one :product {:id "1"} {:logic-delete? false})
```
打印log

```sql
SELECT * FROM user WHERE id='1' AND delete_flag=0
SELECT * FROM order WHERE id='1' AND delete_flag=0
SELECT * FROM product WHERE id='1'
```
