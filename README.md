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

而redsql借助next-jdbc,对外提供CURD接口,负责执行sqlmap.除此之外,还提供了多数据源,逻辑删除,字段自动填充等功能.

## [快速开始](./doc/getting-started.md)

## [常用sqlmap总结](./doc/sqlmap-summary.md)

## [使用helper函数构造sqlmap](./doc/use-helper-build-sqlmap.md)

## CURD接口

## 配置

## 扩展

### 多数据源

### 逻辑删除

### 字段自动填充
