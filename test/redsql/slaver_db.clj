(ns redsql.slaver-db
  (:require
   [redsql.api :refer [def-api def-transaction]]))

(def-api)

(defmacro with-transaction [& args]
  (def-transaction :redsql.slaver-db args))
