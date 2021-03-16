package com.ca.mcit.sprint3

import io.confluent.kafka.schemaregistry.client.CachedSchemaRegistryClient
import org.apache.avro.Schema
import scala.io.Source

object RegisterSchema extends App {

  val Trip = Source.fromString(
    """{
      |"type":"record",
      |"name":"EnrichedTrip",
      |"namespace":"com.ca.mcit.sprint3",
      |"fields":[
      |{"name":"start_date","type":"string"},
      |{"name":"start_station_code","type":"int"},
      |{"name":"end_date","type":"string"},
      |{"name":"end_station_code","type":"int"},
      |{"name":"duration_sec","type":"int"},
      |{"name":"is_member","type":"int"},
      |{"name":"system_id","type":"string"},
      |{"name":"timezone","type":"string"},
      |{"name":"station_id","type":"int"},
      |{"name":"name","type":"string"},
      |{"name":"short_name","type":"string"},
      |{"name":"lat","type":"double"},
      |{"name":"lon","type":"double"},
      |{"name":"capacity","type":"int"}
      |]}""".stripMargin
  ).mkString

  // 2. Parse schema
  val avroParser = new Schema.Parser().parse(Trip)

  // 3. Create a client
  val srClient = new CachedSchemaRegistryClient("http://172.16.129.87:8081", 1)
  srClient.register("enriched_trip-vasu", avroParser)

}
