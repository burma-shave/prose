import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "prose"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    // Add your project dependencies here,
    "com.typesafe.slick" %% "slick" % "1.0.0",
    "com.h2database" % "h2" % "1.3.166",
    "com.typesafe" % "play-slick_2.10" % "0.3.1",
    jdbc
   // anorm
  )


  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here
    resolvers += Resolver.url("github repo for play-slick", url("http://loicdescotte.github.com/releases/"))(Resolver.ivyStylePatterns)
  )

}
