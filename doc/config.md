# 配置

## 数据源配置
数据源驱动使用了[HikariCP](https://github.com/brettwooldridge/HikariCP),数据源参数需要参考HikariCP，比如

```clojure
(require '[redsql.core :as redsql])

;;HikariCP数据源参数
(def db-spec
  {:jdbcUrl "jdbc:p6spy:h2:./demo"})

;; 连接
(redsql/connect! db-spec)

;; 关闭连接
(redsql/disconnect! db-spec)
```

## redsql配置

目前redsql支持下面的配置项

| 名称                   | 默认值 | 说明                                                                                                                                      |
|------------------------|--------|-------------------------------------------------------------------------------------------------------------------------------------------|
| logic-delete?          | false  | 是否启用逻辑删除, ture：启用；false:不启用                                                                                                |
| logic-delete-field     | nil    | 如果启用逻辑删除，逻辑删除的字段                                                                                                          |
| logic-delete-value     | nil    | 如果启用逻辑删除，代表逻辑删除的值                                                                                                        |
| logic-not-delete-value | nil    | 如果启用逻辑删除，代表逻辑不删除的值                                                                                                      |
| insert-fill-filed-fn   | nil    | 插入自动填充函数                                                                                                                          |
| update-fill-filed-fn   | nil    | 更新自动填充函数                                                                                                                                          |
| builder-fn             | nil    | 查询结果格式化函数，可以参考[next.js ResultSetBuilder](https://github.com/seancorfield/next-jdbc/blob/develop/doc/result-set-builders.md) |


redsql支持三种基本设置方式，分别为：全局级配置、数据源级配置、api级配置, 优先级为：api级配置 > 数据源级配置 > 全局级配置


### 全局级配置

全局级配置是所有数据源共享的配置,使用`set-global-config`来设置全局配置

```clojure
(require '[redsql.config :as config])
(require '[next.jdbc.result-set :as rs])

;; 全局配置项
(def config-opt
  {:logic-delete? true
   :logic-delete-field :delete_flag
   :logic-delete-value true
   :logic-not-delete-value false
   :builder-fn rs/as-unqualified-lower-maps})

;;使用
(config/set-global-config config-opt)
```

### 数据源级配置

数据源级配置是每个数据源独享的配置，可以为不同数据源配置不同的配置文件，多数据源的使用参考[使用多数据源](multi-datasource.md)

```clojure
(require '[custom-master-db-ns :as master-db])
(require '[custom-master-db-ns :as slaver-db])

(def master-config
  {:logic-delete? true
   :logic-delete-field :delete_flag
   :logic-delete-value true
   :logic-not-delete-value false
   :builder-fn rs/as-unqualified-lower-maps})

(def slaver-config
  {:logic-delete? false
   :builder-fn rs/as-unqualified-lower-maps})

;; master数据源设置
(master-db/set-config master-config)

;; slaver数据源配置
(slaver-db/set-config slaver-config)
```

### api级配置

可以为每个api单独指定配置

```clojure
(require '[redsql.core :as redsql])

(def logic-delete-opt
  {:logic-delete? true
   :logic-delete-field :delete_flag
   :logic-delete-value true
   :logic-not-delete-value false)

(def sqlmap {:select [:*]
             :from [:user]
             :where [:= :name "tom"]})

;; 使用with-meta携带配置信息
(redsql/get-list (with-meta sqlmap logic-delete-opt))


;; simple命名的函数，配置数据作为最后一个参数传入
(redsql/get-simple-list :user {:name "tom"} logic-delete-opt)
```

