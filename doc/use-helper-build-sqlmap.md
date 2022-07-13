# 使用helper函数构造sqlmap

除了直接创建sqlmap外,[honeysql](https://github.com/seancorfield/honeysql)还提供了许多helper函数,来辅助构造sqlmap

```clojure
(require '[honey.sql.helpers :as helpers])

(-> (helpers/select :a :b :c)
    (helpers/from :foo)
    (helpers/where [:= :foo.a "baz"]))

;;=> {:select [:a :b :c] :from [:foo] :where [:= :foo.a "baz"]}
```

而redsql在此基础上添加更多的helper函数,来方便构造sqlmap的查询条件,比如使用`like`辅助函数可以避免我们手动拼接"%"的麻烦

```clojure
(require '[redsql.helpers :as hs])

(-> (hs/select :a :b :c)
    (hs/from :foo)
    (hs/where (hs/like :a "baz")))

;;=> {:select [:a :b :c] :from [:foo] :where [:like :a "%baz%"]}
```

不仅如此,redsql提供的helper函数,可以更优雅的动态构造查询条件.举个例子:



```clojure
(def name "tom")
(def age 10)

(def valid-name? (clojure.string/blank? name))
(def valid-age? (pos? age))

(-> (hs/select :*)
    (hs/from :user)
    (hs/where
        (hs/eq valid-name? :name name)
        (hs/eq valid-age? :age age)))
```

下面罗列了redsql新增的helper函数

## eq

```clojure
(require '[redsql.helpers :as hs])

(-> (hs/select :*)
    (hs/from :user)
    (hs/where (hs/eq :name "tom")))

;;等价 {:select [:*] :from [:user] :where [:= :name "tom"]}
```
