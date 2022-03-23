(ns redsql.api
  (:require
   [redsql.jdbc :as jdbc]
   [redsql.config :as config]
   [redsql.build-sql.core :as build]
   [redsql.connection :as connection]
   [next.jdbc.result-set :as rs]))

(defn- insert!
  [ns conn {:keys [table record] :as params}]
  (let [ds (or conn (connection/get-ds ns))
        opt (config/get-config ns (meta params))
        sql (build/build-insert-sql table [record] opt)]
    (jdbc/jdbc-execute-one! ds sql opt)))

(defn- insert-multi!
  [ns conn {:keys [table records] :as params}]
  (let [ds (or conn (connection/get-ds ns))
        opt (config/get-config ns (meta params))
        sql (build/build-insert-sql table records opt)]
    (jdbc/jdbc-execute! ds sql opt)))

(defn- update!
  [ns conn sqlmap]
  (let [ds (or conn (connection/get-ds ns))
        opt (config/get-config ns (meta sqlmap))
        sql (build/build-update-sql sqlmap opt)]
    (jdbc/jdbc-execute! ds sql opt)))

(defn- delete!
  [ns conn sqlmap]
  (let [ds (or conn (connection/get-ds ns))
        opt (config/get-config ns (meta sqlmap))
        sql (build/build-delete-sql sqlmap opt)]
    (jdbc/jdbc-execute! ds sql opt)))

(defn- get-one
  [ns conn sqlmap]
  (let [ds (or conn (connection/get-ds ns))
        opt (config/get-config ns (meta sqlmap))
        sql (build/build-query-sql sqlmap opt)]
    (jdbc/jdbc-execute-one! ds sql opt)))

(defn- get-list
  [ns conn sqlmap]
  (let [ds (or conn (connection/get-ds ns))
        opt (config/get-config ns (meta sqlmap))
        sql (build/build-query-sql sqlmap opt)]
    (jdbc/jdbc-execute! ds sql opt)))

(defn- get-count
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
  (if (or (zero? count)
          (zero? size))
    0
    (int (Math/ceil (/ count size)))))

(defn- get-page
  [ns conn sqlmap]
  (let [ct (get-page-count ns conn sqlmap)
        {size :limit offset :offset} sqlmap]
    {:rows (get-list ns conn sqlmap)
     :page (int (/ offset size))
     :size size
     :total-count ct
     :total-page (get-total-page ct size)}))

(defn- get-simple-one
  [ns conn table params opt]
  (let [sqlmap (build/build-simple-sqlmap table params opt)]
    (get-one ns conn sqlmap)))

(defn- get-simple-list
  [ns conn table params opt]
  (let [sqlmap (build/build-simple-sqlmap table params opt)]
    (get-list ns conn sqlmap)))

(defn- get-simple-count
  [ns conn table params opt]
  (let [sqlmap (build/build-simple-sqlmap table params opt)]
    (get-count ns conn sqlmap)))

(defn- simple-delete!
  [ns conn table params opt]
  (let [sqlmap (build/build-simple-delete-sqlmap table params opt)]
    (delete! ns conn sqlmap)))

(def ^:private api-funs
  [{:id "insert!"
    :meta {:doc "insert one record into table"}
    :fn (fn [ns conn params]
          (insert! ns conn params))}
   {:id "insert-multi!"
    :meta {:doc "insert multiple record into table"}
    :fn (fn [ns conn params]
          (insert-multi! ns conn params))}
   {:id "update!"
    :meta {:doc "update table"}
    :fn (fn [ns conn params]
          (update! ns conn params))}
   {:id "delete!"
    :meta {:doc "delete!"}
    :fn (fn [ns conn params]
          (delete! ns conn params))}
   {:id "get-one"
    :meta {:doc "query one record"}
    :fn (fn [ns conn params]
          (get-one ns conn params))}
   {:id "get-list"
    :meta {:doc "query records"}
    :fn (fn [ns conn params]
          (get-list ns conn params))}
   {:id "get-count"
    :meta {:doc "query count"}
    :fn (fn [ns conn params]
          (get-count ns conn params))}
   {:id "get-page"
    :meta {:doc "query page"}
    :fn (fn [ns conn params]
          (get-page ns conn params))}
   ])

