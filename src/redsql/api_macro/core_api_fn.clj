(ns redsql.api-macro.core-api-fn
  (:require
   [redsql.jdbc :as jdbc]
   [redsql.config :as config]
   [redsql.build-sql.core :as build]
   [redsql.connection :as connection]
   [next.jdbc.result-set :as rs]))

(defn insert!
  "insert record"
  [ns conn {:keys [table record] :as params}]
  (let [ds (or conn (connection/get-ds ns))
        opt (config/get-config ns (meta params))
        sql (build/build-insert-sql table [record] opt)]
    (jdbc/jdbc-execute-one! ds sql opt)))

(defn insert-multi!
  "insert multi records"
  [ns conn {:keys [table records] :as params}]
  (let [ds (or conn (connection/get-ds ns))
        opt (config/get-config ns (meta params))
        sql (build/build-insert-sql table records opt)]
    (jdbc/jdbc-execute! ds sql opt)))

(defn update!
  [ns conn sqlmap]
  (let [ds (or conn (connection/get-ds ns))
        opt (config/get-config ns (meta sqlmap))
        sql (build/build-update-sql sqlmap opt)]
    (jdbc/jdbc-execute! ds sql opt)))

(defn delete!
  [ns conn sqlmap]
  (let [ds (or conn (connection/get-ds ns))
        opt (config/get-config ns (meta sqlmap))
        sql (build/build-delete-sql sqlmap opt)]
    (jdbc/jdbc-execute! ds sql opt)))

(defn get-one
  [ns conn sqlmap]
  (let [ds (or conn (connection/get-ds ns))
        opt (config/get-config ns (meta sqlmap))
        sql (build/build-query-sql sqlmap opt)]
    (jdbc/jdbc-execute-one! ds sql opt)))

(defn get-list
  [ns conn sqlmap]
  (let [ds (or conn (connection/get-ds ns))
        opt (config/get-config ns (meta sqlmap))
        sql (build/build-query-sql sqlmap opt)]
    (jdbc/jdbc-execute! ds sql opt)))

(defn get-count
  [ns conn sqlmap]
  (let [ds (or conn (connection/get-ds ns))
        opt (merge
             (config/get-config ns (meta sqlmap))
             {:builder-fn rs/as-unqualified-lower-maps})
        sql (build/build-count-sql sqlmap opt)]
    (:count
     (jdbc/jdbc-execute-one! ds sql opt))))

(defn- get-page-count
  [ns conn sqlmap]
  (let [sp (dissoc sqlmap :limit :offset :order-by :group-by)]
    (get-count ns conn sp)))

(defn- get-total-page
  [count size]
  (if (or (zero? count) (zero? size))
    0
    (int (Math/ceil (/ count size)))))

(defn get-page
  [ns conn sqlmap]
  (let [ct (get-page-count ns conn sqlmap)
        {size :limit offset :offset} sqlmap]
    {:rows (get-list ns conn sqlmap)
     :page (int (/ offset size))
     :size size
     :total-count ct
     :total-page (get-total-page ct size)}))
