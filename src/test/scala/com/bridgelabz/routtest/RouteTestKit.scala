package com.bridgelabz.routtest

import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import com.bridgelabz.akka.Routes
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest

class RouteTestKit extends AnyWordSpec with Matchers with ScalatestRouteTest {

  "RouteTest" should {
    "check the status code for GET requests to the getJson path" in {

      Get("/getJson") ~> Routes.route ~> check {
        response.status.equals(StatusCodes.Accepted)
      }
    }
    "check the status code for GET requests to the getXML path" in {
      Get("/getXML") ~> Routes.route ~> check {
        response.status.equals(StatusCodes.Accepted)
      }
    }
    "check the status code for GET requests with NAME parameter to the getJson path" in {
      Get("/getJson?name=Rajat") ~> Routes.route ~> check {
        response.status.equals(StatusCodes.Accepted)
      }
    }
    "check the status code for GET requests with NAME parameter to the getXML path" in {
      Get("/getXML?name=Rajat") ~> Routes.route ~> check {
        response.status.equals(StatusCodes.Accepted)
      }
    }
    "check the status code for POST requests to the message path with valid data" in {
      Post("/message", "{greeting: Hey, name: TestKit}") ~> Routes.route ~> check {
        response.status.equals(StatusCodes.Accepted)
      }
    }
    "check the status code for POST requests to the message path with invalid data" in {
      Post("/message", "{}") ~> Routes.route ~> check {
        !response.status.equals(StatusCodes.Accepted)
      }
    }
  }
}
