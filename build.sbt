import play.Project._

name := """chartbeat-metrics"""

version := "1.0"

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  cache,
  "com.datastax.cassandra" % "cassandra-driver-core" % "2.0.1"
)

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
//routesGenerator := InjectedRoutesGenerator

playScalaSettings