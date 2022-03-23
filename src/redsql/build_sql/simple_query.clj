(ns redsql.build-sql.simple-query
  (:require
   [honey.sql.helpers :as helper]))

(defn- mp->vp
  "{:name \"hh\"
    :age {:> 12}}
  =>
  [[:= :name \"hh\"]
   [:> :age 12]]
  "
  [props]
  (map
   (fn [[k v]]
     (if (map? v)
       [(first (keys v)) k (first (vals v))]
       [:= k v]))
   props))

(defn ->query-sqlmap
  [table params opt]
  (let [base-sqlmap {:select [:*]
                     :from [(keyword table)]}
        sqlmap (apply
                helper/where
                (into [base-sqlmap] (mp->vp params)))]
    (with-meta sqlmap opt)))

(defn ->delete-sqlmap
  [table params opt]
  (let [base-sqlmap {:delete-from [(keyword table)]}
        sqlmap (apply
                helper/where
                (into [base-sqlmap] (mp->vp params)))]
    (with-meta sqlmap opt)))
