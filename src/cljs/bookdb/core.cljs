(ns bookdb.core
  (:require [reagent.core :as reagent]
            [re-frame.core :as re-frame]
            [devtools.core :as devtools]
            [bookdb.handlers]
            [bookdb.subs]
            [bookdb.views :as views]
            [bookdb.config :as config]
            [bookdb.routing :as routing]
            [cljs.reader :as edn]
            [goog.dom]))


(defn dev-setup []
  (when config/debug?
    (println "dev mode")
    (devtools/install!)))

(defn ^:export render-to-string
  "Takes an app state as EDN and returns the HTML for that state.
  It can be invoked from JS as `hrubix.core.render_to_string(edn)`."
  [state-edn]
  (let [state (edn/read-string state-edn)]
    (re-frame/dispatch-sync [:initialize-db state])
    (reagent/render-to-string [views/main-panel])))

(defn mount-root []
  (reagent/render [views/main-panel]
                  (.getElementById js/document "app")))

(defn ^:export init []
  (let [state (->> "app-state"
                   goog.dom/getElement
                   .-textContent
                   edn/read-string)]
    (routing/init!)
    (re-frame/dispatch-sync [:initialize-db state])
    (dev-setup)
    (mount-root)))
