import com.mariussoutier.sbt.Imports.ConcatKeys
import NameFilter._

name := "sbt-concat-example"

version := "1.0"

name := "sbt-concat-example"

scalaVersion := "2.10.4"

// Enabling SbtWeb will also trigger concat
lazy val root = (project in file(".")).enablePlugins(SbtWeb)

// Override some settings
ConcatKeys.fileSeparator := ";"

ConcatKeys.outputFileName := "all.js"

// c.js should not appear in all.js, but the file will be kept in assets
includeFilter in concat := (includeFilter in concat).value && { name: String => name != "c.js" }

// No need to import concat thanks to autoImport
pipelineStages := Seq(concat)
