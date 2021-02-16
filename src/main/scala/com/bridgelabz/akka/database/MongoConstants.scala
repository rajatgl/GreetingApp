package com.bridgelabz.akka.database

import com.bridgelabz.akka.constants.Constants
import com.bridgelabz.akka.models.User
import org.bson.codecs.configuration.{CodecProvider, CodecRegistries, CodecRegistry}
import org.mongodb.scala.MongoClient.DEFAULT_CODEC_REGISTRY
import org.mongodb.scala.{Document, MongoClient, MongoCollection, MongoDatabase}
import org.mongodb.scala.bson.codecs.Macros

class MongoConstants {

  private val greetingCodecProvider: CodecProvider = Macros.createCodecProvider[User]()

  //Maintains set of Codec instances
  private val codecRegistry: CodecRegistry = CodecRegistries.fromRegistries(
    CodecRegistries.fromProviders(greetingCodecProvider),
    DEFAULT_CODEC_REGISTRY
  )

  //creates new mongo instance
  private val mongoClient: MongoClient = MongoClient()
  private val mongoDatabase: MongoDatabase =
    mongoClient
      .getDatabase(Constants.databaseName)
      .withCodecRegistry(codecRegistry)

  private val database: MongoDatabase = mongoClient.getDatabase(Constants.databaseName)

  val greetingCollection: MongoCollection[User] =
    mongoDatabase.getCollection[User](Constants.collectionName)

  val collection: MongoCollection[Document] = database.getCollection(Constants.collectionName)
}
