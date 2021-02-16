package com.bridgelabz.akka.database

/**
 * Created on 12/22/2020.
 * Class: Config.scala
 * Author: Rajat G.L.
 */
import com.bridgelabz.akka.constants.Constants
import com.bridgelabz.akka.models.User
import com.typesafe.scalalogging.Logger

import scala.concurrent.Future
import org.mongodb.scala.{Completed, Document, MongoClient, MongoCollection, MongoDatabase, MongoExecutionTimeoutException, result}
import org.mongodb.scala.model.Filters.equal

class Config {
  /**
   *
   * @param user : Data to be added into database
   * @return : Future[Done]
   */
  def sendRequest(user: User) : Future[Completed] = {

    val logger: Logger = Logger("Config")
    logger.info("Sending request with object: " + user)
    val doc: Document = Document("greeting" -> user.greeting, "name" -> user.name)
    (new MongoConstants).collection.insertOne(doc).toFuture()
  }

  def deleteRequest(user: User): Future[result.DeleteResult] = {

    val logger: Logger = Logger("Config")
    logger.warn("Deleting request with object: " + user)
    (new MongoConstants).collection.deleteOne(equal("name", user.name)).toFuture()
  }
}
