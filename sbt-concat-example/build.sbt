import com.mariussoutier.sbt.Imports.ConcatKeys
import NameFilter._
import com.mariussoutier.sbt.Imports.ConcatKeys

name := "sbt-concat-example"

version := "1.0"

name := "sbt-concat-example"

scalaVersion := "2.10.4"

// Enabling SbtWeb will also trigger concat
lazy val root = (project in file(".")).enablePlugins(SbtWeb)

// c.js should not appear in all.js, but the file will be kept in assets
includeFilter in concat := (includeFilter in concat).value && { name: String => name != "c.js" }


// Override some settings
ConcatKeys.fileSeparator := ";"

ConcatKeys.concatOpts := Map(
  "all.js" -> (includeFilter in concat).value
)

// No need to import concat thanks to autoImport
pipelineStages := Seq(concat)
