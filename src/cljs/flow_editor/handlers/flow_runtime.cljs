(ns flow-editor.handlers.flow-runtime
  (:require [re-frame.core :refer [register-handler dispatch]]))


(def default-process-code
  "function(ports, send) {\n\n}")


(defn update-runtime [db]
  (let [new-graph (js->clj (.getState (:runtime db)) :keywordize-keys true)]
    (println "flow graph updated! " new-graph)
    (when-let [local-storage-key (:local-storage-key db)]
      (.setItem js/localStorage local-storage-key (.stringify js/JSON (clj->js new-graph))))
    (assoc db :graph new-graph)))


;; ===== Entity handlers =====

(register-handler
 :flow-runtime/add-entity
 (fn [db [_ entity-id]]
   (.addEntity (:runtime db) #js {:id entity-id})
   (dispatch [:ui/close-modal])
   (dispatch [:flow-runtime/watch-entity entity-id])
   (update-runtime db)))


(register-handler
  :flow-runtime/watch-entity
  (fn [db [_ entity-id]]
    (.on (:runtime db) entity-id #(dispatch [:flow-runtime/entity-value-changed
                                             entity-id %]))
    (dispatch [:flow-runtime/entity-value-changed
               entity-id (.get (:runtime db) entity-id)])
    db))


(register-handler
  :flow-runtime/unwatch-entity
  (fn [db [_ entity-id]]
    (.off (:runtime db) entity-id)
    (update-in db [:entity-values] dissoc entity-id)))


(register-handler
  :flow-runtime/entity-value-changed
  (fn [db [_ entity-id value]]
    (let [i (get-in db [:entity-values entity-id :iter] 0)]
      (assoc-in db [:entity-values entity-id] {:iter (inc i) :value value}))))


(register-handler
 :flow-runtime/remove-entity
 (fn [db [_ entity-id]]
   (.removeEntity (:runtime db) entity-id)
   (update-runtime db)))


;; ===== Process handlers =====

(register-handler
 :flow-runtime/add-process
 (fn [db [_ process-id]]
   (.addProcess (:runtime db) #js {:id process-id :code default-process-code})
   (dispatch [:ui/close-modal])
   (update-runtime db)))


(register-handler
 :flow-runtime/remove-process
 (fn [db [_ process-id]]
   (.removeProcess (:runtime db) process-id)
   (update-runtime db)))


(register-handler
 :flow-runtime/update-process-code
 (fn [db [_ pid code]]
   (let [p (get-in db [:graph :processes (keyword pid)])]
     (->> (merge p {:code code :procedure nil})
      (clj->js)
      (.addProcess (:runtime db)))
     (update-runtime db))))


(register-handler
 :flow-runtime/start-process
 (fn [db [_ pid]]
   (.start (:runtime db) pid)
   db))


(register-handler
 :flow-runtime/stop-process
 (fn [db [_ pid]]
   (.stop (:runtime db) pid)
   db))


(register-handler
 :flow-runtime/add-process-port
 (fn [db [_ pid]]
   (let [p (get-in db [:graph :processes (keyword pid)])
         runtime (:runtime db)
         ports (merge (:ports p) {"" (.-PORT_TYPES.COLD runtime)})]
     (.addProcess runtime (clj->js (merge p {:ports ports})))
     (update-runtime db))))


(register-handler
 :flow-runtime/rename-port
 (fn [db [_ pid old-name new-name]]
   (let [p (get-in db [:graph :processes (keyword pid)])
         runtime (:runtime db)
         port-type (get-in p [:ports (keyword old-name)])
         ports (-> (:ports p)
                 (dissoc (keyword old-name))
                 (assoc (keyword new-name) port-type))
         arcs (->> (get-in db [:graph :arcs])
                (vals)
                (filter (fn [{:keys [process port] :as arc}]
                          (and (= process pid)
                               (or (= port old-name)
                                   (= port new-name))))))]
     (doseq [arc arcs]
       (.removeArc runtime (:id arc)))
     (.addProcess runtime (clj->js (merge p {:ports ports})))
     (update-runtime db))))


(register-handler
 :flow-runtime/change-port-type
 (fn [db [_ pid port-name port-type]]
   (let [p (get-in db [:graph :processes (keyword pid)])
         runtime (:runtime db)
         ports (-> (:ports p)
                 (assoc (keyword port-name) port-type))
         arcs (->> (get-in db [:graph :arcs])
                (vals)
                (filter (fn [{:keys [process port] :as arc}]
                          (and (= process pid)
                               (= port port-name)))))]
     (when (= port-type (.-PORT_TYPES.ACCUMULATOR runtime))
       (doseq [arc arcs]
         (.removeArc runtime (:id arc))))
     (.addProcess runtime (clj->js (merge p {:ports ports})))
     (update-runtime db))))


(register-handler
  :flow-runtime/connect-port
  (fn [db [_ pid port-name eid]]
    (doseq [arc (->> (get-in db [:graph :arcs])
                  (vals)
                  (filter (fn [{:keys [process port] :as arc}]
                            (and (= process pid)
                                 (= port port-name)))))]
      (.removeArc (:runtime db) (:id arc)))
    (when eid (.addArc (:runtime db) (clj->js {:port port-name :process pid :entity eid})))
    (update-runtime db)))


(register-handler
  :flow-runtime/remove-process-port
  (fn [db [_ pid port-name]]
    (let [p (get-in db [:graph :processes (keyword pid)])]
      (doseq [arc (->> (get-in db [:graph :arcs])
                    (vals)
                    (filter (fn [{:keys [process port] :as arc}]
                              (and (= process pid)
                                   (= port port-name)))))]
        (.removeArc (:runtime db) (:id arc)))
      (.addProcess (:runtime db) (clj->js (merge p {:ports (dissoc (:ports p) (keyword port-name))})))
      (update-runtime db))))


(register-handler
  :flow-runtime/connect-output
  (fn [db [_ pid eid]]
    (doseq [arc (->> (get-in db [:graph :arcs])
                  (vals)
                  (filter (fn [{:keys [process port] :as arc}]
                            (and (= process pid)
                                 (not port)))))]
      (.removeArc (:runtime db) (:id arc)))
    (when eid
      (.addArc (:runtime db) (clj->js {:process pid :entity eid})))
    (update-runtime db)))