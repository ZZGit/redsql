# 多数据源


## 原理

redsql核心api都是通过宏`def-api`来生成的

打开`redsql.core.clj`文件可以看到非常简单的代码

```clojure
(ns redsql.core
  (:require
   [redsql.api :refer [def-api]]))

(def-api)
```

通过`def-api`宏我们可以非常方便为不同namesapce生成api, 通过调用不同namespace下api来区分的不同数据源的操作

## 多数据源操作

下面我们来创建两个数据源作为例子

### 新建文件master-db.clj

内容如下

```clojure
(ns customns.master-db
  (:require
   [redsql.api :refer [def-api]]))

(def-api)
```

### 新建文件slaver-db.clj

内容如下
```clojure
(ns customns.slaver-db
  (:require
   [redsql.api :refer [def-api]]))

(def-api)
```


### 多数据源操作

```clojure
(require '[customns.master-db :as master])
(require '[customns.slaver-db :as slaver])

;; master数据源配置
(def master-db-spec
  {:jdbcUrl "jdbc:h2:./master"})

;; master数据源操作
(master/connect! master-db-spec)
(master/get-simple-list :user {:name "tom"})
(master/disconnect! master-db-spec)

;; slaver数据源配置
(def slaver-db-spec
  {:jdbcUrl "jdbc:h2:./slaver"})

;; slaver数据源操作
(slaver/connect! slaver-db-spec)
(slaver/get-simple-list :user {:name "tom"})
(slaver/disconnect! master-db-spec)
```
