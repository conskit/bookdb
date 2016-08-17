(ns bookdb.handlers
    (:require [re-frame.core :as re-frame]
              [bookdb.config :as config]
              [bookdb.routing :as routing]))

(re-frame/register-handler
 :initialize-db
 [config/standard-middlewares]
 (fn  [_ [state]]
   state))

(re-frame/register-handler
  :change-to-last-name
  [config/standard-middlewares]
  (fn  [db _]
    (routing/navigate :bookdb.core/hello-page {:foo :bar} false nil)
    (assoc-in db [1 :data :firstname] (get-in db [1 :data :lastname]))))
