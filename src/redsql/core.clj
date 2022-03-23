(ns redsql.core
  (:require
   [redsql.api :refer [def-api def-transaction]]))

(def-api)

(defmacro with-transaction [& args]
  (def-transaction :redsql.core args))
