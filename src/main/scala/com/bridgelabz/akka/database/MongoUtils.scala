package com.bridgelabz.akka.database

/**
 * Created on 12/26/2020.
 * Class: MongoUtils.scala
 * Author: Rajat G.L.
 */
import akka.actor.TypedActor.dispatcher
import com.bridgelabz.akka.models.User
import org.mongodb.scala.bson.codecs.Macros
import org.mongodb.scala.MongoClient.DEFAULT_CODEC_REGISTRY
import org.bson.codecs.configuration.{CodecProvider, CodecRegistries, CodecRegistry}
import org.mongodb.scala.{MongoClient, MongoCollection, MongoDatabase}

import scala.concurrent.Future

object MongoUtils {
  //Provides a codec for encryption and decryption
  val greetingCodecProvider: CodecProvider = Macros.createCodecProvider[User]()

  //Maintains set of Codec instances
  val codecRegistry: CodecRegistry = CodecRegistries.fromRegistries(
    CodecRegistries.fromProviders(greetingCodecProvider),
    DEFAULT_CODEC_REGISTRY
  )

  //creates new mongo instance
  val mongoClient: MongoClient = MongoClient()

  val mongoDatabase: MongoDatabase =
    mongoClient
      .getDatabase("mydb")
      .withCodecRegistry(codecRegistry)

  val greetingCollection: MongoCollection[User] =
    mongoDatabase.getCollection[User]("greeting")

  // method to fetch entire data from mongodb database
  def getAllUsers: Future[Seq[User]] = {
    greetingCollection.find().toFuture()
  }

  def getAllUsers(userName: String):Future[Seq[User]] = {

    val greetingCollection: MongoCollection[User] =
      mongoDatabase.getCollection[User]("greeting")

    greetingCollection.find().toFuture()
  }
}
