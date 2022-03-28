(ns redsql.helper
  (:refer-clojure :exclude [set group-by])
  (:require
   [honey.sql :as sql]
   [honey.sql.helpers :as helpers]))


(def select helpers/select)
(def update-table helpers/update)
(def delete-from helpers/delete-from)
(def delete helpers/delete)
(def set helpers/set)
(def from helpers/from)
(def where helpers/where)
(def order-by helpers/order-by)
(def group-by helpers/group-by)
(def limit helpers/limit)
(def offset helpers/offset)
(def join helpers/join)
(def left-join helpers/left-join)
(def right-join helpers/right-join)
(def having helpers/having)

(def one-arg-helper-fns
  [{:fn-name "eq"
    :fn-key :=}
   {:fn-name "ne"
    :fn-key :<>}

   {:fn-name "gt"
    :fn-key :>}
   {:fn-name "ge"
    :fn-key :>=}

   {:fn-name "lt"
    :fn-key :<}
   {:fn-name "le"
    :fn-key :<=}

   {:fn-name "like"
    :fn-key :like
    :val-fn (fn [v] (str "%" v "%"))}
   {:fn-name "like-left"
    :fn-key :like
    :val-fn (fn [v] (str "%" v))}
   {:fn-name "like-right"
    :fn-key :like
    :val-fn (fn [v] (str  v "%"))}

   {:fn-name "is-null"
    :fn-key :=
    :val-fn (fn [v] nil)}
   {:fn-name "is-not-null"
    :fn-key :not=
    :val-fn (fn [v] nil)}

   {:fn-name "in"
    :fn-key :in}
   ])

(defn- def-one-arg-helper-fns
  [{:keys [fn-name fn-key val-fn]}]
  (let [f (symbol fn-name)
        vf (or val-fn identity)]
    `(do
       (defn ~f
         ([v#]
          {~fn-key (~vf v#)})
         ([k# v#]
          [~fn-key k# (~vf v#)])
         ([b# k# v#]
          (when b# (~f k# v#))))
       (defn ~(symbol (str fn-name "-condition"))
         [k# v#]
         (let [b# (if (string? v#) (empty? v#) (nil? v#))]
           (~f (not b#) k# v#))))))

(defmacro export-helper-fns []
  `(do ~@(map def-one-arg-helper-fns one-arg-helper-fns)))

(export-helper-fns)

(defn between
  "BETWEEN 值1 AND 值2"
  ([v1 v2]
   {:between [v1 v2]})
  ([k v1 v2]
   [:between k v1 v2])
  ([b k v1 v2]
   (when b (between k v1 v2))))

(defn pagination
  [sqlmap page size]
  (assoc sqlmap :offset (* page size)  :limit size))

(defn- valid-args [args]
  (remove nil? args))

(defn- get-where-args [args]
  (let [first-arg (first args)]
    (if (boolean? (first args))
      (when first-arg (valid-args (rest args)))
      (valid-args args))))

(defn OR
  "拼接 OR"
  [& args]
  (let [vargs (get-where-args args)]
    (when (seq vargs) (into [:or] vargs))))

(defn AND
  "拼接 AND"
  [& args]
  (let [vargs (get-where-args args)]
    (when (seq vargs) (into [:and] vargs))))

(defn- to-asc-vals [ks]
  (mapv (fn [k] [k :asc]) ks))

(defn order-by-asc
  "升序排序"
  [sqlmap & args]
  (apply helpers/order-by (into [sqlmap] (to-asc-vals args))))

(defn- to-desc-vals [ks]
  (mapv (fn [k] [k :desc]) ks))

(defn order-by-desc
  "降序排序"
  [sqlmap & args]
  (apply helpers/order-by (into [sqlmap] (to-desc-vals args))))

(defn convert-rows
  "convert page data rows"
  [page-data convert-fn]
  (update page-data :rows #(map convert-fn %)))