(def ^:private simple-api-funs
  [{:id "get-simple-one"
    :meta {:doc ""}
    :fn (fn [ns conn table params opt]
          (get-simple-one ns conn table params opt))}
   {:id "get-simple-list"
    :meta {:doc ""}
    :fn (fn [ns conn table params opt]
          (get-simple-list ns conn table params opt))}
   {:id "get-simple-count"
    :meta {:doc ""}
    :fn (fn [ns conn table params opt]
          (get-simple-count ns conn table params opt))}
   {:id "simple-delete!"
    :meta {:doc "simple-delete!"}
    :fn (fn [ns conn table params opt]
          (simple-delete! ns conn table params opt))}])

(def ^:private connect-fun
  {:id "connect!"
   :meta {:doc ""}
   :fn (fn [ds-key db-spec]
         (connection/connect! ds-key db-spec))})

(def ^:private set-config-fun
  {:id "set-config"
   :meta {:doc ""}
   :fn (fn [ds-key cfg]
         (config/set-ds-config ds-key cfg))})

(def ^:private close-funs
  [{:id "disconnect!"
    :meta {:doc ""}
    :fn (fn [ds-key]
          (connection/disconnect! ds-key))}
   {:id "clear-config"
    :meta {:doc ""}
    :fn (fn [ds-key]
          (config/clear-ds-config ds-key))}])

(defn intern-fn [ns id meta f]
  (intern ns (with-meta (symbol (name id)) meta) f))

(defmacro def-api []
  (let [ns (keyword (ns-name *ns*))]
    `(do
       (doseq [{id# :id meta# :meta fn# :fn} ~api-funs]
         (redsql.api/intern-fn
          *ns* id#
          (assoc meta#
                 :arglists (quote ~'([params] [conn params])))
          (fn f#
            ([params#]
             (fn# ~ns nil params#))
            ([conn# params#] (fn# ~ns conn# params#)))))
       (doseq [{id# :id meta# :meta fn# :fn} ~simple-api-funs]
         (redsql.api/intern-fn
          *ns* id#
          (assoc meta#
                 :arglists (quote ~'([table]
                                     [table params]
                                     [table params opt]
                                     [conn table params opt])))
          (fn f#
            ([table#] (fn# ~ns nil table# nil nil))
            ([table# params#] (fn# ~ns nil table# params# nil))
            ([table# params# opt#] (fn# ~ns nil table# params# opt#))
            ([conn# table# params# opt#] (fn# ~ns conn# table# params# opt#)))))
       (let [{id# :id meta# :meta fn# :fn} ~connect-fun]
         (redsql.api/intern-fn
          *ns* id#
          (assoc meta#
                 :arglists (quote ~'([db-spec])))
          (fn f#
            ([db-spec#] (fn# ~ns db-spec#)))))
       (let [{id# :id meta# :meta fn# :fn} ~set-config-fun]
         (redsql.api/intern-fn
          *ns* id#
          (assoc meta#
                 :arglists (quote ~'([config])))
          (fn f#
            ([cfg#] (fn# ~ns cfg#)))))
       (doseq [{id# :id meta# :meta fn# :fn} ~close-funs]
         (redsql.api/intern-fn
          *ns* id#
          (assoc meta#
                 :arglists (quote ~'([])))
          (fn f#
            ([] (fn# ~ns)))))
       )))


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
           (into [tx# (redsql.connection/get-ns-ds ~ns-key)] (rest ~farg))
           (binding [redsql.connection/*tds* tx#]
             ~@(rest args)))
        `(next.jdbc/with-transaction
           ~farg
           (binding [redsql.connection/*tds* tx#]
             ~@(rest args))))
      `(next.jdbc/with-transaction [tx# (redsql.connection/get-ns-ds ~ns-key)]
         (binding [redsql.connection/*tds* tx#]
           ~@args)))))
