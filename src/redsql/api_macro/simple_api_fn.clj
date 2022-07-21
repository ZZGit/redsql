(ns redsql.api-macro.simple-api-fn
  (:require
   [redsql.build-sql.core :as build]
   [redsql.api-macro.core-api-fn :as core-api]))

(defn get-simple-one
  [ns conn table params opt]
  (let [sqlmap (build/build-simple-sqlmap table params opt)]
    (core-api/get-one ns conn sqlmap)))

(defn get-simple-list
  [ns conn table params opt]
  (let [sqlmap (build/build-simple-sqlmap table params opt)]
    (core-api/get-list ns conn sqlmap)))

(defn get-simple-count
  [ns conn table params opt]
  (let [sqlmap (build/build-simple-sqlmap table params opt)]
    (core-api/get-count ns conn sqlmap)))

(defn simple-delete!
  [ns conn table params opt]
  (let [sqlmap (build/build-simple-delete-sqlmap table params opt)]
    (core-api/delete! ns conn sqlmap)))
