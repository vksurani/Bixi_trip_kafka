package com.ca.mcit.sprint3

import io.confluent.kafka.schemaregistry.client.CachedSchemaRegistryClient
import org.apache.kafka.clients.consumer.{ConsumerConfig, ConsumerRecord}
import org.apache.kafka.common.serialization.{IntegerDeserializer, StringDeserializer}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.streaming.dstream.DStream
import org.apache.spark.streaming.kafka010.{ConsumerStrategies, KafkaUtils, LocationStrategies}
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.functions._

object SparkEnrichment {

  def main(args: Array[String]): Unit = {
    processKafkaStream
  }
  val srClient = new CachedSchemaRegistryClient("http://172.16.129.87:8081",1)
  val metadata = srClient.getSchemaMetadata("enriched_trip-value",1)
  val stationSchema = srClient.getByID(metadata.getId)

  def getStationDF: DataFrame = {
    val spark = SparkSession.builder()
      .appName("Spark")
      .config("spark.master", "local")
      .getOrCreate()

    val enrichedStationDf: DataFrame = spark.read.option("header", "true").option("inferschema", "true")
       .csv("hdfs://quickstart.cloudera:8020/user/winter2020/vasu/finalproject/Enriched/000000_0")

    enrichedStationDf.createOrReplaceTempView("enrichedStation")

    val stationDf: DataFrame = spark.sql(
        """SELECT *
          |FROM enrichedStation""".stripMargin)

    stationDf.select(
      col("system_id"),
      col("timezone"),
      col("station_id"),
      col("name"),
      col("short_name").as("shortName"),
      col("lat"),
      col("lot"),
      col("capacity")
    )
  }

  def processKafkaStream: Unit = {
    val conf = new SparkConf().setMaster("local[*]")
      .setAppName("Spark streaming with Kafka")
    val sc = new SparkContext(conf)
    val ssc = new StreamingContext(sc, Seconds(10))

    val kafkaConfig = Map[String, String](
      ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG -> "quickstart.cloudera:9092",
      ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG -> classOf[IntegerDeserializer].getName,
      ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG -> classOf[StringDeserializer].getName,
      ConsumerConfig.GROUP_ID_CONFIG -> String.valueOf(System.currentTimeMillis()),
      ConsumerConfig.AUTO_OFFSET_RESET_CONFIG -> "earliest",
      ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG -> "false"
    )

    val topic = "winter2020_vasu_trip"

    val inStream: DStream[ConsumerRecord[String, String]] = KafkaUtils.createDirectStream[String, String](
      ssc,
      LocationStrategies.PreferConsistent,
      ConsumerStrategies.Subscribe[String, String](List(topic), kafkaConfig)
    )

    inStream.map(_.value()).foreachRDD(microBatchRdd => businessLogic(microBatchRdd))

    ssc.start()
    ssc.awaitTermination()
  }

  def businessLogic(rdd: RDD[String]): Unit = {
    val spark = SparkSession.builder().getOrCreate()
    import spark.implicits._

    val record = rdd.map(_.split(","))
      .map(x => (x(0), x(1), x(2), x(3), x(4), x(5)))

    val tripsDf = record.toDF("startDate",
      "startStationCode",
      "endDate",
      "endStationCode",
      "durationSec",
      "isMember")

    val stationDF = getStationDF

    val enrichedTripJoinCondition = tripsDf.col("startStationCode") ===
      stationDF.col("shortName")

    val enrichedTrip = tripsDf
      .join(stationDF, enrichedTripJoinCondition, "left")

    if (enrichedTrip.count() > 0) {
      val path = "/Users/vasusurani/IdeaProjects/bixi_data_kafka/output/"
      enrichedTrip
        .coalesce(1)
        .write
        .mode("append")
        .format("csv")
        .option("header", "false")
        .save(path)

      enrichedTrip.show()
    }
  }
}

