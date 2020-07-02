(ns gad2.server.ring)


;;;; Ring handlers


(defn login-handler
  "Here's where you'll add your server-side login/auth procedure (Friend, etc.).
  In our simplified example we'll just always successfully authenticate the user
  with whatever user-id they provided in the auth request."
  [ring-req]
  (let [{:keys [session params]} ring-req
        {:keys [user-id]} params]
    (debugf "Login request: %s" params)
    {:status 200 :session (assoc session :uid user-id)}))

(defn home-page [request]
  (layout/render request "home.html"))

(defn wrap-formats [handler]
  (wrap-restful-format
   handler
   {:formats [:json-kw :transit-json :transit-msgpack]}))

(defroutes ring-routes
  (GET  "/"      ring-req (home-page ring-req))
  (GET  "/docs"  ring-req (fn [_]
                            (-> (response/ok (-> "docs/docs.md" io/resource slurp))
                                (response/header "Content-Type" "text/plain; charset=utf-8"))))
  (GET  "/chsk"  ring-req (ring-ajax-get-or-ws-handshake ring-req))
  (wrap-formats (POST "/json"  ring-req (fn [request] (response/ok
                                                       {:result (-> request :params)}))))
  (POST "/chsk"  ring-req (ring-ajax-post                ring-req))
  (POST "/login" ring-req (login-handler                 ring-req))
  (route/resources "/") ; Static files, notably public/main.js (our cljs target)
  (route/not-found "<h1>Page not found</h1>"))


(def main-ring-handler
  "**NB**: Sente requires the Ring `wrap-params` + `wrap-keyword-params`
  middleware to work. These are included with
  `ring.middleware.defaults/wrap-defaults` - but you'll need to ensure
  that they're included yourself if you're not using `wrap-defaults`.
  You're also STRONGLY recommended to use `ring.middleware.anti-forgery`
  or something similar."
  (ring.middleware.defaults/wrap-defaults
   (-> ring-routes var wrap-reload wrap-webjars)
   (assoc-in ring.middleware.defaults/site-defaults [:security :anti-forgery] false)))
