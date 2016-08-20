(ns bookdb.subs
    (:require-macros [reagent.ratom :refer [reaction]])
    (:require [re-frame.core :as re-frame]))

(re-frame/register-sub
 :all-books
 (fn [db]
   (reaction (get-in @db [1 :data]))))

(re-frame/register-sub
  :current-page
  (fn [db]
    (reaction (first @db))))
