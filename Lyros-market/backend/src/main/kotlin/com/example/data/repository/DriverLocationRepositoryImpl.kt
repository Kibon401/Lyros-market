package com.example.data.repository

import com.example.data.database.DatabaseFactory.dbQuery
import com.example.data.entities.DriverLocationsTable
import com.example.domain.models.DriverLocation
import com.example.domain.repositories.DriverLocationRepository
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class DriverLocationRepositoryImpl : DriverLocationRepository {
    override suspend fun saveDriverLocation(driverId: Int, latitude: Double, longitude: Double): DriverLocation? = dbQuery {
        // For simplicity, we'll just insert a new record. 
        // In a high-frequency update scenario, you might want to update the last record for a driver.
        val insertStatement = DriverLocationsTable.insert {
            it[DriverLocationsTable.driverId] = driverId
            it[DriverLocationsTable.latitude] = latitude
            it[DriverLocationsTable.longitude] = longitude
            it[DriverLocationsTable.timestamp] = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        }
        insertStatement.resultedValues?.singleOrNull()?.let { rowToDriverLocation(it) }
    }

    override suspend fun getLastKnownDriverLocation(driverId: Int): DriverLocation? = dbQuery {
        DriverLocationsTable
            .selectAll()
            .where { DriverLocationsTable.driverId eq driverId }
            .orderBy(DriverLocationsTable.timestamp, SortOrder.DESC)
            .limit(1)
            .map { rowToDriverLocation(it) }
            .singleOrNull()
    }

    private fun rowToDriverLocation(row: ResultRow) = DriverLocation(
        id = row[DriverLocationsTable.id].value,
        driverId = row[DriverLocationsTable.driverId].value,
        latitude = row[DriverLocationsTable.latitude],
        longitude = row[DriverLocationsTable.longitude],
        timestamp = row[DriverLocationsTable.timestamp]
    )
}
