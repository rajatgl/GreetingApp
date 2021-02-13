name := "AkkaPractice"

version := "0.1"

scalaVersion := "2.12.6"

libraryDependencies ++= Seq(  "com.typesafe.akka" %% "akka-actor" % "2.5.32",  "com.typesafe.akka" %% "akka-stream" % "2.5.32",  "com.typesafe.akka" %% "akka-http" % "10.2.2")
libraryDependencies += "org.mongodb.scala" %% "mongo-scala-driver" % "2.9.0"
libraryDependencies += "com.lightbend.akka" %% "akka-stream-alpakka-mongodb" % "2.0.2"
libraryDependencies += "com.typesafe.akka" %% "akka-http-spray-json" % "10.2.2"
libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2"
libraryDependencies += "com.thoughtworks.xstream" % "xstream" % "1.4.11.1"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.2" % "test"
libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-stream-testkit" % "2.5.32",
  "com.typesafe.akka" %% "akka-http-testkit" % "10.2.2",
  "org.scalatestplus" %% "mockito-3-4" % "3.2.3.0" % "test",
)
libraryDependencies += "org.apache.logging.log4j" % "log4j-api" % "2.14.0"
libraryDependencies += "org.apache.logging.log4j" % "log4j-core" % "2.14.0"
libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.2.3" % Runtime
libraryDependencies += "ch.qos.logback" % "logback-core" % "1.2.3"
