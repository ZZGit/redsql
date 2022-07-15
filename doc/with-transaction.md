# 事务操作

使用`with-transaction`宏,包含的操作都处于一个事务中

```clojure
(require '[redsql.core :as redsql])

(redsql/with-transaction
  (redsql/insert! {:table :user
                   :record {...}})
  (redsql/insert! {:table :order
                   :record {...}}))
```
