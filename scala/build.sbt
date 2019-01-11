

val rxVersion = "0.4.0"
val scalatagsVersion = "0.6.5"
val scalaJSdomVersion = "0.9.2"
val scaladgetVersion = "1.2.2-SNAPSHOT"
name := "zombies"

lazy val model = Project("model", file("model")) enablePlugins(SbtOsgi, ScalaJSPlugin) settings (
  scalaVersion := "2.12.8",
  OsgiKeys.exportPackage := Seq("zombies.*;-split-package:=merge-first"),
  OsgiKeys.importPackage := Seq("*;resolution:=optional"),
  OsgiKeys.privatePackage := Seq("!scala.*,!java.*,!monocle.*,!META-INF.*.RSA,!META-INF.*.SF,!META-INF.*.DSA,META-INF.services.*,META-INF.*,*"),
  OsgiKeys.requireCapability := """osgi.ee;filter:="(&(osgi.ee=JavaSE)(version=1.8))"""",
  osgiSettings
)



lazy val console = Project("console", file("console")) dependsOn (model) settings (
  libraryDependencies += "com.github.tomas-langer" % "chalk" % "1.0.2",
  )

lazy val buildGUI = taskKey[Unit]("buildGUI")

lazy val gui = Project("gui", file("gui")) dependsOn (model) enablePlugins (ExecNpmPlugin) settings(
  libraryDependencies += "com.lihaoyi" %%% "scalatags" % scalatagsVersion,
  libraryDependencies += "org.scala-js" %%% "scalajs-dom" % scalaJSdomVersion,
  libraryDependencies += "com.lihaoyi" %%% "scalarx" % rxVersion,
  libraryDependencies += "fr.iscpif.scaladget" %%% "svg" % scaladgetVersion,
  libraryDependencies += "fr.iscpif.scaladget" %%% "bootstrapnative" % scaladgetVersion,
  libraryDependencies += "fr.iscpif.scaladget" %%% "bootstrapslider" % scaladgetVersion,
  buildGUI := {

    val demoTarget = target.value
    val demoResource = (resourceDirectory in Compile).value

    IO.copyFile((fastOptJS in Compile).value.data, demoTarget / "js/demo.js")
    IO.copyFile(dependencyFile.value, demoTarget / "js/deps.js")
    IO.copyDirectory(cssFile.value, demoTarget / "css")
    IO.copyDirectory(demoResource, demoTarget)
  }
)

