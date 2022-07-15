# CURD接口

## insert!

插入单条记录

```clojure
(require '[redsql.core :as redsql])

(def user {:id "999"
           :username "liubei"
           :password "123456"
           :realname "刘备"
           :email "liubei@shu.com"})

(redsql/insert!{:table :t_user
                :record user})
```

## insert-multi!

批量插入

```clojure
(require '[redsql.core :as redsql])

(def users [{:id "999"
             :username "liubei"
             :password "123456"
             :realname "multi-user"
             :email "liubei@shu.com"}
            {:id "1000"
             :username "zhugeliang"
             :password "123456"
             :realname "multi-user"
             :email "zhugeliang@shu.com"}])

(redsql/insert-multi! {:table :t_user
                       :records users})
```

## update!

更新

```clojure
(require '[redsql.core :as redsql])

(def sqlmap {:update :t_user
             :set {:name "tom"
                   :email "tom@qq.com"}
             :where [:= :id 2]})

(redsql/update! sqlmap)
```

## delete!

删除

```clojure
(require '[redsql.core :as redsql])

(def sqlmap {:delete-from [:t_user]
             :where [:= :id "1"]})

(redsql/delete! sqlmap)
```



## get-list

get-list接收sqlmap作为参数,返回多个记录,查询不断返回`[]`

```clojure
(require '[redsql.core :as redsql])

(def sqlmap {:select [:*]
             :from [:t_user]
             :where [:like :name "tom%"]})

(redsql/get-list sqlmap)
```

输出
```clojure
[
  {:id "1" :name "tom1"}
  {:id "2" :name "tom2"}
  {:id "3" :name "tom3"}
]
```

## get-one

get-one接收sqlmap作为参数,返回单个记录,查询不到返回`nil`

```clojure
(require '[redsql.core :as redsql])

(def sqlmap {:select [:*]
             :from [:t_user]
             :where [:like :name "tom%"]})

(redsql/get-one sqlmap)
```

输出
```clojure
{:id "1" :name "tom1"}
```

## get-count

get-count接收sqlmap作为参数,返回查询记录的数量,查询不到返回0

```clojure
(require '[redsql.core :as redsql])

(def sqlmap {:select [:*]
             :from [:t_user]
             :where [:like :name "tom%"]})

(redsql/get-count sqlmap)
```

输出
```clojure
3
```

## get-page

get-page接收sqlmap作为参数,返回查询的记录和分页数据

```clojure
(require '[redsql.core :as redsql])

(def sqlmap {:select [:*]
             :from [:t_user]
             :where [:like :name "tom%"]
             :limit 5
             :offset 0})

(redsql/get-page sqlmap)

```

分页查询直接使用sqlmap会有点麻烦,offset的值需要根据当前页码和数量计算出来,而使用[pagination](use-helper-build-sqlmap.md#pagination)来构造sqlmap会省去这个麻烦,它会根据传入的参数,自动计算`:offset`的值

```clojure
(require '[redsql.helpers :as hs])
(require '[redsql.core :as redsql])

(def sqlmap (-> (hs/select :*)
                (hs/from :t_user)
                (hs/pagination 0 5)
                (hs/where
                    (hs/like :name "tom")))

(redsql/get-page sqlmap)

```


返回
```clojure
{:rows [{:id "1" :name "tom1"}
        {:id "2" :name "tom2"}
        {:id "3" :name "tom3"}]
 :page 0
 :size 5
 :total-count 3
 :total-page 1
}
```

返回结果说明

| 属性        | 说明               |
| ---         | ---                |
| rows        | 分页查询返回的列表 |
| page        | 当前的页码,从0开始 |
| size        | 每页显示的数量     |
| total-count | 总条数             |
| total-page  | 总页码数           |


## <a id="get-simple-list">get-simple-list<a>

当查询单表,而且条件只有and组合,我们可以使用`get-simple-list`来替代`get-list`

```clojure
(require '[redsql.core :as redsql])
(require '[redsql.helpers :as hs])

(redsql/get-simple-list :t_user {:name "tom"})
(redsql/get-simple-list :t_user {:name {:= "tom"}})
(redsql/get-simple-list :t_user {:name (hs/eq "tom")
;; 上面三种形式是等价的,条件数据默认规则是就是相等,推荐第一种写法

;;=> SELECT * FROM t_user WHERE name = 'tom'

(redsql/get-simple-list :t_user {:name "tom" :age 10})

;;=> SELECT * FROM t_user WHERE name = 'tom' AND age = 10


(redsql/get-simple-list :t_user {:name "tom" :age {:> 10}})
(redsql/get-simple-list :t_user {:name "tom" :age (hs/gt 10)

;;=> SELECT * FROM t_user WHERE name = 'tom' AND age = 10


(redsql/get-simple-list :t_user {:name {:like "%tom%"} :age 10})
(redsql/get-simple-list :t_user {:name (hs/like "tom") :age 10})

;;=> SELECT * FROM t_user WHERE name like '%tom%' AND age = 10
```

## get-simple-one

当查询单表,而且条件只有and组合,我们可以使用`get-simple-one`来替代`get-one`

用法可以参考[get-simple-list](#get-simple-list)

## get-simple-count

当查询单表,而且条件只有and组合,我们可以使用`get-simple-count`来替代`get-count`

用法可以参考[get-simple-list](#get-simple-list)

## simple-delete!

当删除单表,而且条件只有and组合,我们可以使用`simple-delete!`来替代`delete!`

用法可以参考[get-simple-list](#get-simple-list)
