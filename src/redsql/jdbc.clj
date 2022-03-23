(ns redsql.jdbc
  (:require
   [next.jdbc :as jdbc]
   [next.jdbc.result-set :as rs]))

(def ^:private default-opt
  {:return-keys true
   :builder-fn rs/as-unqualified-maps})

(defn jdbc-execute!
  [conn sql opt]
  (let [options (merge default-opt opt)]
    (jdbc/execute! conn sql options)))

(defn jdbc-execute-one!
  [conn sql opt]
  (let [options (merge default-opt opt)]
    (jdbc/execute-one! conn sql options)))
