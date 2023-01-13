package se.miun.sath2102.dt031g.bathingsites

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Singleton implementation of a Room database.
 */
@Database(entities = [BathingSite::class], version = 1)
abstract class BathingsiteDatabase: RoomDatabase() {

    companion object {
        private var INSTANCE: BathingsiteDatabase? = null


        /**
         * Returns the singleton instance of the BathingsiteDatabase, or instantiates it
         * if it does not exist.
         *
         * @param context The application context
         * @return The BathingsiteDatabace instance
         */
        fun getInstance(context: Context): BathingsiteDatabase {
            INSTANCE = INSTANCE ?: Room.databaseBuilder(context.applicationContext,
                BathingsiteDatabase::class.java, "Bathingsites")
                .build()

            return INSTANCE as BathingsiteDatabase
        }


        /**
         * Destroy the database instance.
         */
        fun destroy() {
            INSTANCE = null
        }


    }

    /**
     * Get access to the BathingSiteDao data access object.
     *
     * @return The BathingSiteDatabase DAO
     */
    abstract fun BathingSiteDao(): BathingSiteDao


}
