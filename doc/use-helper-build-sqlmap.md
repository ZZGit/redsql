# 使用helper函数构造sqlmap

除了直接创建sqlmap外,[honeysql](https://github.com/seancorfield/honeysql)还提供了许多[helper](https://cljdoc.org/d/com.github.seancorfield/honeysql/2.2.891/api/honey.sql.helpers)函数,来辅助构造sqlmap

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

不仅如此,redsql提供的helper函数,可以更优雅处理动态构造查询条件.

比如，根据某个变量的值动态创建查询条件，是我们经常能遇到的场景。通常会判断变量值，手动构造sqlmap的查询条件

```clojure
(def name "tom")
(def age 10)

(def where-vec [(when (clojure.string/blank? name) [:= :name name])
                (when (pos? age) [:= :age age])])

(def sqlmap {:select [:*]
             :from [:t_user]
             :where where-vec})

```

而redsql提供的helper函数，第一个参数可以传入一个布尔类型的值，为true则最为查询条件，反之不查询。免去了手动构造的麻烦

```clojure
(def name "tom")
(def age 10)

(-> (hs/select :*)
    (hs/from :user)
    (hs/where
        (hs/eq (clojure.string/blank? name) :name name)
        (hs/eq (pos? age) :age age)))
```

下面罗列了redsql新增的helper函数

## <a id="eq">eq</a>

等于

```clojure
(require '[redsql.helpers :as hs])

(-> (hs/select :*)
    (hs/from :user)
    (hs/where (hs/eq :name "tom")))

;;等价 {:select [:*] :from [:user] :where [:= :name "tom"]}
```

eq函数也能应用在以"simple"命名的api中

```clojure
(require '[redsql.helpers :as hs])
(require '[redsql.core :as redsql])

(redsql/get-simple-list :t_user {:name (hs/eq "tom")
```

## ne

不等于

用法参考[eq](#eq)

## <a id="gt">gt</a>

大于

```clojure
(require '[redsql.helpers :as hs])

(-> (hs/select :*)
    (hs/from :user)
    (hs/where (hs/gt :age 20)))

;;等价 {:select [:*] :from [:user] :where [:> :age 20]}
```

gt函数也能应用在以"simple"命名的api中

```clojure
(require '[redsql.helpers :as hs])
(require '[redsql.core :as redsql])

(redsql/get-simple-list :t_user {:age (hs/gt 20)
```

## ge

大于等于

用法参考[gt](#gt)

## lt

小于

用法参考[gt](#gt)

## le

小于等于

用法参考[gt](#gt)

## <a id="like">like</a>

like查询，会自动给要查询的字符串开头和结尾拼上`%`

```clojure
(require '[redsql.helpers :as hs])

(-> (hs/select :*)
    (hs/from :user)
    (hs/where (hs/like :name “tom”)))

;;等价 {:select [:*] :from [:user] :where [:like :name "%tom%"]}
```

like函数也能应用在以"simple"命名的api中

```clojure
(require '[redsql.helpers :as hs])
(require '[redsql.core :as redsql])

(redsql/get-simple-list :t_user {:name (hs/like "tom")
```

## like-left
like查询, 会自动给要查询的字符串开头拼上`%`

用法参考[like](#like)

## like-right
like查询, 会自动给要查询的字符串结尾拼上`%`

用法参考[like](#like)

## <a id="pagination">pagination</a>
分页查询，自动计算`:offset`,`:limit`的值

```clojure
(def sqlmap {:select [:*]
             :from [:user]})

(pagination sqlmap 0 10)
;;=> {:select [:*] :from [:user] :offset 0 :limit 10}

(pagination sqlmap 1 10)
;;=> {:select [:*] :from [:user] :offset 10 :limit 10}
```

## is-null

值为null

```clojure
(require '[redsql.helpers :as hs])

(-> (hs/select :*)
    (hs/from :user)
    (hs/where (hs/is-null :name)))

;;等价 {:select [:*] :from [:user] :where [:= :name nil]}
```

## is-not-null

值不为null

```clojure
(require '[redsql.helpers :as hs])

(-> (hs/select :*)
    (hs/from :user)
    (hs/where (hs/is-not-null :name)))

;;等价 {:select [:*] :from [:user] :where [:not= :name nil]}
```
