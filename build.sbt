sbtPlugin := true

name := "sbt-concat"

version := "1.0"

scalaVersion := "2.10.4"

resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

addSbtPlugin("com.typesafe.sbt" % "sbt-web" % "1.0.2")
