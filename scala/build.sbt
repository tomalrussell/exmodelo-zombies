

enablePlugins(SbtOsgi)

def monadLessVersion = "0.0.13"
val monocleVersion = "1.5.0"

name := "zombies"

lazy val model = Project("model", file("model")) settings (
  scalaVersion := "2.12.8",

//  OsgiKeys.exportPackage := Seq("zombies.*;-split-package:=merge-first"),
//  OsgiKeys.importPackage := Seq("*;resolution:=optional"),
//  OsgiKeys.privatePackage := Seq("!scala.*,!java.*,!monocle.*,!META-INF.*.RSA,!META-INF.*.SF,!META-INF.*.DSA,META-INF.services.*,META-INF.*,*"),
//  OsgiKeys.requireCapability := """osgi.ee;filter:="(&(osgi.ee=JavaSE)(version=1.8))"""",
//
//  addCompilerPlugin("org.scalamacros" %% "paradise" % "2.1.0" cross CrossVersion.full)
)



lazy val console = Project("console", file("console")) dependsOn  (model) settings (
  libraryDependencies += "com.github.tomas-langer" % "chalk" % "1.0.2",
)