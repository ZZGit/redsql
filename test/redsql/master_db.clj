(ns redsql.master-db
  (:require
   [redsql.api :refer [def-api def-transaction]]))

(def-api)

(defmacro with-transaction [& args]
  (def-transaction :redsql.master-db args))
