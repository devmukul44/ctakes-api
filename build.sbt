name := "ctakesPlayV2_4"

version := "1.0"

lazy val `ctakesplayv2_4` = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.10.5"

libraryDependencies ++= Seq( jdbc , cache , ws )

unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"

libraryDependencies += "org.apache.opennlp" % "opennlp-uima" % "1.5.3"

libraryDependencies += "org.apache.uima" % "uimaj-core" % "2.4.0"

libraryDependencies += "com.googlecode.clearnlp" % "clearnlp" % "1.3.0"

libraryDependencies += "org.cleartk" % "cleartk-ml-liblinear" % "2.0.0"

unmanagedBase := baseDirectory.value / "custom_lib"

//libraryDependencies += "com.google.guava" % "guava" % "18.0"

//libraryDependencies += "org.apache.ctakes" % "ctakes-dictionary-lookup" % "3.2.2"
//
//libraryDependencies += "org.apache.ctakes" % "ctakes-utils" % "3.2.2"
//
//libraryDependencies += "org.apache.ctakes" % "ctakes-core-res" % "3.2.2"
//
//libraryDependencies += "org.apache.ctakes" % "ctakes-dictionary-lookup" % "3.2.2"
//
//libraryDependencies += "org.apache.ctakes" % "ctakes-dictionary-lookup" % "3.2.2"
//
//libraryDependencies += "org.apache.ctakes" % "ctakes-dictionary-lookup-res" % "3.2.2"
//
//libraryDependencies += "org.apache.ctakes" % "ctakes-dictionary-lookup-fast" % "3.2.2"
//
//libraryDependencies += "org.apache.ctakes" % "ctakes-dictionary-lookup-fast-res" % "3.2.2"
//
//libraryDependencies += "org.apache.ctakes" % "ctakes-drug-ner" % "3.2.2"
//
//libraryDependencies += "org.apache.ctakes" % "ctakes-drug-ner-res" % "3.2.2"
//
//libraryDependencies += "org.apache.ctakes" % "ctakes-side-effect" % "3.2.2"
//
//libraryDependencies += "org.apache.ctakes" % "ctakes-side-effect-res" % "3.2.2"
//
//libraryDependencies += "org.apache.ctakes" % "ctakes-dependency-parser" % "3.2.2"
//
//libraryDependencies += "org.apache.ctakes" % "ctakes-ytex" % "3.2.2"
//
//libraryDependencies += "org.apache.ctakes" % "ctakes-ytex-res" % "3.2.2"
//
//libraryDependencies += "org.apache.ctakes" % "ctakes-ytex-uima" % "3.2.2"
//
//libraryDependencies += "org.apache.ctakes" % "ctakes-dictionary-lookup-res" % "3.2.2"
//
//libraryDependencies += "org.apache.ctakes" % "ctakes-dictionary-lookup-fast" % "3.2.2"
//
//libraryDependencies += "org.apache.ctakes" % "ctakes-dictionary-lookup-fast-res" % "3.2.2"
//
//libraryDependencies += "org.apache.ctakes" % "ctakes-ytex-uima" % "3.2.2"
//
//libraryDependencies += "org.apache.ctakes" % "ctakes-clinical-pipeline" % "3.2.2"