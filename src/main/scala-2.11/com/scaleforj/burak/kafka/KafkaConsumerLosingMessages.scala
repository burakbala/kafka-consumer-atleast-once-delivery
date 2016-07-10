package com.scaleforj.burak.kafka

import java.util
import java.util.Properties

import org.apache.kafka.clients.consumer.{ConsumerRecords, KafkaConsumer}
import collection.JavaConversions._

object KafkaConsumerLosingMessages extends App {
  val props = new Properties()
  props.put("bootstrap.servers", "localhost:9092")
  props.put("group.id", "kafka-consumer-losing-messages-group")
  props.put("client.id", "kafka-consumer-losing-messages-client")
  props.put("enable.auto.commit", "false")
  props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
  props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
  props.put("max.poll.records", "2")
  val consumer = new KafkaConsumer[String, String](props)
  consumer.subscribe(util.Arrays.asList("test1"))


  while (true) {

    val consumerRecords = consumer.poll(1000)
    if(consumerRecords.nonEmpty) {
      try {
        processRecords(consumerRecords)
        consumer.commitSync()
      } catch {
        case e: Throwable => {
        }
      }
    }
  }

  var count = 0
  def processRecords(records: ConsumerRecords[String, String]): Unit = {
    count += 1
    if(count % 2 == 1) {
      records.records("test1").to[List].foreach { record =>
        println(s"Record: ${record.value()} failed")
      }
      throw new RuntimeException("process failed")
    } else {
      records.records("test1").to[List].foreach { record =>
        println(s"Record: ${record.value()} processed")
      }
    }
  }
}
