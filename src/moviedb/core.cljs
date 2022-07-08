(ns moviedb.core
  (:require
   [reagent.core :as r]
   [reagent.dom :as d]
   [re-frame.core :as rf]
   [ajax.core :refer [GET json-response-format]]))

;; -------------------------
;; Views

(def movie-title (r/atom ""))
;; (def omdb-resp (r/atom ""))

(defn handle-omdb-resp [resp]
  (.log js/console "Response:" (:Title resp) (:Year resp))
  (rf/dispatch [:save-omdb-resp resp]))

(rf/reg-sub
 :loaded-response?
 (fn [db _]
   (get-in db [:omdb-data :loaded])))

(rf/reg-sub
 :omdb-resp
 (fn [db _]
   (get-in db [:omdb-data :resp])))

(defn home-page []
  [:div {:class "home-container"}
   [:h2 "Enter a movie title:"]
   [:form {:on-submit (fn [e] 
                        (.preventDefault e)
                        (GET "http://www.omdbapi.com"
                          {:params {:apikey "a74b26be" :t @movie-title}
                           :handler handle-omdb-resp
                           :response-format (json-response-format {:keywords? true})}))}
    [:input {:type "input"
             :on-change #(reset! movie-title (.-value (.-target %)))
             }]]
   (let [loaded? (rf/subscribe [:loaded-response?])
         omdb-resp (rf/subscribe [:omdb-resp])]
     (when @loaded?
     [:<>
      [:img {:src (:Poster @omdb-resp)}]
      [:h2 (str "Year: " (:Year @omdb-resp))]
      [:h4 (:Plot @omdb-resp)]]))
   ])

(rf/reg-event-db
 :save-omdb-resp
 (fn [db [_ resp]]
   (update db :omdb-data assoc :resp resp :loaded true)))
;; -------------------------
;; Initialize app

(defn mount-root []
  (d/render [home-page] (.getElementById js/document "app")))

(defn ^:export init! []
  (mount-root))
