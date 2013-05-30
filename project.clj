(defproject clj-pail "0.1.1-SNAPSHOT"
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

  :profiles {:1.3 {:dependencies [[org.clojure/clojure "1.3.0"]]}
             :1.4 {:dependencies [[org.clojure/clojure "1.4.0"]]}
             :1.5 {:dependencies [[org.clojure/clojure "1.5.1"]]}
             :1.6 {:dependencies [[org.clojure/clojure "1.6.0-master-SNAPSHOT"]]}
             :dev {:dependencies [[midje "1.5.1"]]
                   :plugins [[lein-midje "3.0.1"]]
                   :source-paths ["src/test"]
                   :aot [clj-pail.fakes.structure]}}

  :aliases {"all" ["with-profile" "+1.3:+1.4:+1.5:+1.6"]}

  :repositories {"sonatype" {:url "http://oss.sonatype.org/content/repositories/releases"
                             :snapshots false
                             :releases {:checksum :fail :update :always}}
                 "sonatype-snapshots" {:url "http://oss.sonatype.org/content/repositories/snapshots"
                                       :snapshots true
                                       :releases {:checksum :fail :update :always}}})
