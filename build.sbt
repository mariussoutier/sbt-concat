sbtPlugin := true

organization := "com.mariussoutier.sbt"

name := "sbt-concat"

version := "1.0.1-SNAPSHOT"

scalaVersion := "2.10.4"

resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

addSbtPlugin("com.typesafe.sbt" % "sbt-web" % "1.0.2")
