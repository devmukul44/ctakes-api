logLevel := Level.Warn

resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.4.9")

addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.8.2")
