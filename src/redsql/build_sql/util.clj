(ns redsql.build-sql.util)


(defn- valid-join-key? [jk sqlmap]
  (let [v (jk sqlmap)]
    (and (vector? v) (pos? (count v)))))

(defn is-join-sqlmap?
  "是连接查询的sqlmap?"
  [sqlmap]
  (boolean
   (some
    #(valid-join-key? % sqlmap)
    [:join :left-join :right-join])))

(defn get-table-alias-key
  "获取表别名"
  [table]
  (if (vector? table)
    (second table)
    table))

(defn get-table-key
  "获取表名"
  [table]
  (if (vector? table)
    (first table)
    table))

(defn get-table-ns-name-key
  [table-ns-key name-key]
  (keyword (str (name table-ns-key) "." (name name-key))))
