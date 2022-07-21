(ns redsql.api
  (:require
   [redsql.api-macro.core :as api-macro]))

(defmacro def-api []
  (let [ns (keyword (ns-name *ns*))
        api-macros [api-macro/def-core-api-funs
                    api-macro/def-simple-api-funs
                    api-macro/def-other-api-funs
                    api-macro/def-with-transaction-macro]]
    `(do ~@(map (fn [f] (f ns)) api-macros))))
