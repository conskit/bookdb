(ns bookdb.actions
  (:require [conskit.macros :refer [defcontroller action]]))

(defcontroller
  crud-ctrlr
  [create-new-book! update-book! delete-book!]
  (action
    ^{:socket true}
    create-book
    [data]
    (create-new-book! data))
  (action
    ^{:socket true}
    update-book
    [data]
    (update-book! data))
  (action
    ^{:socket true}
    delete-book
    [data]
    (delete-book! data)))
