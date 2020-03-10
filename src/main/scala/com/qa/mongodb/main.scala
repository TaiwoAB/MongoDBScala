package com.qa.mongodb
import org.mongodb.scala._
import com.qa.mongodb.Helpers._
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.model.Updates._
object main extends App {
  val mongoClient: MongoClient = MongoClient("mongodb://localhost")

  val database: MongoDatabase = mongoClient.getDatabase("mydb")
  val collection: MongoCollection[Document] = database.getCollection("test")
  val doc: Document = Document( "name" -> "MongoDB", "type" -> "database",
    "count" -> 1, "info" -> Document("x" -> 203, "y" -> 102))
  collection.insertOne(doc).results()
  val observable: Observable[Completed] = collection.insertOne(doc)
  val documents = (1 to 100) map { i: Int => Document("i" -> i) }
  val insertObservable = collection.insertMany(documents)
  val insertAndCount = for {
    insertResult <- insertObservable
    countResult <- collection.countDocuments()
  } yield countResult

  println(s"total # of documents after inserting 100 small ones (should be 101):  ${insertAndCount.headResult()}")
  observable.subscribe(new Observer[Completed] {

    override def onNext(result: Completed): Unit = println("Inserted")

  override def onError(e: Throwable): Unit = println("Failed")

    override def onComplete(): Unit = println("Completed")
    // collection.deleteOne(equal("i", 5)).printHeadResult("Delete Result: ")
    // collection.deleteMany(gte("name", "MongoDB")).printHeadResult("Delete Result: ")
    collection.find().first().printHeadResult()
    collection.find().printResults()
    collection.updateOne(equal("name", "MongoDB"), set("name", "sql")).printHeadResult("Update Result: ")
    //
    //collection.drop().results()
   // mongoClient.close()
  })
}
