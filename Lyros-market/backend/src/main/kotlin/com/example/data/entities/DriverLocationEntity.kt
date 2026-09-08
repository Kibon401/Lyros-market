package com.example.data.entities

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.CurrentDateTime
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

object DriverLocationsTable : IntIdTable("driver_location") {
    val driverId = reference("driver_id", UsersTable)
    val latitude = double("latitude")
    val longitude = double("longitude")
    val timestamp = datetime("timestamp").defaultExpression(CurrentDateTime)
}
