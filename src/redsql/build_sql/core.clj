(ns redsql.build-sql.core
  (:require
   [honey.sql :as sql]
   [honey.sql.helpers :as helpers]
   [clojure.set :refer [rename-keys]]
   [redsql.build-sql.logic-delete :as ld]
   [redsql.build-sql.fill-filed :as fill]
   [redsql.build-sql.simple-query :as simple]))


(def ^:private default-sql-format
  {:pretty true})

(defn- sql-format [sqlmap opt]
  (sql/format
   sqlmap
   (merge default-sql-format (:sql-format opt))))

(defn build-query-sql
  [sqlmap opt]
  (-> sqlmap
      (ld/add-sqlmap-logic-delete opt)
      (sql-format opt)))

(defn build-count-sql
  [sqlmap opt]
  (let [query-sqlmap (ld/add-sqlmap-logic-delete sqlmap opt)]
    (-> (helpers/select [:%count.* :count])
        (helpers/from query-sqlmap)
        (sql-format opt))))

(defn- ->insert-sql-value
  [record opt]
  (-> record
      (ld/add-row-logic-delete opt)
      (fill/add-insert-fill-filed opt)))

(defn build-insert-sql
  [table records opt]
  (-> (helpers/insert-into table)
      (helpers/values
       (map #(->insert-sql-value % opt) records))
      (sql-format opt)))

(defn build-update-sql
  [sqlmap opt]
  (-> sqlmap
      (ld/add-update-sqlmap-logic-delete opt)
      (update :set #(fill/add-update-fill-filed % opt))
      (sql-format opt)))

(defn- build-logic-delete-sql
  [sqlmap opt]
  (-> sqlmap
      (rename-keys {:delete-from :update})
      (assoc :set (ld/create-logic-delete-prop opt))
      (sql-format opt)))

(defn- build-physics-deldete-sql
  [sqlmap opt]
  (sql-format sqlmap opt))

(defn build-delete-sql
  [sqlmap opt]
  (if (ld/logic-delete? opt)
    (build-logic-delete-sql sqlmap opt)
    (build-physics-deldete-sql sqlmap opt)))

(defn build-simple-sqlmap
  [table params opt]
  (simple/->query-sqlmap table params opt))

(defn build-simple-delete-sqlmap
  [table params opt]
  (simple/->delete-sqlmap table params opt))
