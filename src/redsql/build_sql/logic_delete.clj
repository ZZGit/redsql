(ns redsql.build-sql.logic-delete
  (:require
   [honey.sql :as sql]
   [honey.sql.helpers :as helpers]
   [redsql.build-sql.util :as util]))

(defn logic-delete?
  "是否逻辑删除"
  [opt]
  (:logic-delete? opt))

(defn- create-logic-delete-condition
  "创建逻辑查询条件"
  ([opt]
   [:=
    (:logic-delete-field opt)
    (:logic-not-delete-value opt)])
  ([table-key opt]
   [:=
    (util/get-table-ns-name-key table-key (:logic-delete-field opt))
    (:logic-not-delete-value opt)]))

(defn create-logic-delete-prop
  "创建逻辑删除属性"
  [opt]
  {(:logic-delete-field opt) (:logic-delete-value opt)})

(defn create-not-logic-delete-prop
  "创建逻辑删除属性"
  [opt]
  {(:logic-delete-field opt) (:logic-not-delete-value opt)})

(defn- create-logic-delete-select-sqlmap
  ":table_name => [{:select [:*]
                    :from [:table_name]
                    :where {:delete_falg true})} :table_name]
  [:table_name :t] => [{:select [:*]
                        :from [:table_name]
                        :where {:delete_falg true})} :t]"
  [table-form opt]
  (let [tk (util/get-table-key table-form)
        ak (util/get-table-alias-key table-form)]
    [{:select [:*]
      :from [tk]
      :where (create-logic-delete-condition opt)} ak]))


(defn- update-join-sqlmap-from [from-val opt]
  (map
   #(create-logic-delete-select-sqlmap % opt)
   from-val))

(defn- update-join-sqlmap-join [join-val opt]
  (reduce
   #(into %1 %2)
   []
   (mapv
    (fn [[k v]]
      [(create-logic-delete-select-sqlmap k opt) v])
    (partition 2 join-val))))

(defn- add-join-logic-delete
  "连接查询的sqlmap加入逻辑删除条件"
  [sqlmap opt]
  (-> sqlmap
      (update :from #(update-join-sqlmap-from % opt))
      (update :join #(update-join-sqlmap-join % opt))
      (update :left-join #(update-join-sqlmap-join % opt))
      (update :right-join #(update-join-sqlmap-join % opt))))

(defn- add-logic-delete
  "不是连接查询的sqlmap加入逻辑删除条件"
  [sqlmap opt]
  (->> (:from sqlmap)
       (map util/get-table-alias-key)
       (map #(create-logic-delete-condition % opt))
       (reduce helpers/where sqlmap)))

(defn add-sqlmap-logic-delete
  "sqlmap中加入逻辑删除的条件"
  [sqlmap opt]
  (if (logic-delete? opt)
    (if (util/is-join-sqlmap? sqlmap)
      (add-join-logic-delete sqlmap opt)
      (add-logic-delete sqlmap opt))
    sqlmap))

(defn add-update-sqlmap-logic-delete
  "update sqlmap中加入逻辑删除的条件"
  [sqlmap opt]
  (let [table-key (:update sqlmap)]
    (if (logic-delete? opt)
      (helpers/where
       sqlmap
       (create-logic-delete-condition table-key opt))
      sqlmap)))

(defn add-row-logic-delete
  "插入数据添加逻辑删除属性"
  [row opt]
  (if (logic-delete? opt)
    (merge row (create-not-logic-delete-prop opt))
    row))
