package com.mariussoutier.sbt

import com.typesafe.sbt.web._
import com.typesafe.sbt.web.pipeline.Pipeline
import sbt.Keys._
import sbt._

object Imports {
  // The task that will be part of sbt's asset pipeline
  val concat = taskKey[Pipeline.Stage]("Concat all JS files")

  // Namespace settings so we don't have to prefix them
  object ConcatKeys {
    // Customize concat
    val fileSeparator = settingKey[String]("The character which combines the files")
    val outputFileName = settingKey[String]("The output file")
  }

}

/**
 * sbt-web plugin to concatenate files. Just like grunt-contrib-concat.
 */
object ConcatPlugin extends AutoPlugin {

  // Dependencies on other plugins, combine with '&&'
  override def requires = SbtWeb

  // NoTrigger = Must be enabled manually
  // AllRequirements = If required project is enabled, this will be as well
  override def trigger = AllRequirements

  // User of our plugin doesn't have to import concat task himself, autoImport puts it into scope
  val autoImport = Imports

  import autoImport._
  import com.typesafe.sbt.web.Import._, WebKeys._

  override def projectSettings: Seq[Def.Setting[_]] = Seq(
    // By default, concat JavaScript assets
    // We are reusing sbt's includeFilter but scope it to our task
    includeFilter in concat := (jsFilter in Assets).value,
    ConcatKeys.outputFileName := "main.js",
    ConcatKeys.fileSeparator := "\n",
    concat := { mappings: Seq[PathMapping] =>
      // Use log for debugging
      //val log = streams.value.log
      val targetDir = (sourceManaged in Assets).value / "concat"
      val (jsFiles, rest) = mappings.partition { case (file, path) => (includeFilter in concat).value.accept(file)}
      val concatFile = targetDir / ConcatKeys.outputFileName.value
      // sbt's IO allows to easily handle files
      IO.delete(concatFile) // delete from previous runs if clean wasn't called
      IO.touch(concatFile)
      for ((jsFile, _) <- jsFiles) {
        val content = IO.read(jsFile)
        IO.append(concatFile, content)
      }
      // Create a mapping (see PathMapping) for the concatenated file
      val concatMapping = Seq(concatFile) pair relativeTo(targetDir)
      // Pass the concatenated file and the untouched files to next pipeline stage; source files will not be passed on
      concatMapping ++ rest
    }
  )
}
