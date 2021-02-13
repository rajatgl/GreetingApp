package com.bridgelabz.akka.database

/**
 * Created on 12/22/2020.
 * Class: Config.scala
 * Author: Rajat G.L.
 */
import com.bridgelabz.akka.constants.Constants
import com.bridgelabz.akka.models.User

import scala.concurrent.Future
import org.mongodb.scala.{Completed, Document, MongoClient, MongoCollection, MongoDatabase, MongoExecutionTimeoutException, result}
import com.typesafe.scalalogging.LazyLogging
import org.mongodb.scala.model.Filters.equal

class Config extends LazyLogging {

  val mongoClient: MongoClient = MongoClient()
  val database: MongoDatabase = mongoClient.getDatabase(Constants.databaseName)
  val collection: MongoCollection[Document] = database.getCollection(Constants.collectionName)

  /**
   *
   * @param user : Data to be added into database
   * @return : Future[Done]
   */
  def sendRequest(user: User) : Future[Completed] = {

    logger.info("Sending request with object: " + user)
    val doc: Document = Document("greeting" -> user.greeting, "name" -> user.name)
    collection.insertOne(doc).toFuture()
  }

  def deleteRequest(user: User): Future[result.DeleteResult] = {

    collection.deleteOne(equal("name", user.name)).toFuture()
  }
}
