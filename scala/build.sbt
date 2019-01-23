

val rxVersion = "0.4.0"
val scalatagsVersion = "0.6.5"
val scalaJSdomVersion = "0.9.2"
val scaladgetVersion = "1.2.3"
name := "zombies"

lazy val ode = Project("ode", file("ode")) settings (
  scalaVersion := "2.12.8"
  )


lazy val model = Project("model", file("model")) enablePlugins(SbtOsgi, ScalaJSPlugin) settings(
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

lazy val guiDependencies = Seq(
  libraryDependencies += "com.lihaoyi" %%% "scalatags" % scalatagsVersion,
  libraryDependencies += "org.scala-js" %%% "scalajs-dom" % scalaJSdomVersion,
  libraryDependencies += "com.lihaoyi" %%% "scalarx" % rxVersion,
  libraryDependencies += "fr.iscpif.scaladget" %%% "svg" % scaladgetVersion,
  libraryDependencies += "fr.iscpif.scaladget" %%% "bootstrapnative" % scaladgetVersion,
  libraryDependencies += "fr.iscpif.scaladget" %%% "bootstrapslider" % scaladgetVersion
)

def guiBuilder(demoTarget: File, demoResource: File, jsBuild: File, dependencyJS: File, depsCSS: File) = {

  IO.copyFile(jsBuild, demoTarget / "js/demo.js")
  IO.copyFile(dependencyJS, demoTarget / "js/deps.js")
  IO.copyDirectory(depsCSS, demoTarget / "css")
  IO.copyDirectory(demoResource, demoTarget)
}

lazy val guiUtils = Project("guiUtils", file("guiUtils")) dependsOn (model) enablePlugins (ExecNpmPlugin) settings (
  guiDependencies
  )

lazy val zombieland = Project("zombieland", file("zombieland")) dependsOn (guiUtils) enablePlugins (ExecNpmPlugin) settings (
  buildGUI := guiBuilder(target.value, (resourceDirectory in Compile).value, (fullOptJS in Compile).value.data, dependencyFile.value, cssFile.value)
  )

lazy val vigilence = Project("vigilence", file("vigilence")) dependsOn (guiUtils) enablePlugins(SbtOsgi, ExecNpmPlugin) settings(
  scalaVersion := "2.12.8",
  OsgiKeys.exportPackage := Seq("zombies.*;-split-package:=merge-first"),
  OsgiKeys.importPackage := Seq("*;resolution:=optional"),
  OsgiKeys.privatePackage := Seq("!scala.*,!java.*,!monocle.*,!META-INF.*.RSA,!META-INF.*.SF,!META-INF.*.DSA,META-INF.services.*,META-INF.*,*"),
  OsgiKeys.requireCapability := """osgi.ee;filter:="(&(osgi.ee=JavaSE)(version=1.8))"""",
  osgiSettings,
  buildGUI := guiBuilder(target.value, (resourceDirectory in Compile).value, (fullOptJS in Compile).value.data, dependencyFile.value, cssFile.value)
)


lazy val spatialsens = Project("spatialsens", file("spatialsens")) dependsOn (model)

lazy val spatialsensgui = Project("spatialsensgui", file("spatialsens")) dependsOn (model) enablePlugins (ExecNpmPlugin) settings(
  target := file("spatialsens/targetgui"),
  //libraryDependencies += "org.scala-graph" %% "graph-core" % "1.12.5",
  libraryDependencies += "com.lihaoyi" %%% "scalatags" % scalatagsVersion,
  libraryDependencies += "org.scala-js" %%% "scalajs-dom" % scalaJSdomVersion,
  libraryDependencies += "com.lihaoyi" %%% "scalarx" % rxVersion,
  libraryDependencies += "fr.iscpif.scaladget" %%% "svg" % scaladgetVersion,
  libraryDependencies += "fr.iscpif.scaladget" %%% "bootstrapnative" % scaladgetVersion,
  libraryDependencies += "fr.iscpif.scaladget" %%% "bootstrapslider" % scaladgetVersion,
  buildGUI := {

    val demoTarget = target.value
    val demoResource = (resourceDirectory in Compile).value

    IO.copyFile((fullOptJS in Compile).value.data, demoTarget / "js/demo.js")
    IO.copyFile(dependencyFile.value, demoTarget / "js/deps.js")
    IO.copyDirectory(cssFile.value, demoTarget / "css")
    IO.copyDirectory(demoResource, demoTarget)
  }
)
