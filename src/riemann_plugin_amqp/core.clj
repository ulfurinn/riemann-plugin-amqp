(ns riemann-plugin-amqp.core
  "AMQP event source for Riemann."
  (:require langohr.core langohr.channel langohr.queue langohr.consumers
            [clojure.data.json :as json]
            riemann.core riemann.common riemann.config))

(defrecord AMQPSource [core
                       ^String host
                       ^String vhost
                       ^String username
                       ^String password
                       exchange
                       routing-key
                       stopper]

  riemann.service/ServiceEquiv
  (equiv? [this other] (and (instance? AMQPSource other)
                            (= host (:host other))
                            (= vhost (:vhost other))
                            (= exchange (:exchange other))
                            (= routing-key (:routing-key other))))

  riemann.service/Service
  (conflict? [this other] false)

  (reload! [this new-core] (reset! core new-core))

  (start! [this]
    (locking this
      (when-not @stopper
        (let [conn (langohr.core/connect {:host host :vhost vhost :username username :password password})
              chan (langohr.channel/open conn)
              queue-declaration (langohr.queue/declare chan)
              queue (get queue-declaration :queue)]
          (langohr.queue/bind chan queue exchange {:routing-key routing-key})
          (langohr.consumers/subscribe chan queue (fn [chan meta ^bytes payload]
                                                    (let [json-str (String. payload "UTF-8")
                                                          json-map (json/read-str json-str :key-fn keyword)
                                                          event (riemann.common/event json-map)]
                                                      (riemann.core/stream! @core event)))
                                       {:auto-ack true})
          (reset! stopper (fn [] (langohr.core/close conn)))))))

  (stop! [this]
    (locking this
      (when @stopper
        (@stopper)
        (reset! stopper nil)))))

(defn- amqp-source*
  [opts]
  (let [core (get opts :core (atom nil))
        host (get opts :host "127.0.0.1")
        vhost (get opts :vhost "/")
        username (get opts :username "guest")
        password (get opts :password "guest")
        exchange (get opts :exchange)
        routing-key (get opts :routing-key "#")]
    (AMQPSource. core host vhost username password exchange routing-key (atom nil))))

(defn source
  "Sets up an AMQP event source.

	Recognized options are [:host :vhost :username :password :exchange :routing-key].
	The exchange must already exist.

	Payloads will be decoded as JSON matching the Riemann event structure."
  [opts]
  (riemann.config/service! (amqp-source* opts)))
