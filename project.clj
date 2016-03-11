(defproject net.ulfurinn/riemann-plugin-amqp "0.1.0"
  :description "AMQP event source for Riemann"
  :url "http://github.com/ulfurinn/riemann-plugin-amqp"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [com.novemberain/langohr "3.5.1"]
                 [org.clojure/data.json "0.2.6"]]
  :profiles {:dev {:dependencies [[riemann "0.2.10"]]}}
  :codox {:namespaces [riemann-plugin-amqp.core]})
