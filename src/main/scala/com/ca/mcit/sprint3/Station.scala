package com.ca.mcit.sprint3

case class Station(
                    station_id: Int,
                    name: String,
                    short_name: String,
                    lat: Double,
                    lon: Double,
                    capacity: Int
                  )

object Station {
  def apply(csvLine: String): Station = {
    val s = csvLine.split(",", -1)
    new Station(s(0).toInt, s(1), s(2), s(3).toDouble, s(4).toDouble, s(5).toInt)
  }

  def toCsv(station: Station): String = {
      station.station_id + "," +
      station.name + "," +
      station.short_name + "," +
      station.lat + "," +
      station.lon + "," +
      station.capacity
  }
}
