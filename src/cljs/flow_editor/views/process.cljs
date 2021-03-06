(ns flow-editor.views.process
  (:require [re-frame.core :refer [subscribe dispatch]]
            [reagent.core :as r]
            [clojure.set :refer [union]]
            [flow-editor.utils.graph-ui :refer [p-node p-node-id]]
            [flow-editor.views.utils.codemirror :refer [cm]]
            [re-com.core :refer [input-text
                                 md-icon-button button md-circle-icon-button
                                 single-dropdown title label
                                 gap v-box h-box box line]]))


(defn header
  [process minified]
  (let [port-types (subscribe [:flow-runtime/port-types])
        editing-id? (r/atom false)]
    (fn [process minified]
      (let [id (:id process)
            new-id (atom id)
            acc-type (get @port-types "ACCUMULATOR")
            autostart? (:autostart process)
            async? (:async process)
            accumulator? (->> (:ports process)
                           (vals)
                           (some #(= % acc-type)))]
        [h-box
         :children [[:div
                     {:style {:background-color "#de7a13"
                              :width "20px"
                              :height "20px"
                              :border-radius "10px"
                              :display "inline-block"}}]
                    [gap :size "10px"]
                    (if @editing-id?
                      [input-text
                       :model id
                       :width "200px"
                       :change-on-blur? false
                       :on-change #(reset! new-id %)]
                      [title
                       :label id
                       :margin-top "0.3em"
                       :level :level3])
                    (when @editing-id?
                      [md-icon-button
                       :md-icon-name "zmdi-close"
                       :size :smaller
                       :tooltip "cancel"
                       :on-click #(reset! editing-id? false)])
                    (when @editing-id?
                      [md-icon-button
                       :md-icon-name "zmdi-check"
                       :size :smaller
                       :tooltip "apply"
                       :on-click (fn []
                                   (dispatch [:flow-runtime/rename-process id @new-id])
                                   (reset! editing-id? false))])
                    [gap :size "10px"]
                    (when (not @editing-id?)
                      [md-icon-button
                       :md-icon-name "zmdi-edit"
                       :size :smaller
                       :style {:opacity "0.3"}
                       :tooltip "rename"
                       :on-click #(reset! editing-id? true)])
                    [gap :size "auto"]
                    (if accumulator?
                      [md-icon-button
                       :md-icon-name "zmdi-brightness-5"
                       :disabled? true
                       :tooltip "no autostart for accumulator"]
                      (if autostart?
                        [md-icon-button
                         :md-icon-name "zmdi-brightness-auto"
                         :tooltip "turn off autostart"
                         :style {:color "orange"}
                         :on-click #(dispatch [:flow-runtime/set-process-autostart id nil])]
                        [md-icon-button
                         :md-icon-name "zmdi-brightness-5"
                         :tooltip "turn on autostart"
                         :on-click #(dispatch [:flow-runtime/set-process-autostart id true])]))
                    (if accumulator?
                      [md-icon-button
                       :md-icon-name "zmdi-time"
                       :disabled? true
                       :tooltip "process with accumulator cannot be asynchronous"]
                      (if async?
                        [md-icon-button
                         :md-icon-name "zmdi-time"
                         :tooltip "turn off async"
                         :style {:color "orange"}
                         :on-click #(dispatch [:flow-runtime/set-process-async id nil])]
                        [md-icon-button
                         :md-icon-name "zmdi-time"
                         :tooltip "turn on async"
                         :on-click #(dispatch [:flow-runtime/set-process-async id true])]))
                    [md-icon-button
                     :md-icon-name "zmdi-play"
                     :tooltip "start"
                     :on-click #(dispatch [:flow-runtime/start-process id])]
                    (when async?
                      [md-icon-button
                       :md-icon-name "zmdi-stop"
                       :tooltip "stop"
                       :on-click #(dispatch [:flow-runtime/stop-process id])])
                    [gap :size "10px"]
                    [line]
                    [gap :size "10px"]
                    [md-icon-button
                     :md-icon-name "zmdi-delete"
                     :tooltip "delete this process"
                     :on-click #(dispatch [:flow-runtime/remove-process id])]
                    [gap :size "10px"]
                    [line]
                    [gap :size "10px"]
                    (if minified
                      [md-icon-button
                       :md-icon-name "zmdi-plus"
                       :tooltip "reopen"
                       :on-click #(dispatch [:flow-runtime-ui/minify-node (p-node id) nil])]
                      [md-icon-button
                       :md-icon-name "zmdi-minus"
                       :tooltip "minimize"
                       :on-click #(dispatch [:flow-runtime-ui/minify-node (p-node id) true])])
                    [md-icon-button
                     :md-icon-name "zmdi-close"
                     :on-click #(dispatch [:flow-runtime-ui/close-node
                                            {:id id :type "process"}])]]]))))


(defn port-row
  [name type pid]
  (let [port-types (subscribe [:flow-runtime/port-types])
        entities (subscribe [:flow-runtime/all-entities])
        connections (subscribe [:flow-runtime/process-port-connection pid])]
    (fn [name type pid]
      (let [port-type-choices (->> @port-types
                                (mapv (fn [[k v]] {:id v :label k})))
            current-entity (->> @connections
                             (filter #(= (:port %) name))
                             (first)
                             (:entity))
            entity-choices (->> @entities
                             (mapv :id)
                             (set)
                             (remove (set (mapv :entity @connections)))
                             (union (if current-entity #{current-entity} #{}))
                             (mapv (fn [id] {:id id :label id}))
                             (concat [{:id nil :label "-- Disconnect !"}]))]
        [h-box
         :children [[input-text
                     :model name
                     :width "160px"
                     :on-change #(dispatch [:flow-runtime/rename-port pid name %])]
                    [single-dropdown
                     :choices port-type-choices
                     :model type
                     :width "140px"
                     :on-change #(dispatch [:flow-runtime/change-port-type pid name %])]
                    (if-not (= type (get @port-types "ACCUMULATOR"))
                      [single-dropdown
                       :choices entity-choices
                       :model current-entity
                       :filter-box? true
                       :on-change #(dispatch [:flow-runtime/connect-port pid name %])]
                      [box
                       :child " "
                       :size "auto"])
                    [md-circle-icon-button
                     :size :smaller
                     :style {:margin-left "10px"}
                     :md-icon-name "zmdi-minus"
                     :on-click #(dispatch [:flow-runtime/remove-process-port pid name])]]]))))


(defn ports-editor
  [ports pid]
  [v-box
   :children [[h-box
               :children [[label :label "ports"]
                          [md-circle-icon-button
                           :size :smaller
                           :style {:margin-left "10px"}
                           :md-icon-name "zmdi-plus"
                           :on-click #(dispatch [:flow-runtime/add-process-port pid])]]]
              (for [[port-name type] ports]
                ^{:key (str pid "::port::" port-name)} [port-row (name port-name) type pid])]])


(defn output-port
  [pid]
  (let [out-arc (subscribe [:flow-runtime/output-port pid])
        entities (subscribe [:flow-runtime/all-entities])
        connections (subscribe [:flow-runtime/process-port-connection pid])]
    (fn [pid]
      (let [entity (:entity @out-arc)
            entity-choices (->> @entities
                             (mapv :id)
                             (set)
                             (remove (set (mapv :entity @connections)))
                             (union (if entity #{entity} #{}))
                             (mapv (fn [id] {:id id :label id}))
                             (concat [{:id nil :label "-- Disconnect !"}]))]
        [single-dropdown
         :choices entity-choices
         :model entity
         :width "200px"
         :filter-box? true
         :on-change #(dispatch [:flow-runtime/connect-output pid %])]))))


(defn procedure-editor
  [process code-changes]
  (let [cm-options {:mode {:name "javascript"
                           :globalVars true}}

        id (:id process)
        port-types (subscribe [:flow-runtime/port-types])
        runtime (subscribe [:flow-runtime/runtime])
        connections (subscribe [:flow-runtime/process-port-connection id])]

      (fn [process code-changes]
        (let [port-vals (->> (:ports process)
                          (map (fn [[port type]]
                                 (let [arc (if (= type (get @port-types "ACCUMULATOR"))
                                             (filter (fn [arc] (not (:port arc))) @connections)
                                             (filter (fn [arc] (= (:port arc) (name port))) @connections))
                                       val (when arc (.get @runtime (:entity (first arc))))]
                                    [port val])))
                          (into {}))
              additionalCtx {"this" (.getContext @runtime)
                             "ports" port-vals}]

          [cm (:code process) cm-options code-changes additionalCtx]))))


(defn process-component
  [process minified]
  (let [code-changes (r/atom (:code process))
        active-node (subscribe [:graph-ui/active-node])]
    (fn [process minified]
      (let [code-changed? (not= @code-changes (:code process))
            id (:id process)]
        [v-box
         :class (str "process-component " (p-node-id id)
                     (when (and (= (:id @active-node) id)
                                (= (:type @active-node) "process"))
                       " selected"))
         :gap "5px"
         :attr {:on-mouse-over #(dispatch [:graph-ui/set-active-node (p-node id)])}
         :children (if minified
                     [[header process minified]]
                     [[header process minified]
                      [ports-editor (:ports process) id]
                      [label :label "procedure"]
                      [procedure-editor process code-changes]
                      [h-box
                       :gap "10px"
                       :children [[button
                                   :label "update"
                                   :class (when code-changed? "btn-primary")
                                   :disabled? (not code-changed?)
                                   :on-click #(dispatch [:flow-runtime/update-process-code id @code-changes])]
                                  [gap :size "auto"]
                                  [label
                                   :label "output"
                                   :style {:margin-top "8px"}]
                                  [output-port id]]]])]))))
