(ns redsql.api-macro.core
  (:require
   [redsql.config :as config]
   [redsql.connection :as connection]
   [redsql.api-macro.core-api-fn]
   [redsql.api-macro.simple-api-fn]))

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

(defn- get-pulic-funs-metadata
  "获取namespace下的所有访问权限为public的函数的元数据和函数引用
   举例: (get-pulic-funs-metadata 'redsql.api-macro.core-api-fn)
   返回结果: [
                [{:arglists ([ns conn sqlmap]),
                  :name get-list,
                  :doc \"get-list\"}
                #'redsql.api-macro.core-api-fn/get-list]

                [{:arglists ([ns conn sqlmap]),
                  :name get-one,
                  :doc \"get-one\"}
                 #'redsql.api-macro.core-api-fn/get-one]
              ]
  "
  [ns]
  (map
   (fn [[_ v]]
     (let [metadata (select-keys (meta v) [:arglists :doc :name])]
       [metadata v]))
   (ns-publics ns)))

(defn intern-fn
  [ns fname meta f]
  (let [meta-name (with-meta (symbol (name fname)) meta)]
    (intern ns meta-name f)))
x
(defn def-core-api-funs [ns]
  (let [core-api-ns 'redsql.api-macro.core-api-fn
        fsm (get-pulic-funs-metadata core-api-ns)]
    (doseq [[meta f] fsm]
      (intern-fn
       *ns* (:name meta) meta
       (fn
         ([params]
          (f ns nil params))
         ([conn params] (f ns conn params)))))))

(defn def-simple-api-funs [ns]
  (let [simple-api-ns 'redsql.api-macro.simple-api-fn
        fsm (get-pulic-funs-metadata simple-api-ns)]
    (doseq [[meta f] fsm]
      (intern-fn
       *ns* (:name meta) meta
       (fn
         ([table] (f ns nil table nil nil))
         ([table params] (f ns nil table params nil))
         ([table params opt] (f ns nil table params opt))
         ([conn table params opt] (f ns conn table params opt)))))))

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
