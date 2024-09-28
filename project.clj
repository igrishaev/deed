(defproject com.github.igrishaev/deed "0.1.0-SNAPSHOT"

  :url
  "https://github.com/igrishaev/deed"

  :license
  {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
   :url "https://www.eclipse.org/legal/epl-2.0/"}

  :deploy-repositories
  {"releases"
   {:url "https://repo.clojars.org"
    :creds :gpg}
   "snapshots"
   {:url "https://repo.clojars.org"
    :creds :gpg}}

  :release-tasks
  [["vcs" "assert-committed"]
   ["sub" "change" "version" "leiningen.release/bump-version" "release"]
         ["change" "version" "leiningen.release/bump-version" "release"]
   ["vcs" "commit"]
   ["vcs" "tag" "--no-sign"]
   ["sub" "with-profile" "uberjar" "install"]
   ["sub" "with-profile" "uberjar" "deploy"]
   ["sub" "change" "version" "leiningen.release/bump-version"]
         ["change" "version" "leiningen.release/bump-version"]
   ["vcs" "commit"]
   ["vcs" "push"]]

  :plugins
  [[lein-sub "0.3.0"]
   [exoscale/lein-replace "0.1.1"]]

  :dependencies
  []

  :managed-dependencies
  [[com.github.igrishaev/deed :version]
   [org.clojure/clojure "1.11.1"]

   ;; base64
   [commons-codec/commons-codec "1.17.1"]

   ;; bench
   [criterium "0.4.6"]
   [com.taoensso/nippy "3.4.2"]
   [com.alpha-prosoft/jsonista "0.3.8.11"]]

  :sub
  ["deed-core"
   "deed-base64"]

  :profiles
  {:dev
   {:source-paths ["dev"]
    :dependencies [[org.clojure/clojure]]
    :global-vars
    {*warn-on-reflection* true
     *assert* true}}

   :test
   {:source-paths ["test"]}})
