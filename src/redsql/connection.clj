(ns redsql.connection
  (:require
   [next.jdbc :as jdbc]
   [next.jdbc.connection :as connection])
  (:import
   (com.zaxxer.hikari HikariDataSource)))

(def ^:private datasource (atom {}))

;; 事务数据源
(def ^:dynamic *tds* nil)

(defn- set-ds [ns-key ds]
  (swap! datasource #(assoc % ns-key ds)))

(defn get-ds [ns-key]
  (or *tds* (get @datasource ns-key)))

(defn get-ns-ds [ns-key]
  (get @datasource ns-key))

(defn connect! [ns-key db-spec]
  (let [ds (connection/->pool HikariDataSource db-spec)]
    (.close (jdbc/get-connection ds))
    (set-ds ns-key ds)))

(defn disconnect! [ns-key]
  (let [ds (get-ds ns-key)]
    (when (and (instance? HikariDataSource ds)
               (not (.isClosed ds)))
      (.close ds))))
