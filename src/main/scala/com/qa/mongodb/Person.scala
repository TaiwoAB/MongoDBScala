package com.qa.mongodb
import org.mongodb.scala._
import com.qa.mongodb.Helpers._
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.model.Sorts._
import org.mongodb.scala.model.Updates._
import org.mongodb.scala.bson.codecs.Macros._
import org.mongodb.scala.bson.codecs.DEFAULT_CODEC_REGISTRY
import org.bson.codecs.configuration.CodecRegistries.{fromRegistries, fromProviders}
import org.mongodb.scala.bson.ObjectId

object Person {
  def apply(firstName: String, lastName: String): Person =
    Person(new ObjectId(), firstName, lastName)
}
case class Person(_id: ObjectId, firstName: String, lastName: String)
object classMain extends App{
  val codecRegistry = fromRegistries(fromProviders(classOf[Person]), DEFAULT_CODEC_REGISTRY )
  val mongoClient: MongoClient = MongoClient("mongodb://localhost")
  val database: MongoDatabase = mongoClient.getDatabase("mydatabase").withCodecRegistry(codecRegistry)
  val collection: MongoCollection[Person] = database.getCollection("test");
  val person: Person = Person("Ada", "Lovelace")
  collection.insertOne(person).results()
  val people: Seq[Person] = Seq(
    Person("Charles", "Babbage"),
    Person("George", "Boole"),
    Person("Gertrude", "Blanch"),
    Person("Grace", "Hopper"),
    Person("Ida", "Rhodes"),
    Person("Jean", "Bartik"),
    Person("John", "Backus"),
    Person("Lucy", "Sanders"),
    Person("Tim", "Berners Lee"),
    Person("Zaphod", "Beeblebrox")
  )
  collection.insertMany(people).printResults()
  collection.find().first().printHeadResult()
  collection.find().printResults()

  collection.find(equal("firstName", "Ida")).first().printHeadResult()
  collection.find(regex("firstName", "^G")).sort(ascending("lastName")).printResults()
  collection.updateOne(equal("lastName", "Berners Lee"), set("lastName", "Berners-Lee")).printHeadResult("Update Result: ")
  collection.deleteOne(equal("firstName", "Zaphod")).printHeadResult("Delete Result: ")

}