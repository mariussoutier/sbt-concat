# sbt-concat

This is an example of a simple [sbt-web](https://github.com/sbt/sbt-web) plugin.

The plugin itself does nothing fancy, it only concatenates assets. You'd usually do this with JavaScript files
to save requests, but could also use it with CSS or even HTML.

To enable the plugin, add sbt-web and concat to your `plugins.sbt` and enable sbt-web on your root project. concat will
bootstrap itself and also import its main task, `concat`, into your `build.sbt` file. Add it to the asset pipeline to
run it on your assets:

`pipelineStages := Seq(concat)`

You can control the plugin by importing `ConcatKeys._` and overriding any of the following settings:

Option                  | Description
------------------------|------------
fileSeparator           | String to put between the files; defaults to empty String
outputFileName          | The output file name; defaults to `main.js`
includeFilter in concat | The files to concatenate; defaults to JavaScript assets

To play around with it, go to the `sbt-concat-example` project, run `sbt` and then `web-pipeline`. You cannot execute
`concat` directly because the task is a function that requires input mappings (take a look at it via `show concat`).

Also check out the [slides](http://www.slideshare.net/marius-soutier/sbt-webintro) for a talk I gave about sbt-web.
