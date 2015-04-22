import com.mojolly.scalate.ScalatePlugin.ScalateKeys._
import com.mojolly.scalate.ScalatePlugin._
import org.scalatra.sbt._
import sbt.Keys._
import sbt._


object BuildSettings {
  val buildSettings = Defaults.coreDefaultSettings ++ Seq(
    organization := "co.ifwe",
    version := "0.2.0-SNAPSHOT",
    scalaVersion := "2.10.4",
    scalacOptions ++= Seq("-deprecation", "-feature", "-language:implicitConversions")
  )
}

object PredictBuild extends Build {
  import BuildSettings._

  lazy val root = Project("root", file("."),
    settings = buildSettings
  ) aggregate (antelope, demo, demoweb)

  lazy val antelope = Project("antelope", file("antelope"),
    settings = buildSettings ++ Seq (
      name := "antelope",
      libraryDependencies := libraryDependencies.value ++ Seq(
        "tv.cntt" %% "chill-scala" % "1.2",
        "org.slf4j" % "slf4j-simple" % "1.7.7",
        "org.scalatest" %% "scalatest" % "2.2.2"
      )
    )
  )

  lazy val demo = Project("demo-best-buy", file("demo-best-buy"),
    settings = buildSettings ++ Seq (
      name := "demo"
    )
  ) dependsOn(antelope)

  val ScalatraVersion = "2.3.0"
  lazy val demoweb = Project ("demo-best-buy-web", file("demo-best-buy-web"),
    settings = buildSettings ++ ScalatraPlugin.scalatraWithJRebel ++ scalateSettings ++ Seq(
      name := "Antelope Best Buy Demo",
      resolvers += Classpaths.typesafeReleases,
      libraryDependencies ++= Seq (
        "org.scalatra" %% "scalatra" % ScalatraVersion,
        "org.scalatra" %% "scalatra-scalate" % ScalatraVersion,
        "org.scalatra" %% "scalatra-specs2" % ScalatraVersion % "test",
        "ch.qos.logback" % "logback-classic" % "1.1.2" % "runtime",
        "org.eclipse.jetty" % "jetty-webapp" % "9.1.5.v20140505" % "container",
        "org.eclipse.jetty" % "jetty-plus" % "9.1.5.v20140505" % "container",
        "javax.servlet" % "javax.servlet-api" % "3.1.0",
        "org.scalatra" %% "scalatra-json" % ScalatraVersion,
        "org.json4s"   %% "json4s-jackson" % "3.2.11"
      ),
      scalateTemplateConfig in Compile <<= (sourceDirectory in Compile){ base =>
        Seq(
          TemplateConfig(
            base / "webapp" / "WEB-INF" / "templates",
            Seq.empty,  /* default imports should be added here */
            Seq(
              Binding("context", "_root_.org.scalatra.scalate.ScalatraRenderContext", importMembers = true, isImplicit = true)
            ),  /* add extra bindings here */
            Some("templates")
          )
        )
      }
    )
  ) dependsOn(demo)

  lazy val demodating = Project("demo-dating-simulation", file("demo-dating-simulation"),
    settings = buildSettings ++ Seq (
      name := "demodating",
      libraryDependencies := libraryDependencies.value ++ Seq(
        "org.apache.commons" % "commons-math3" % "3.5"
      )
    )
  ) dependsOn(antelope)

}
