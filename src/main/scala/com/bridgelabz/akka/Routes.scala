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
import com.thoughtworks.xstream.XStream
import com.thoughtworks.xstream.io.xml.DomDriver
import com.typesafe.scalalogging.Logger
import org.mongodb.scala.Completed

import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.util.{Failure, Success}

object Routes extends App with Directives with UserJsonSupport {

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

  /**
   * handles all the get post requests to appropriate path endings
   * @return
   */
  def route : Route =
    handleExceptions(exceptionHandler){
      concat(
        get {
          concat(
            // GET "/getJson" path to fetch user objects in JSON format
            path("getJson") {
              //optional "name" parameter to GET byName
              parameters("name".?){(name: Option[String])=>{
                if(name.isDefined) {
                  logger.debug("JSON data of specified user provided")
                  complete(getAllUsers(name.get).flatMap(sequence => Future(sequence.filter(user => user.name.equalsIgnoreCase(name.get)))))
                } else {
                  logger.debug("JSON data of all users provided")
                  complete(getAllUsers)
                }
              }}
            },
            // GET "/getJson" path to fetch user objects in XML format
            path("getXML") {
              val greetingSeqFuture: Future[Seq[User]] = getAllUsers
              //optional "name" parameter to GET byName
              parameters("name".?){(name: Option[String])=>{
                var finalDisplayResult: Future[Seq[User]] = null
                if(name.isDefined) {
                  logger.debug("XML data of specified user provided")
                  finalDisplayResult = getAllUsers(name.get).flatMap(sequence => Future(sequence.filter(user => user.name.equalsIgnoreCase(name.get))))
                }
                else {
                  logger.debug("XML data of all users provided")
                  finalDisplayResult = getAllUsers
                }
                val data = Await.result(finalDisplayResult,10.seconds)
                val xStream = new XStream(new DomDriver())
                val xml = xStream.toXML(data)
                complete(xml)
              }}
            }
          )
        } ~ post {
          // POST data to server via path name mentioned
          path("message") {
            entity(as[User]) {
              emp =>
                val request: Future[Completed] = sendRequest(emp)
                onComplete(request) {
                  _ => logger.debug("Data Insertion Complete");complete("Data Inserted!")
                }
            }
          }
        }
      )
    }
  //server binding
  val binder = Http().newServerAt(host,port).bind(route)
  binder.onComplete {
    case Success(serverBinding) => logger.debug("Server Binding Successful"); println(println(s"Listening to ${serverBinding.localAddress}"))
    case Failure(error) => logger.debug("Server Binding Failed"); println(s"Error : ${error.getMessage}")
  }
}