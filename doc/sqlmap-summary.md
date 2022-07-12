# 常用sqlmap总结

honeysql是通过clojure的map的数据结构来表示sql,简称sqlmap.下面总结了常用sqlmap

## 查询语句

```clojure
{:select [:*]
 :from [:t_user]}

;;等价 SELECT * FROM t_user
```

## 查询特定几个字段

```clojure
{:select [:name :age]
 :from [:t_user]}

;;等价 SELECT name,age FROM t_user
```

## 条件查询
```clojure
{:select [:*]
 :from [:t_user]
 :where [:= :name "tom"]}

;;等价 SELECT * FROM t_user WHERE name = 'tom'
```

## 模糊查询
```clojure
{:select [:*]
 :from [:t_user]
 :where [:like :name "%tom%"]}

;;等价 SELECT * FROM t_user WHERE name LIKE '%tom%'
```

## and查询
```clojure
{:select [:*]
 :from [:t_user]
 :where [:and
          [:< :age 20]
          [:= :name "tom"]]}

;;等价 SELECT * FROM t_user WHERE age < 20 AND name = 'tom'
```

## or查询
```clojure
{:select [:*]
 :from [:t_user]
 :where [:or
           [:< :age 20]
           [:= :name "tom"]]}

;;等价 SELECT * FROM t_user WHERE age < 20 OR name = 'tom'
```

## 条件嵌套
```clojure
{:select [:*]
 :from [:t_user]
 :where [:and
          [:= :name "tom"]
            [:or
              [:<= :age 20]
              [:>= :age 30]]]}

;;等价  SELECT * FROM t_user WHERE name = 'tom' AND (age <= 20 OR age >= 30)
```

## join查询

```clojure
{:select [:*]
 :from [[:t_order :o]]
 :join [[:t_user :u] [:= :u.order_id :o.id]]
 :where [:= :u.name "tom"]}

;;等价 SELECT * FROM t_order AS o INNER JOIN t_user AS u ON u.order_id = u.id WHERE u.name = 'tom'
```

## left-join查询
```clojure
{:select [:*]
 :from [[:t_order :o]]
 :left-join [[:t_user :u] [:= :u.order_id :o.id]]
 :where [:= :u.name "tom"]}

;;等价 SELECT * FROM t_order AS o LEFT JOIN t_user AS u ON u.order_id = u.id WHERE u.name = 'tom'
```

## right-join查询
```clojure
{:select [:*]
 :from [[:t_order :o]]
 :right-join [[:t_user :u] [:= :u.order_id :o.id]]
 :where [:= :u.name "tom"]}

;;等价 SELECT * FROM t_order AS o RIGHT JOIN t_user AS u ON u.order_id = u.id WHERE u.name = 'tom'
```

## 分组查询
```clojure
{:select [:*]
 :from [:t_user]
 :group-by [:name]}

;;等价 SELECT * FROM t_user GROUP BY name
```

## 排序查询
```clojure
{:select [:*]
 :from [:t_user]
 :order-by [[:age :asc] [:name :desc]]}

;;等价 SELECT * FROM t_user ORDER BY age ASC, name DESC
```


## 更新
```clojure
{:update :t_user
 :set {:name "tom2"},
 :where [:= :id 1]}

;;等价 UPDATE t_user SET name='tom2' WHERE id=1
```

## 删除
```clojure
{:delete-from [:t_user]
 :where [:= :name "tom"]}

;;等价 DELETE FROM t_user WHERE name='tom'
```
