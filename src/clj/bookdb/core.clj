(ns bookdb.core
  (:require [puppetlabs.trapperkeeper.core :refer [defservice]]
            [puppetlabs.trapperkeeper.services :refer [get-service service-context]]
            [clojure.tools.logging :as log]
            [conskit.macros :refer [defcontroller action]]
            [ck.routing.bidi]
            [ck.server.http-kit]
            [ck.migrations.flyway]
            [hugsql.core :as hug]
            [hiccup.page :as h]
            [ring.util.response :as r]
            [ck.react-server :as ckrs]
            [bookdb.actions :refer [crud-ctrlr]]))

(defn template
  [rendered-html meta state]
  (h/html5
    [:head
     [:meta {:charset "utf-8"}]
     (h/include-css "/css/bootstrap.min.css")
     (h/include-css "/css/dataTables.bootstrap.min.css")
     [:title (:title meta)]]
    [:body
     [:div#app rendered-html]
     [:script#app-state {:type "application/edn"} state]
     (h/include-js "/js/compiled/app.js")
     (h/include-js "/js/jquery.js")
     (h/include-js "/js/bootstrap.min.js")
     (h/include-js "/js/jquery.dataTables.min.js")
     (h/include-js "/js/dataTables.bootstrap.min.js")
     [:script "bookdb.core.init()"]]))

(defn map-of-db-bindings
  "Creates a binding map from hugsql db fns"
  [file conn]
  (->> (hug/map-of-db-fns file)
       (map (fn [[k v]] [k (partial (:fn v) conn)]))
       (into {})))

(defn bidify
  [routes]
  (let [not-catch-all? #(not (true? (:route %)))
        catch-all (remove not-catch-all? routes)
        filtered-routes (filter not-catch-all? routes)
        rs (if (not-empty catch-all) (concat filtered-routes catch-all) routes)]
    ["" (for [r rs
              :let [{:keys [id route]} r]]
          [route id])]))

(defcontroller
  main-ctrlr
  [get-books get-routes]
  (action
    ^{:route "/"
      :react-server-page {:title "bookdb"
                          :template-fn template}}
    book-list
    [req]
    ;[:ck.react-server/redirect nil {:headers {"Location" "/create"}}]
    [:ck.react-server/ok (get-books)])
  (action
    ^{:route "/create"
      :react-server-page {:title "Create | bookdb"
                          :template-fn template}}
    book-create
    [req]
    [:ck.react-server/ok {:message "Hello There"}])
  (action
    ^{:route true
      :react-server-page {:title "Not Found | bookdb"
                          :template-fn template}}
    not-found
    [req]
    [:ck.react-server/not-found {}])
  (action
    ^{:socket true}
    routes
    [data]
    (str (bidify (get-routes))))
  (action
    ^{:route #"/js.*"}
    scripts
    [req]
    (r/resource-response (:uri req) {:root "public"}))
  (action
    ^{:route #"/css.*"}
    styles
    [req]
    (r/resource-response (:uri req) {:root "public"}))
  (action
    ^{:route #"/fonts.*"}
    fonts
    [req]
    (r/resource-response (:uri req) {:root "public"}))
  (action
    ^{:route #"/images.*"}
    images
    [req]
    (r/resource-response (:uri req) {:root "public"})))


(defservice
  service
  [[:ConfigService get-in-config]
   [:ActionRegistry register-controllers! register-bindings! register-interceptors!]
   [:CKServer register-handler!]
   [:CKMigration migrate!]
   [:CKReactServer get-render-fn]
   [:CKRouter get-routes]]
  (init [this context]
        (log/info "Initializing Application")
        (register-controllers! [main-ctrlr crud-ctrlr])
        (register-interceptors! [ckrs/react-server-page])
        (register-bindings! (merge
                              (map-of-db-bindings "db/sql/book.sql" (get-in-config [:database]))
                              {:get-render-fn get-render-fn
                               :get-routes    get-routes}))
        (register-handler! :http-kit :bidi)
        (migrate! :flyway :database)
        context)
  (start [this context]
         (log/info "Start Application")
         context)
  (stop [this context]
        (log/info "Stopping Application")
        context))
