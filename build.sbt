name := """backend-management"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.8"

lazy val H2Version = "1.4.191"
lazy val playSlickVersion = "2.0.0"
lazy val jqueryVersion = "3.0.0-alpha1"
lazy val bootstrapVersion = "4.0.0-alpha.2"

libraryDependencies ++= Seq(
  filters,
  cache,
  ws,
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.0" % Test,

  "com.h2database" % "h2" % H2Version,
  "com.typesafe.play" %% "play-slick" % playSlickVersion,
  "com.typesafe.play" %% "play-slick-evolutions" % playSlickVersion,

  "org.webjars" % "jquery" % jqueryVersion,
  "org.webjars" % "bootstrap" % bootstrapVersion
)

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"

routesGenerator := InjectedRoutesGenerator