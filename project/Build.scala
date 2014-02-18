import sbt._
import Keys._

object Build extends Build {

  lazy val solicitorSettings = Defaults.defaultSettings ++ Seq(
    crossScalaVersions := Seq("2.10.3"),
    scalaVersion <<= (crossScalaVersions) { versions => versions.head },
    publishTo := Some(Resolver.file("file", new File("/Users/gphat/src/mvn-repo/releases"))),
    libraryDependencies ++= Seq(
      "net.databinder.dispatch" %% "dispatch-core" % "0.11.0",
      "org.clapper" %% "grizzled-slf4j" % "1.0.1",
      "org.specs2" %% "specs2" % "1.14" % "test",
      "org.slf4j" % "slf4j-simple" % "1.7.5" % "test"
    )
  )

  lazy val root = Project(
    id = "solicitor",
    base = file("core"),
    settings = solicitorSettings ++ Seq(
      description := "Core Solicitor",
      version := "1.0"
    )
  )

  lazy val typesafe = Project(
    id = "solicitor-typesafe",
    base = file("typesafe"),
    settings = solicitorSettings ++ Seq(
      description := "Typesafe Config",
      version := "1.0",
      libraryDependencies ++= Seq(
        "com.typesafe" % "config" % "1.2.0"
      )
    )
  ) dependsOn(
    root
  )
}