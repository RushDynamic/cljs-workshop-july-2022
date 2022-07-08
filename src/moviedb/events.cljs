(ns moviedb.events
  (:require [re-frame.core :as rf]))

(rf/reg-event-db
 :save-omdb-resp
 (fn [db [_ resp]]
   (update db :omdb-data assoc :resp resp :loaded true)))

(rf/reg-sub
 :omdb-resp
 (fn [db _]
   (get-in db [:omdb-data :resp])))

(rf/reg-sub
 :loaded-response?
 (fn [db _]
   (get-in db [:omdb-data :loaded])))