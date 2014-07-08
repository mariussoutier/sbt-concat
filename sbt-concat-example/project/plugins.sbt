lazy val plugin = file("../").getCanonicalFile.toURI

lazy val root = Project("plugins", file(".")).dependsOn(plugin)

// In a real project you'd use addSbtPlugin(... % ... % ...)
