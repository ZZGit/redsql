# CURD 接口


## get-list

查询多条记录

```clojure
(require '[redsql.core :as redsql])

(def sqlmap {:select [:*]
             :from [:t_user]
             :where [:= :username "tom"]})

(redsql/get-list sqlmap)
```

```sql
SELECT * FROM t_user WHERE username = 'tom'
```


```clojure
(require '[redsql.core :as redsql])

(def sqlmap {:select [:*]
             :from [:t_user]
             :where [:like :username "tom%"]})

(redsql/get-list sqlmap)
```

```sql
SELECT * FROM t_user WHERE username LIKE 'tom%'
```


