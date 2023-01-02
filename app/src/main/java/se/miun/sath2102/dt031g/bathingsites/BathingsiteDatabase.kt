package se.miun.sath2102.dt031g.bathingsites

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

//TODO bygg singleton för att instantiera databasen

@Database(entities = [BathingSite::class], version = 1)
abstract class BathingsiteDatabase: RoomDatabase() {

    companion object {
        private var INSTANCE: BathingsiteDatabase? = null

        fun getInstance(context: Context): BathingsiteDatabase {
            INSTANCE = INSTANCE ?: Room.databaseBuilder(context.applicationContext,
                BathingsiteDatabase::class.java, "Bathingsites")
                .build()

            return INSTANCE as BathingsiteDatabase
        }

        fun destroy() {
            INSTANCE = null
        }
    }

    abstract fun BathingSiteDao(): BathingSiteDao
}