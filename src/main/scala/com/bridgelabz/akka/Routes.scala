package com.bridgelabz.akka

/**
 * Created on 12/26/2020.
 * Class: Routes.scala
 * Author: Rajat G.L.
 */

import akka.actor.ActorSystem
import akka.http.javadsl.model.StatusCodes
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.server.{Directives, ExceptionHandler, Route}
import com.bridgelabz.akka.database.Config
import com.bridgelabz.akka.database.MongoUtils.getAllUsers
import com.bridgelabz.akka.models.{User, UserJsonSupport}
import com.thoughtworks.xstream.XStream
import com.thoughtworks.xstream.io.xml.DomDriver
import com.typesafe.scalalogging.Logger
import org.mongodb.scala.Completed

import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.util.{Failure, Success}

object Routes extends App with Directives with UserJsonSupport {

  // $COVERAGE-OFF$
  val logger = Logger("Root")
  logger.info("Route object accessed")
  //host and port numbers set via respective environment variables
  val host = System.getenv("Host")
  val port = System.getenv("Port").toInt

  //maintains a pool of actors
  implicit val system: ActorSystem = ActorSystem("AS")
  //maintains and executes actor system
  implicit val executor: ExecutionContext = system.dispatcher

  // Handling Arithmetic and Null Pointer Exceptions
  val exceptionHandler = ExceptionHandler {
    case aex: ArithmeticException =>
      extractUri { uri =>
        logger.error(s"Request to $uri could not be handled normally: ${aex.getMessage}")
        complete(HttpResponse(StatusCodes.BAD_REQUEST.intValue(), entity = "Number could not be parsed. Is there a text were a number should be?"))
      }
    case nex: NullPointerException =>
      extractUri { uri =>
        logger.error(s"Request to $uri could not be handled normally: ${nex.getMessage}")
        complete(HttpResponse(StatusCodes.BAD_REQUEST.intValue(), entity = "Null value found while parsing the data. Contact the admin."))
      }
    case ex: Exception =>
      extractUri { uri =>
        logger.error(s"Request to $uri could not be handled normally: ${ex.getMessage}")
        complete(HttpResponse(StatusCodes.BAD_REQUEST.intValue(), entity = "Some error occured. Please try again later."))
      }
  }

  // $COVERAGE-ON$
  /**
   * handles all the get post requests to appropriate path endings
   * @return
   */
  def route(config: Config, executorContext: ExecutionContext, actorSystem: ActorSystem): Route = {
    implicit val system: ActorSystem = actorSystem; implicit val executor: ExecutionContext = executorContext
    handleExceptions(exceptionHandler) {
      concat(get {concat(
          path("getJson") {
            //optional "name" parameter to GET byName
            parameters("name".?) { (name: Option[String]) => {
              if (name.isDefined) {
                logger.debug("JSON data of specified user provided")
                complete(getAllUsers(name.get).flatMap(sequence => Future(sequence.filter(user => user.name.equalsIgnoreCase(name.get)))))
              } else {
                logger.debug("JSON data of all users provided")
                complete(getAllUsers)
              }
            }}
          }, path("getXML") {
            parameters("name".?) { (name: Option[String]) => {
              var finalDisplayResult: Future[Seq[User]] = Future(Seq())
              if (name.isDefined) {
                logger.debug("XML data of specified user provided")
                finalDisplayResult = getAllUsers(name.get).flatMap(sequence => Future(sequence.filter(user => user.name.equalsIgnoreCase(name.get))))
              }
              else {
                logger.debug("XML data of all users provided")
                finalDisplayResult = getAllUsers
              }
              val data = Await.result(finalDisplayResult, 10.seconds)
              val xStream = new XStream(new DomDriver())
              val xml = xStream.toXML(data)
              complete(xml)
            }}
          }
        )
      } ~ post {
        path("message") {
          entity(as[User]) { emp =>
            val request: Future[Completed] = config.sendRequest(emp)
            onComplete(request) {
              case Success(_) => logger.debug("Data Insertion Complete")
                complete("Data Inserted!")
              case _ => complete(StatusCodes.BAD_REQUEST.intValue() -> "Data is invalid.")
            }
          }
        }
      })
    }
  }

  // $COVERAGE-OFF$
  //server binding
  val config: Config = new Config()
  val binder = Http().newServerAt(host, port).bind(route(config, executor, system))

  binder.onComplete {
    case Success(serverBinding) => logger.debug("Server Binding Successful")
    case Failure(error) => logger.debug(s"Server Binding Failed: ${error.getMessage}")
  }
}
