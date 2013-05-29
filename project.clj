(defproject clj-pail "0.1.0-SNAPSHOT"
  :description "A Clojure abstraction for Pail"
  :url "https://github.com/dcuddeback/clj-pail"
  :license {:name "MIT License"
            :url "http://opensource.org/licenses/MIT"}

  :min-lein-version "2.0"

  :dependencies [[org.clojure/clojure "1.5.1"]
                 [com.backtype/dfs-datastores "1.3.4"]]

  :source-paths ["src/main"]

  :deploy-repositories [["releases" {:url "https://clojars.org/repo" :username :gpg :password :gpg}]
                        ["snapshots" {:url "https://clojars.org/repo" :username :gpg :password :gpg}]]

  :profiles {:dev {:dependencies [[midje "1.5.1"]]
                   :plugins [[lein-midje "3.0.1"]]}})
