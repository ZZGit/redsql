# 使用helper函数构造sqlmap

除了直接创建sqlmap外,[honeysql]()还提供了许多helper函数,来辅助构造sqlmap

```clojure
(require '[honey.sql.helpers :as helpers])

(-> (helpers/select :a :b :c)
    (helpers/from :foo)
    (helpers/where [:= :foo.a "baz"]))

=> {:select [:a :b :c] :from [:foo] :where [:= :foo.a "baz"]}
```

而redsql在此基础上添加更多的helper函数,来方便构造sqlmap的查询条件,比如使用`like`辅助函数可以避免我们手动拼接"%"的麻烦

```clojure
(require '[redsql.helpers :as hs])

(-> (hs/select :a :b :c)
    (hs/from :foo)
    (hs/where (hs/like :a "baz")))

=> {:select [:a :b :c] :from [:foo] :where [:like :a "%baz%"]}
```

下面罗列一下honeysql内置的helper函数和redsql新增的helper函数

## honeysql提供的helpers

## redsql新增的helpers
