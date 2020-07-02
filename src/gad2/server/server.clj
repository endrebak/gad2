(ns gad2.server
  (:require [ring.adapter.jetty :as jetty]
            [clojure.java.io :as io]
            [nrepl.server :refer [start-server stop-server]]
            [cprop.source :as source]
            [mount.core :as mount]
            [clojure.string     :as str]
            [ring.middleware.defaults]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.middleware.anti-forgery :as anti-forgery]
            [ring.middleware.webjars :refer [wrap-webjars]]
            [ring.middleware.format :refer [wrap-restful-format]]
            [ring.util.http-response :as response]
            [compojure.core     :as comp :refer (defroutes GET POST)]
            [compojure.route    :as route]
            [hiccup.core        :as hiccup]
            [clojure.core.async :as async  :refer (<! <!! >! >!! put! chan go go-loop)]
            [taoensso.encore    :as encore :refer (have have?)]
            [taoensso.timbre    :as timbre :refer (tracef debugf infof warnf errorf)]
            [taoensso.sente     :as sente]

            [gad2.server.websocket :refer [start-example-broadcaster!]]

            ;;; TODO Choose (uncomment) a supported web server + adapter -------------
            ;; [org.httpkit.server :as http-kit]
            ;; [taoensso.sente.server-adapters.http-kit :refer (get-sch-adapter)]
            ;;
            [immutant.web :as immutant]
            [taoensso.sente.server-adapters.immutant :refer (get-sch-adapter)]
            ;;
            ;; [nginx.clojure.embed :as nginx-clojure]
            ;; [taoensso.sente.server-adapters.nginx-clojure :refer (get-sch-adapter)]
            ;;
            ;; [aleph.http :as aleph]
            ;; [taoensso.sente.server-adapters.aleph :refer (get-sch-adapter)]
            ;; -----------------------------------------------------------------------

            ;; Optional, for Transit encoding:
            [taoensso.sente.packers.transit :as sente-transit]))


;; (defn handler [request]
;;   {:status 200
;;    :headers {"Content-Type" "text/plain"}
;;    :body "Hello Clojure, Hello Ring!"})

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
  "Home-page")

(defn wrap-formats [handler]
  (wrap-restful-format
   handler
   {:formats [:json-kw :transit-json :transit-msgpack]}))

(defroutes ring-routes
  (GET  "/"      ring-req (home-page ring-req))
  ;; (GET  "/docs"  ring-req (fn [_]
  ;;                           (-> (response/ok (-> "docs/docs.md" io/resource slurp))
  ;;                               (response/header "Content-Type" "text/plain; charset=utf-8"))))
  ;; (GET  "/chsk"  ring-req (ring-ajax-get-or-ws-handshake ring-req))
  ;; (wrap-formats (POST "/json"  ring-req (fn [request] (response/ok
  ;;                                                      {:result (-> request :params)}))))
  ;; (POST "/chsk"  ring-req (ring-ajax-post                ring-req))
  ;; (POST "/login" ring-req (login-handler                 ring-req))
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
;; ring.middleware.defaults/site-defaults))



;;;; Init stuff

(defonce    web-server_ (atom nil)) ; (fn stop [])


(defn  stop-web-server! [] (when-let [stop-fn @web-server_] (stop-fn)))


(defn start-web-server! [& [port]]
  (stop-web-server!)
  (let [port (or port 0) ; 0 => Choose any available port
        ring-handler (var main-ring-handler)

        [port stop-fn]
        ;;; TODO Choose (uncomment) a supported web server ------------------
        ;; (let [stop-fn (http-kit/run-server ring-handler {:port port})]
        ;;   [(:local-port (meta stop-fn)) (fn [] (stop-fn :timeout 100))])
        ;;
        (let [server (immutant/run ring-handler :port port)]
          [(:port server) (fn [] (immutant/stop server))])
        ;;
        ;; (let [port (nginx-clojure/run-server ring-handler {:port port})]
        ;;   [port (fn [] (nginx-clojure/stop-server))])
        ;;
        ;; (let [server (aleph/start-server ring-handler {:port port})
        ;;       p (promise)]
        ;;   (future @p) ; Workaround for Ref. https://goo.gl/kLvced
        ;;   ;; (aleph.netty/wait-for-close server)
        ;;   [(aleph.netty/port server)
        ;;    (fn [] (.close ^java.io.Closeable server) (deliver p nil))])
        ;; ------------------------------------------------------------------

        uri (format "http://localhost:%s/" port)]

    (infof "Web server is running at `%s`" uri)
    (try
      (.browse (java.awt.Desktop/getDesktop) (java.net.URI. uri))
      (catch java.awt.HeadlessException _))

    (reset! web-server_ stop-fn)))


(defn stop!  []  (stop-router!)  (stop-web-server!))
(defn start! [] (start-router!) (start-web-server! 3000) (start-example-broadcaster!))



;; need to check file for changes
;; rerun upon change


;; should have file that defines

;; wildcard functions

;; workflow as an edn-file?

;; whenever one changes, update
