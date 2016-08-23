(ns bookdb.handlers
    (:require [re-frame.core :as re-frame]
              [bookdb.config :as config]
              [reagent-modals.modals :as reagent-modals]
              [bookdb.routing :as routing]))

(re-frame/register-handler
 :initialize-db
 [config/standard-middlewares]
 (fn  [_ [state]]
   state))

(re-frame/register-handler
  :create-book
  [config/standard-middlewares]
  (fn [db [data]]
    (let [{:keys [send-fn]} @routing/sente-client]
      (send-fn [:bookdb.actions/create-book data]
               5000
               (fn [reply]
                 (when (= 1 reply)
                   (re-frame/dispatch [:change-page :bookdb.core/book-list])))))
    db))

(re-frame/register-handler
  :update-book
  [config/standard-middlewares]
  (fn [db [data]]
    (let [{:keys [send-fn]} @routing/sente-client]
      (send-fn [:bookdb.actions/update-book data]
               5000
               (fn [reply]
                 (when (= 1 reply)
                   (reagent-modals/close-modal!)
                   (re-frame/dispatch [:change-page :bookdb.core/book-list])))))
    db))

(re-frame/register-handler
  :delete-book
  [config/standard-middlewares]
  (fn [db [data]]
    (let [{:keys [send-fn]} @routing/sente-client]
      (send-fn [:bookdb.actions/delete-book data]
               5000
               (fn [reply]
                 (when (= 1 reply)
                   (reagent-modals/close-modal!)
                   (re-frame/dispatch [:change-page :bookdb.core/book-list])))))
    db))

(re-frame/register-handler
  :change-to-last-name
  [config/standard-middlewares]
  (fn  [db _]
    (routing/navigate :bookdb.core/hello-page {:foo :bar} false nil)
    (assoc-in db [1 :data :firstname] (get-in db [1 :data :lastname]))))
