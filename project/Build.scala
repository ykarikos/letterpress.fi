import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "letterpress"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    // Add your project dependencies here,
    jdbc,
    anorm,
//    "mysql" % "mysql-connector-java" % "5.1.21",    
    "org.scalatest" %% "scalatest" % "1.9.1" % "test",
    "junit" % "junit" % "4.10" % "test"
  )

  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here      
  )
}