package com.mariussoutier.sbt

import com.typesafe.sbt.web._
import com.typesafe.sbt.web.pipeline.Pipeline
import sbt.Keys._
import sbt._

import scala.collection.mutable.ListBuffer

object Imports {
  // The task that will be part of sbt's asset pipeline
  val concat = taskKey[Pipeline.Stage]("Concat all JS files")

  // Namespace settings so we don't have to prefix them
  object ConcatKeys {
    // Customize concat
    val fileSeparator = settingKey[String]("The character which combines the files")
    // Concat opts - map of file names and filter for them
    val concatOpts = SettingKey[Map[String, FileFilter]]("concat-opts", "Definiton of files concatenations")
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

  import com.mariussoutier.sbt.ConcatPlugin.autoImport._
  import com.typesafe.sbt.web.Import.WebKeys._
  import com.typesafe.sbt.web.Import._

  override def projectSettings: Seq[Def.Setting[_]] = Seq(
    // By default, concat JavaScript assets
    // We are reusing sbt's includeFilter but scope it to our task
    includeFilter in concat := (jsFilter in Assets).value,
    ConcatKeys.concatOpts := Map("main.js" -> (includeFilter in concat).value),
    ConcatKeys.fileSeparator := "\n",
    concat := { mappings: Seq[PathMapping] =>
      // Use log for debugging
      //val log = streams.value.log
      val targetDir = (sourceManaged in Assets).value / "concat"
      var result = Map[String, ListBuffer[File]]()
      val concatOptsVal = ConcatKeys.concatOpts.value
      val fileSeparatorVal = ConcatKeys.fileSeparator.value
      val (jsFiles, rest) = mappings.partition { case (file, path) =>
        val concatPart = concatOptsVal.filter((value: (String, FileFilter)) => {
          value._2.accept(file)
        })

        if (!concatPart.isEmpty) {
          if (result.contains(concatPart.head._1)) {
            result.get(concatPart.head._1).map(list => list += file)
          } else {
            result += (concatPart.head._1 -> ListBuffer(file))
          }
        }
        !concatPart.isEmpty
      }

      var concatMapping = ListBuffer[(File, String)]()

      for (item <- result) {
        val concatFile = targetDir / item._1

        // sbt's IO allows to easily handle files
        IO.delete(concatFile) // delete from previous runs if clean wasn't called
        IO.touch(concatFile)
        for (jsFile <- item._2) {
          val content = IO.read(jsFile)
          IO.append(concatFile, content.concat(fileSeparatorVal))
        }

        concatMapping = concatMapping ++ Seq(concatFile).pair(relativeTo(targetDir))
      }

      // Pass the concatenated file and the untouched files to next pipeline stage; source files will not be passed on
      concatMapping ++ rest
    }
  )
}
