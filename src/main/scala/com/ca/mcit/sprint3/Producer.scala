package com.ca.mcit.sprint3

import java.util.Properties
import org.apache.hadoop.fs.{FSDataInputStream, Path}
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerRecord}
import org.apache.kafka.common.serialization.{IntegerSerializer, StringSerializer}

object Producer extends App with Main {
  def Prdcr(args: Array[String]): Unit = {
    val topicName = "winter2020_vasu_trip"

    val producerProperties = new Properties()
    producerProperties.setProperty(
      ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092"
    )
    producerProperties.setProperty(
      ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, classOf[IntegerSerializer].getName
    )
    producerProperties.setProperty(
      ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, classOf[StringSerializer].getName
    )

    val producer = new KafkaProducer[Int, String](producerProperties)

    val inputStream: FSDataInputStream = fs
      .open(new Path("/Users/vasusurani/Downloads/Final_Project_Big_Data/sprint3/100_trips.csv"))

    var i = 1
    val data = scala.io.Source.fromInputStream(inputStream).getLines()
      .drop(1).mkString("\n")
      .split("\n")
      .foreach(line => {
        println(line)
        producer.send(new ProducerRecord[Int, String](topicName, i, line))
        i = i + 1
      })
    producer.flush()
  }
}
