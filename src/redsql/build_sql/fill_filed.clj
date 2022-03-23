(ns redsql.build-sql.fill-filed)

(defn add-insert-fill-filed
  "插入操作添加自动填充"
  [sqlmap opt]
  (if-let [fill-fn (:insert-fill-filed-fn opt)]
    (merge sqlmap (fill-fn))
    sqlmap))

(defn add-update-fill-filed
  "更新操作添加自动填充"
  [sqlmap opt]
  (if-let [fill-fn (:update-fill-filed-fn opt)]
    (merge sqlmap (fill-fn))
    sqlmap))


