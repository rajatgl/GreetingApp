package com.bridgelabz.akka.database

/**
 * Created on 12/26/2020.
 * Class: MongoUtils.scala
 * Author: Rajat G.L.
 */
import com.bridgelabz.akka.models.User
import org.mongodb.scala.model.Filters.equal

import scala.concurrent.Future

object MongoUtils {

  // method to fetch entire data from mongodb database
  def getAllUsers: Future[Seq[User]] = {
    (new MongoConstants).greetingCollection.find().toFuture()
  }

  def getAllUsers(userName: String):Future[Seq[User]] = {
    (new MongoConstants).greetingCollection.find(equal("name", userName)).toFuture()
  }
}
