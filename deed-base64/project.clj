(defproject com.github.igrishaev/deed-base64 "0.1.0-SNAPSHOT"

  :description
  "Base64 encoding and decoding support"

  :scm {:dir ".."}

  :plugins
  [[lein-parent "0.3.8"]]

  :dependencies
  [[com.github.igrishaev/deed]
   [commons-codec/commons-codec]]

  :source-paths ["src/clj"]

  :parent-project
  {:path "../project.clj"
   :inherit [:deploy-repositories
             :license
             :release-tasks
             :managed-dependencies
             :plugins
             :repositories
             :url
             [:profiles :dev]
             [:profiles :test]]})
