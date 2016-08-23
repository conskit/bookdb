(ns bookdb.views
    (:require [re-frame.core :as re-frame]
              [reagent.core :as reagent]
              [reagent-forms.core :refer [bind-fields]]
              [reagent-modals.modals :as reagent-modals]
              [bookdb.routing :refer [link-to-page]]))

(defn row [label input]
  [:div.row
   [:div.col-md-6
    [:div.form-group
     [:label label]
     input]]])

(def form-template
  [:div
   (row "Book name" [:input.form-control {:field :text :id :book_name}])
   (row "Author" [:input.form-control {:field :text :id :author}])])

(defn book-form [data command]
  (let [doc (reagent/atom data)]
    [:div
     [bind-fields form-template doc]
     [:button.btn.btn-default {:type "submit" :on-click #(re-frame/dispatch [command @doc])} "Submit"]]))

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
    (link-to-page "/" "Book Database" nil "navbar-brand")]
   [:div#bs-example-navbar-collapse-1.collapse.navbar-collapse
    [:ul.nav.navbar-nav
     [:li (link-to-page "/create" "Create")]]]]])

(defn page-container [content]
  [:div#page-wrapper {:style {:padding-top "70px"}}
   [nav]
   [:div.container
    [content]]
   [reagent-modals/modal-window]])

(defn update-modal [book]
  [:div.modal-content
   [:div.modal-header
    [:h4 "Update Book"]]
   [:div.modal-body
    [book-form book :update-book]]])

(defn delete-confirm-modal [book]
  [:div.modal-content
   [:div.modal-header
    [:h4 "Delete Book"]]
   [:div.modal-body
    "Are you sure you want to delete " [:strong (:book_name book)] "?"]
   [:div.modal-footer
    [:button.btn.btn-default {:on-click #(reagent-modals/close-modal!)} "Cancel"]
    [:button.btn.btn-primary {:on-click #(re-frame/dispatch [:delete-book book])} "Confirm"]]])

(defn table [books]
  (reagent/create-class
    {:component-did-mount  #(.DataTable (js/$ (reagent/dom-node %)))
     :component-did-update #(.DataTable (js/$ (reagent/dom-node %)))
     :reagent-render       (fn []
                             [:table.table.table-striped.table-bordered
                              {:cell-spacing "0" :width "100%"}
                              [:thead>tr
                               [:th "Book Id"]
                               [:th "Book Name"]
                               [:th "Author"]
                               [:th "Actions"]]
                              [:tbody
                               (for [{:keys [book_id book_name author] :as book} @books]
                                 ^{:key book_id} [:tr
                                                  [:td book_id]
                                                  [:td book_name]
                                                  [:td author]
                                                  [:td
                                                   [:button.btn.btn-default {:on-click #(reagent-modals/modal! [update-modal book])} "Update"]
                                                   " "
                                                   [:button.btn.btn-default {:on-click #(reagent-modals/modal! [delete-confirm-modal book])} "Delete"]]])]])}))

(defn home []
  (let [all-books (re-frame/subscribe [:all-books])]
    (fn []
      [table all-books])))


(defn page []
  [book-form {} :create-book])

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
