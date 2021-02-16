package com.bridgelabz.routtest

import akka.actor.ActorSystem
import akka.http.scaladsl.model.{HttpEntity, HttpMethods, HttpRequest, MediaTypes, StatusCodes}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.util.ByteString
import com.bridgelabz.akka.Routes
import com.bridgelabz.akka.database.{Config, MongoUtils}
import com.bridgelabz.akka.models.User
import org.mockito.Mockito.when
import org.mongodb.scala.Completed
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

class RouteTestKit extends AnyWordSpec with Matchers with ScalatestRouteTest with MockitoSugar {

  "RouteTest" should {
    "check the status code for GET requests to the getJson path" in {

      val mockConfig: Config = mock[Config]
      implicit val system: ActorSystem = ActorSystem("AS")
      implicit val executor: ExecutionContext = system.dispatcher

      Get("/getJson") ~> Routes.route(mockConfig, executor, system) ~> check {
        response.status.equals(StatusCodes.Accepted)
      }
    }
    "check the status code for GET requests to the getXML path" in {

      val mockConfig: Config = mock[Config]
      implicit val system: ActorSystem = ActorSystem("AS")
      implicit val executor: ExecutionContext = system.dispatcher

      Get("/getXML") ~> Routes.route(mockConfig, executor, system) ~> check {
        response.status.equals(StatusCodes.Accepted)
      }
    }
    "check the status code for GET requests with NAME parameter to the getJson path" in {

      val mockConfig: Config = mock[Config]
      implicit val system: ActorSystem = ActorSystem("AS")
      implicit val executor: ExecutionContext = system.dispatcher

      Get("/getJson?name=Rajat") ~> Routes.route(mockConfig, executor, system) ~> check {
        response.status.equals(StatusCodes.Accepted)
      }
    }
    "check the status code for GET requests with NAME parameter to the getXML path" in {

      val mockConfig: Config = mock[Config]
      implicit val system: ActorSystem = ActorSystem("AS")
      implicit val executor: ExecutionContext = system.dispatcher

      Get("/getXML?name=Rajat") ~> Routes.route(mockConfig, executor, system) ~> check {
        response.status.equals(StatusCodes.Accepted)
      }
    }
    "check the status code for POST requests to the message path with valid data" in {

      val mockConfig: Config = mock[Config]
      implicit val system: ActorSystem = ActorSystem("AS")
      implicit val executor: ExecutionContext = system.dispatcher

      val user = User("Tester", "Hello")
      when(mockConfig.sendRequest(user)).thenReturn(Future.successful(Completed.apply()))

      val jsonRequest = ByteString(
        s"""
            {
              "name":"Tester",
              "greeting": "Hello"
            }
        """.stripMargin
      )

      val postRequest = HttpRequest(
        HttpMethods.POST,
        uri = "/message",
        entity = HttpEntity(MediaTypes.`application/json`, jsonRequest)
      )

      postRequest ~> Routes.route(mockConfig, executor, system) ~> check {
        response.status.equals(StatusCodes.Accepted)
      }
    }
    "check the status code for POST requests to the message path with invalid data" in {

      val user = User("Tester", "Hello")
      val mockConfig: Config = mock[Config]
      implicit val system: ActorSystem = ActorSystem("AS")
      implicit val executor: ExecutionContext = system.dispatcher

      when(mockConfig.sendRequest(user)).thenReturn(Future.failed(new Throwable()))

      val jsonRequest = ByteString(
        s"""
            {
              "name":"Tester",
              "greeting": "Hello"
            }
        """.stripMargin
      )

      val postRequest = HttpRequest(
        HttpMethods.POST,
        uri = "/message",
        entity = HttpEntity(MediaTypes.`application/json`, jsonRequest)
      )

      postRequest ~> Routes.route(mockConfig, executor, system) ~> check {
        !response.status.equals(StatusCodes.Accepted)
      }
    }

    "return a list of users with a given name" in {

      val future = MongoUtils.getAllUsers("hello")
      future.onComplete{
        case Success(_) => assert(true)
        case Failure(exception) => assert(false)
      }
    }

    "config should save and delete a dummy chat with no errors" in {

      val user = User("TesterRANDOM", "Hello")
      val config = new Config
      config.sendRequest(user)
      config.deleteRequest(user)
    }
  }
}
