(ns redsql.config)

(def ^:private config (atom {}))

(def ^:private global-key :_global-key_)

(defn set-global-config
  "设置全局配置, 多个数据源共享配置"
  [cfg]
  (swap! config #(assoc % global-key cfg)))

(defn get-global-config
  "获取全局配置"
  []
  (get @config global-key))

(defn clear-global-config
  "清理全局配置"
  []
  (swap! config #(dissoc % global-key)))

(defn set-ds-config
  "设置数据源的配置"
  [ns-key cfg]
  (swap! config #(assoc % ns-key cfg)))

(defn get-ds-config
  "获取数据源配置"
  [ns-key]
  (get @config ns-key))

(defn clear-ds-config
  "清理数据源配置"
  [ns-key]
  (swap! config #(dissoc % ns-key)))

(defn get-config
  "获取配置信息
  优先级：方法传参的配置 > 设置数据源的配置 > 设置全局的配置
  "
  [ns-key local-config]
  (let [ds-config (get-ds-config ns-key)
        global-config (get-global-config)]
    (merge global-config ds-config local-config)))
