(defproject com.github.igrishaev/deed-vectorz "0.1.0-SNAPSHOT"

  :description
  "Mikera/vectorz support"

  :scm {:dir ".."}

  :plugins
  [[lein-parent "0.3.8"]]

  :dependencies
  [[com.github.igrishaev/deed-core]
   [net.mikera/vectorz]]

  :source-paths ["src"]

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
