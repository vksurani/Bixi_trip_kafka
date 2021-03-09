package com.ca.mcit.sprint3

case class Enrichedtrip(
                start_date: String,
                start_station_code: Int,
                end_date: String,
                end_station_code: Int,
                duration_sec: Int,
                is_member: Int,
                system_id: String,
                timezone: String,
                station_id: Int,
                name: String,
                short_name: String,
                lat: Double,
                lon: Double,
                capacity: Int)
