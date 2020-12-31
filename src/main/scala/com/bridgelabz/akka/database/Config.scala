package com.bridgelabz.akka.database

/**
 * Created on 12/22/2020.
 * Class: Config.scala
 * Author: Rajat G.L.
 */
import com.bridgelabz.akka.database.Config.collection
import com.bridgelabz.akka.models.User

import scala.concurrent.Future
import org.mongodb.scala.{Completed, Document, MongoClient, MongoCollection, MongoDatabase, MongoExecutionTimeoutException}
import com.typesafe.scalalogging.LazyLogging

object Config extends LazyLogging {

  val mongoClient: MongoClient = MongoClient()
  val database: MongoDatabase = mongoClient.getDatabase("mydb")
  val collection: MongoCollection[Document] = database.getCollection("greeting")

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

  def sendRequest(user: User, collectionName: String): Future[Completed] = {

    val collection: MongoCollection[Document] = database.getCollection(collectionName)

    logger.info("Sending request with object: " + user)
    val doc: Document = Document("greeting" -> user.greeting, "name" -> user.name)
    collection.insertOne(doc).toFuture()
  }
}
