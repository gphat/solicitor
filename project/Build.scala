import sbt._
import Keys._

object Build extends Build {

  lazy val solicitorSettings = Defaults.defaultSettings ++ Seq(
    crossScalaVersions := Seq("2.10.4"),
    scalaVersion <<= (crossScalaVersions) { versions => versions.head },
    scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature"),
    publishTo := Some(Resolver.file("file", new File("/Users/gphat/src/mvn-repo/releases"))),
    resolvers ++= Seq(
      "spray repo" at "http://repo.spray.io",
      "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"
    ),
    libraryDependencies ++= Seq(
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

  lazy val http = Project(
    id = "solicitor-http",
    base = file("http"),
    settings = solicitorSettings ++ Seq(
      description := "HTTP Config Backend",
      version := "1.0",
      libraryDependencies ++= Seq(
        "com.typesafe.akka" %% "akka-actor" % "2.3.0",
        "io.spray" % "spray-can" % "1.3.0",
        "io.spray" % "spray-client" % "1.3.0"
      )
    )
  ) dependsOn(
    root
  )

  lazy val typesafe = Project(
    id = "solicitor-typesafe",
    base = file("typesafe"),
    settings = solicitorSettings ++ Seq(
      description := "Typesafe Config Backend",
      version := "1.0",
      libraryDependencies ++= Seq(
        "com.typesafe" % "config" % "1.2.0"
      )
    )
  ) dependsOn(
    root
  )
}
