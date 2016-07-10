package com.scaleforj.burak.kafka

import akka.actor.ActorSystem
import akka.kafka.ProducerSettings
import akka.stream.ActorMaterializer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer

object MessageProducer extends App {
  implicit val actorSystem = ActorSystem("kafka-producer")
  implicit val materialiser = ActorMaterializer

  val producerSettings = ProducerSettings(actorSystem, new StringSerializer, new StringSerializer)
    .withBootstrapServers("localhost:9092")

  val kafkaProducer = producerSettings.createKafkaProducer()

  val topic = "test1"

  (1 to 10).foreach { i =>
    val record = new ProducerRecord[String, String](topic, s"experiment 16 $i")
    kafkaProducer.send(record)
  }

  actorSystem.terminate()

}
