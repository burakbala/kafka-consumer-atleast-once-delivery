package com.scaleforj.burak.kafka

import java.util
import java.util.Properties

import org.apache.kafka.clients.consumer.{ConsumerRecords, KafkaConsumer}
import collection.JavaConversions._

object KafkaConsumerNotLosingMessages extends App {
  val props = new Properties()
  props.put("bootstrap.servers", "localhost:9092")
  props.put("group.id", "kafka-consumer-not-losing-messages-group")
  props.put("client.id", "kafka-consumer-not-losing-messages-client")
  props.put("enable.auto.commit", "false")
  props.put("session.timeout.ms", "30000")
  props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
  props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
  props.put("max.poll.records", "2")
  val consumer = new KafkaConsumer[String, String](props)
  consumer.subscribe(util.Arrays.asList("test1"))

  while (true) {
    val pollStart = System.currentTimeMillis()
    val records = consumer.poll(1000)
    val pollEnd = System.currentTimeMillis()
    println(s"Polled ${records.size} records in ${pollEnd - pollStart} ms.")
    if(records.nonEmpty) {
      try {
        processRecords(records)
        consumer.commitSync()
      } catch {
        case e: Throwable => {
          println("Processing failed")
          val partitions = records.partitions()
          partitions.foreach { topicPartition =>
            val partitionOffset = records.records(topicPartition).head.offset()
            println(s"Seeking to Partition: ${topicPartition.partition()} Offset $partitionOffset")
            consumer.seek(topicPartition, partitionOffset)
          }
        }
      }
    }
  }

  var count = 0
  def processRecords(records: ConsumerRecords[String, String]): Unit = {

    count += 1
    if(count % 2 == 1) {
      records.records("test1").to[List].foreach { record =>
        println(s"Record: ${record.value()} Partition: ${record.partition()} Offset: ${record.offset()} failed")
      }
      throw new RuntimeException("process failed")
    } else {
      records.records("test1").to[List].foreach { record =>
        println(s"Record: ${record.value()} Partition: ${record.partition()} Offset: ${record.offset()} processed")
      }
    }
  }
}
