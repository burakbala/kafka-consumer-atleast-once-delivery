package com.scaleforj.burak.kafka

import java.util.concurrent.TimeUnit

import akka.actor.ActorSystem
import akka.kafka.ConsumerMessage.CommittableMessage
import akka.kafka.scaladsl.Consumer
import akka.kafka.scaladsl.Consumer.Control
import akka.kafka.{ConsumerSettings, Subscriptions}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}
import org.apache.kafka.common.serialization.StringDeserializer

import scala.collection.immutable.Seq
import scala.concurrent.Await
import scala.concurrent.duration.Duration

object ReactiveKafkaConsumerLosingMessages extends App {
  implicit val actorSystem = ActorSystem("kafka-producer")
  implicit val materializer = ActorMaterializer()

  val consumerSettings = ConsumerSettings(actorSystem, new StringDeserializer, new StringDeserializer)
    .withBootstrapServers("localhost:9092")
    .withGroupId("reactive-kafka-consumer-group")
    .withProperty("max.poll.records", "2") //it looks like this has no effect
    .withProperty("enable.auto.commit", "false")
    .withClientId("reactive-kafka-client")

  val source: Source[CommittableMessage[String, String], Control] = Consumer.committableSource(consumerSettings, Subscriptions.topics("test1"))
  source
    .log("Processing started")
    .grouped(2)
    .map { messages =>
      processMessages(messages)
    }
    .log("Processing completed")
    .runWith(Sink.ignore)

  var count = 0
  def processMessages(messages: Seq[CommittableMessage[String, String]]): Unit = {
    if(count % 2 == 1) {
      messages.foreach { message =>
        println( s"Record ${message.value} processed")
        Await.ready(message.committableOffset.commitScaladsl(), Duration(1, TimeUnit.SECONDS))
      }
    } else {
      messages.foreach(message => println( s"Record ${message.value} failed"))
    }
    count += 1
  }
}
