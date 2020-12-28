package com.bridgelabz.akka.models

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

/**
 * Created on 12/22/2020.
 * Class: UserJsonSupport.scala
 * Author: Rajat G.L.
 */
trait UserJsonSupport extends DefaultJsonProtocol with SprayJsonSupport {
  implicit val PortfolioFormats: RootJsonFormat[User] = jsonFormat2(User)
}
