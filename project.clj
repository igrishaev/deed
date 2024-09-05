(def MIN_JAVA_VERSION "17")

(defproject com.github.igrishaev/pinny "0.1.0-SNAPSHOT"

  :description
  "todo"

  :dependencies
  [[org.clojure/clojure "1.11.1"]]

  :pom-addition
  [:properties
   ["maven.compiler.source" ~MIN_JAVA_VERSION]
   ["maven.compiler.target" ~MIN_JAVA_VERSION]]

  :source-paths ["src/clj"]
  :java-source-paths ["src/main/java"]
  :javac-options ["-Xlint:unchecked"
                  "-Xlint:preview"
                  "--release" ~MIN_JAVA_VERSION]

  :profiles
  {:test
   {:source-paths ["test"]
    :dependencies [[criterium "0.4.6"]
                   [com.taoensso/nippy "3.4.2"]]}})
