(ns bookdb.views
    (:require [re-frame.core :as re-frame]
              [reagent.core :as reagent]))


(defn nav []
 [:nav.navbar.navbar-inverse.navbar-fixed-top
  {:role "navigation"}
  [:div.container
   [:div.navbar-header
    [:button.navbar-toggle
     {:data-target "#bs-example-navbar-collapse-1",
      :data-toggle "collapse",
      :type "button"}
     [:span.sr-only "Toggle navigation"]
     [:span.icon-bar]
     [:span.icon-bar]
     [:span.icon-bar]]
    [:a.navbar-brand {:href "/"} "Book Database"]]
   [:div#bs-example-navbar-collapse-1.collapse.navbar-collapse
    [:ul.nav.navbar-nav
     [:li [:a {:href "/create"} "Create"]]]]]])

(defn page-container [content]
  [:div#page-wrapper {:style {:padding-top "70px"}}
   [nav]
   [:div.container
    [content]]])

(defn table [books]
  (reagent/create-class
    {:component-did-mount #(.DataTable (js/$ (reagent/dom-node %)))
     :reagent-render (fn []
                       [:table.table.table-striped.table-bordered
                        {:cell-spacing "0" :width "100%"}
                        [:thead>tr
                         [:th "Book Id"]
                         [:th "Book Name"]
                         [:th "Author"]
                         [:th "Actions"]]
                        [:tbody
                         (for [{:keys [book_id book_name author]} books]
                           ^{:key book_id} [:tr
                                            [:td book_id]
                                            [:td book_name]
                                            [:td author]
                                            [:td ""]])]])}))

(defn home []
  (let [all-books (re-frame/subscribe [:all-books])]
    (fn []
      [table @all-books])))

(defn page []
  [:div "Hello from another page"])

(defn not-found []
  [:div "Not Found"])

(def pages
  {:bookdb.core/book-list home
   :bookdb.core/book-create page
   :ck.react-server/not-found not-found})

(defn main-panel []
  (let [page (re-frame/subscribe [:current-page])]
    (fn []
      (let [content (@page pages)]
        [page-container content]))))
