name := "bixi_data_kafka"

version := "0.1"

scalaVersion := "2.12.8"

val hadoopVersion = "2.7.7"

val sparkVersion = "2.4.4"

val ConfluentVersion = "5.3.0"

libraryDependencies += "org.apache.hive" % "hive-jdbc" % "1.1.0-cdh5.16.2"
libraryDependencies += "org.apache.kafka" % "kafka-clients" % "2.5.0"
libraryDependencies += "org.apache.hadoop" % "hadoop-common" % hadoopVersion
libraryDependencies += "org.apache.hadoop" % "hadoop-hdfs" % hadoopVersion
libraryDependencies += "au.com.bytecode" % "opencsv" % "2.4"
libraryDependencies += "org.apache.spark" %% "spark-core" % sparkVersion
libraryDependencies += "org.apache.spark" %% "spark-sql" % sparkVersion
libraryDependencies += "org.apache.spark" %% "spark-streaming" % sparkVersion
libraryDependencies += "org.apache.spark" %% "spark-streaming-kafka-0-10" % sparkVersion
libraryDependencies += "io.confluent" % "kafka-schema-registry-client" % ConfluentVersion
libraryDependencies += "io.confluent" % "kafka-avro-serializer" % ConfluentVersion

resolvers += "Cloudera" at "https://repository.cloudera.com/artifactory/cloudera-repos/"
resolvers += "Confluent" at "https://packages.confluent.io/maven/"

dependencyOverrides ++= {
  Seq(
    "com.fasterxml.jackson.module" % "jackson-module-scala_2.11" % "2.6.7.1",
    "com.fasterxml.jackson.core" % "jackson-databind" % "2.6.7.1",
  )
}