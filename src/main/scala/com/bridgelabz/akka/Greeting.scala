package com.bridgelabz.akka

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives
import akka.http.scaladsl.server.Directives.{complete, path}
import akka.stream.ActorMaterializer

import scala.concurrent.ExecutionContext

/**
 * Created on 12/19/2020.
 * Class: Greeting.scala
 * Author: Rajat G.L.
 */
object Greeting extends App {
    val host = "0.0.0.0"
    val port = 9000
    implicit val system: ActorSystem = ActorSystem("helloworld")
    implicit val executor: ExecutionContext = system.dispatcher
    implicit val materializer: ActorMaterializer = ActorMaterializer()

    def route = path("hello") {
      Directives.get {
        complete("Hello, World!")
      }
    }

    Http().bindAndHandle(route, host, port)
  }
