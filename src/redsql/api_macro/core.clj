(ns redsql.api-macro.core
  (:require
   [redsql.config :as config]
   [redsql.connection :as connection]
   [redsql.api-macro.core-api-fn :as core-api]
   [redsql.api-macro.simple-api-fn :as simple-api]))

(def ^:private core-api-funs
  [{:id "insert!"
    :meta {:doc "insert one record into table"}
    :fn (fn [ns conn params]
          (core-api/insert! ns conn params))}
   {:id "insert-multi!"
    :meta {:doc "insert multiple record into table"}
    :fn (fn [ns conn params]
          (core-api/insert-multi! ns conn params))}
   {:id "update!"
    :meta {:doc "update table"}
    :fn (fn [ns conn params]
          (core-api/update! ns conn params))}
   {:id "delete!"
    :meta {:doc "delete!"}
    :fn (fn [ns conn params]
          (core-api/delete! ns conn params))}
   {:id "get-one"
    :meta {:doc "query one record"}
    :fn (fn [ns conn params]
          (core-api/get-one ns conn params))}
   {:id "get-list"
    :meta {:doc "query records"}
    :fn (fn [ns conn params]
          (core-api/get-list ns conn params))}
   {:id "get-count"
    :meta {:doc "query count"}
    :fn (fn [ns conn params]
          (core-api/get-count ns conn params))}
   {:id "get-page"
    :meta {:doc "query page"}
    :fn (fn [ns conn params]
          (core-api/get-page ns conn params))}])

(def ^:private simple-api-funs
  [{:id "get-simple-one"
    :meta {:doc ""}
    :fn (fn [ns conn table params opt]
          (simple-api/get-simple-one ns conn table params opt))}
   {:id "get-simple-list"
    :meta {:doc ""}
    :fn (fn [ns conn table params opt]
          (simple-api/get-simple-list ns conn table params opt))}
   {:id "get-simple-count"
    :meta {:doc ""}
    :fn (fn [ns conn table params opt]
          (simple-api/get-simple-count ns conn table params opt))}
   {:id "simple-delete!"
    :meta {:doc "simple-delete!"}
    :fn (fn [ns conn table params opt]
          (simple-api/simple-delete! ns conn table params opt))}])

(def ^:private one-arg-api
  [{:id "connect!"
    :meta {:doc ""}
    :fn (fn [ns db-spec]
          (connection/connect! ns db-spec))}
   {:id "set-config"
    :meta {:doc ""}
    :fn (fn [ns cfg]
          (config/set-ds-config ns cfg))}])

(def ^:private no-arg-api
  [{:id "disconnect!"
    :meta {:doc ""}
    :fn (fn [ns]
          (connection/disconnect! ns))}
   {:id "clear-config"
    :meta {:doc ""}
    :fn (fn [ns]
          (config/clear-ds-config ns))}])

(defn intern-fn [ns id meta f]
  (intern ns (with-meta (symbol (name id)) meta) f))

(defn def-core-api-funs [ns]
  `(doseq [{id# :id meta# :meta fn# :fn} ~core-api-funs]
     (intern-fn
      *ns* id# meta#
      (fn f#
        ([params#]
         (fn# ~ns nil params#))
        ([conn# params#] (fn# ~ns conn# params#))))))

(defn def-simple-api-funs [ns]
  `(doseq [{id# :id meta# :meta fn# :fn} ~simple-api-funs]
     (intern-fn
      *ns* id# meta#
      (fn f#
        ([table#] (fn# ~ns nil table# nil nil))
        ([table# params#] (fn# ~ns nil table# params# nil))
        ([table# params# opt#] (fn# ~ns nil table# params# opt#))
        ([conn# table# params# opt#] (fn# ~ns conn# table# params# opt#))))))

(defn def-other-api-funs [ns]
  `(do
     (doseq [{id# :id meta# :meta fn# :fn} ~one-arg-api]
       (intern-fn
        *ns* id# meta#
        (fn f# ([params#] (fn# ~ns params#)))))
     (doseq [{id# :id meta# :meta fn# :fn} ~no-arg-api]
       (intern-fn
        *ns* id# meta#
        (fn f# ([] (fn# ~ns)))))))

(defn def-transaction
  "数据库事务
   1. (with-transaction (...) (...))
   2. (with-transaction [opt] (...) (...))
   3. (with-transaction [ds opt] (...) (...)])"
  [ns-key args]
  (let [farg (first args)]
    (if (vector? farg)
      (if (map? (first farg))
        `(next.jdbc/with-transaction
           [tx# (redsql.connection/get-ns-ds ~ns-key) (rest ~farg)]
           (binding [redsql.connection/*tds* tx#]
             ~@(rest args)))
        `(next.jdbc/with-transaction
           ~farg
           (binding [redsql.connection/*tds* tx#]
             ~@(rest args))))
      `(next.jdbc/with-transaction [tx# (redsql.connection/get-ns-ds ~ns-key)]
         (binding [redsql.connection/*tds* tx#]
           ~@args)))))

(defn def-with-transaction-macro
  "create macro with-transaction"
  [ns]
  (let [macro-name (symbol "with-transaction")]
    `(defmacro ~macro-name [& body#]
       (def-transaction ~ns body#))))
