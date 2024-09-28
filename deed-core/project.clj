(def MIN_JAVA_VERSION "16")

(defproject com.github.igrishaev/deed "0.1.0-SNAPSHOT"

  :description
  "Fast, flexible, 0-deps (de)serialization library for Clojure"

  :scm {:dir ".."}

  :plugins
  [[lein-parent "0.3.8"]]

  :dependencies
  [[org.clojure/clojure]]

  :pom-addition
  [:properties
   ["maven.compiler.source" ~MIN_JAVA_VERSION]
   ["maven.compiler.target" ~MIN_JAVA_VERSION]]

  :source-paths ["src/clj"]
  :java-source-paths ["src/main/java"]
  :javac-options ["-Xlint:unchecked"
                  "-Xlint:preview"
                  "--release" ~MIN_JAVA_VERSION]

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
