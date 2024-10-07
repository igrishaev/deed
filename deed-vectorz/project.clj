(defproject com.github.igrishaev/deed-vectorz "0.1.0"

  :description
  "Mikera/vectorz support"

  :scm {:dir ".."}

  :plugins
  [[lein-parent "0.3.8"]]

  :source-paths ["src/clj"]
  :java-source-paths ["src/java"]

  :dependencies
  [[com.github.igrishaev/deed-core]
   [net.mikera/vectorz]]

  :parent-project
  {:path "../project.clj"
   :inherit [:deploy-repositories
             :license
             :release-tasks
             :managed-dependencies
             :plugins
             :repositories
             :pom-addition
             :javac-options
             :url
             [:profiles :dev]
             [:profiles :test]]})
