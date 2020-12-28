package com.bridgelabz.akka

/**
 * Created on 12/26/2020.
 * Class: Routes.scala
 * Author: Rajat G.L.
 */

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.server.{Directives, ExceptionHandler, Route}
import com.bridgelabz.akka.database.Config.sendRequest
import com.bridgelabz.akka.database.MongoUtils.getAllUsers
import com.bridgelabz.akka.models.{User, UserJsonSupport}
import org.mongodb.scala.Completed

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

object Routes extends App with Directives with UserJsonSupport {

  val host = "localhost"
  val port = 9000

  implicit val system: ActorSystem = ActorSystem("AS")
  implicit val executor: ExecutionContext = system.dispatcher

  // Handling Arithmetic and Null Pointer Exceptions
  val exceptionHandler = ExceptionHandler {
    case _: ArithmeticException =>
      extractUri { uri =>
        println(s"Request to $uri could not be handled normally")
        complete(HttpResponse(400, entity = "Number could not be parsed. Is there a text were a number should be?"))
      }
    case _: NullPointerException =>
      extractUri { uri =>
        println(s"Request to $uri could not be handled normally")
        complete(HttpResponse(402, entity = "Null value found while parsing the data. Contact the admin."))
      }
    case _: Exception =>
      extractUri { uri =>
        println(s"Request to $uri could not be handled normally")
        complete(HttpResponse(408, entity = "Some error occured. Please try again later."))
      }
  }

  def route : Route =
    handleExceptions(exceptionHandler){
      concat(
        get {
          concat(
            path("getJson") {
              val greetingSeqFuture: Future[Seq[User]] = getAllUsers
              complete(greetingSeqFuture)
            }
          )
        } ~ post {
          path("message") {
            entity(as[User]) {
              emp =>
                val request: Future[Completed] = sendRequest(emp)
                onComplete(request) {
                  _ => complete("Data Inserted!")
                }
            }
          }
        }
      )
    }

  val binder = Http().newServerAt(host,port).bind(route)
  binder.onComplete {
    case Success(serverBinding) => println(println(s"Listening to ${serverBinding.localAddress}"))
    case Failure(error) => println(s"Error : ${error.getMessage}")
  }
}