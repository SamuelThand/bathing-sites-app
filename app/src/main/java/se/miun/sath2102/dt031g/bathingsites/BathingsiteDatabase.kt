package se.miun.sath2102.dt031g.bathingsites

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [BathingSite::class], version = 1)
abstract class BathingsiteDatabase: RoomDatabase() {
    abstract fun BathingSiteDao(): BathingSiteDao
}