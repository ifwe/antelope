import sbt._
import sbt.Keys._

object BuildSettings {
  val buildSettings = Defaults.coreDefaultSettings ++ Seq(
    organization := "co.ifwe",
    version := "0.1.0-SNAPSHOT",
    scalaVersion := "2.10.4",
    scalacOptions ++= Seq("-deprecation","-feature","-language:implicitConversions")
  )
}

object PredictBuild extends Build {
  import BuildSettings._

  lazy val root = Project("root", file("."),
    settings = buildSettings
  ) aggregate (antelope, demo)

  lazy val antelope = Project("antelope", file("antelope"),
    settings = buildSettings ++ Seq (
      name := "antelope",
      libraryDependencies := libraryDependencies.value ++ Seq(
        "org.slf4j" % "slf4j-simple" % "1.7.7",
        "org.scalatest" %% "scalatest" % "2.2.2"
      )
    )
  )

  lazy val demo = Project("demo", file("demo"),
    settings = buildSettings ++ Seq (
      name := "demo"
    )
  ) dependsOn(antelope)

}
