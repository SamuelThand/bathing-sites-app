package se.miun.sath2102.dt031g.bathingsites

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(indices = [Index(value = ["latitude", "longitude"],
    unique = true)])
data class BathingSite(
    @PrimaryKey(autoGenerate = true) val id: Int?,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "description") val description: String?,
    @ColumnInfo(name = "address") val address: String?,
    @ColumnInfo(name = "latitude") val latitude: Double?,
    @ColumnInfo(name = "longitude") val longitude: Double?,
    @ColumnInfo(name = "water_temp") val waterTemp: Double?,
    @ColumnInfo(name = "water_temp_date") val waterTempDate: String?,
    @ColumnInfo(name = "grade") val grade: Float
)
