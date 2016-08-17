(ns bookdb.views
    (:require [re-frame.core :as re-frame]))


(defn home []
  (let [name (re-frame/subscribe [:name])]
    (fn []
      [:div "Hello from " @name " "
       [:button {:on-click #(re-frame/dispatch [:change-page :bookdb.core/hello-page])} "Next Page"]])))

(defn page []
  [:div "Hello from another page"])

(defn not-found []
  [:div "Not Found"])

(def pages
  {:bookdb.core/hello-world home
   :bookdb.core/hello-page page
   :ck.react-server/not-found not-found})

(defn main-panel []
  (let [page (re-frame/subscribe [:current-page])]
    (fn []
      (let [content (@page pages)]
        [content]))))
