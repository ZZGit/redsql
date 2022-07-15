# redsql [![Clojure Test CI](https://github.com/ZZGit/redsql/actions/workflows/test.yml/badge.svg)](https://github.com/ZZGit/redsql/actions/workflows/test.yml) [![codecov](https://codecov.io/gh/ZZGit/redsql/branch/main/graph/badge.svg?token=9LU5MCSHCX)](https://codecov.io/gh/ZZGit/redsql)

[![Clojars Project](https://img.shields.io/clojars/v/org.clojars.redcreation/redsql.svg)](https://clojars.org/org.clojars.redcreation/redsql)

redsql是[honeysql](https://github.com/seancorfield/honeysql) + [next-jdbc
](https://github.com/seancorfield/next-jdbc)的数据库操作的工具。

## 简介

honeysql只负责将clojure的map结构(称为sqlmap),格式化成sql.比如:
```clojure
(require '[honey.sql :as sql])

(def sqlmap {:select [:a :b :c]
             :from   [:foo]
             :where  [:= :foo.a "baz"]})

(sql/format sqlmap)

=> ["SELECT a, b, c FROM foo WHERE foo.a = ?" "baz"]
```

而redsql借助next-jdbc,对外提供CURD接口,负责执行sqlmap.


```clojure
(require '[redsql.core :as redsql])

;; 数据源配置
(def db-spec
  {:jdbcUrl "jdbc:h2:./demo"})

;; 连接
(redsql/connect! db-spec)

;; 查询
(def sqlmap {:select [:id :name]
             :from   [:user]
             :where  [:= :name "tom"]})
(redsql/get-one sqlmap)

;; 关闭连接
(redsql/disconnect! db-spec)
```
返回结果

```clojure
{:id "1" :name "tom"}
```

除此之外,还提供了多数据源,逻辑删除,字段自动填充等功能.

## [快速开始](./doc/getting-started.md)

## [常用sqlmap总结](./doc/sqlmap-summary.md)

## [CURD接口](./doc/curd-api.md)

## [事务操作](./doc/with-transaction.md)

## [使用helper函数构造sqlmap](./doc/use-helper-build-sqlmap.md)

## [配置](./doc/config.md)

### [多数据源](./doc/multi-datasource.md)

### [逻辑删除](./doc/logic-delete.md)

### [字段自动填充](./doc/fill-filed.md)
