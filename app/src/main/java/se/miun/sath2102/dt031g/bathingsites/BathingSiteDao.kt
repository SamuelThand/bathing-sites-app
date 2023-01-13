package se.miun.sath2102.dt031g.bathingsites

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/**
 * Provides the interface for reading and writing BathingSites to and from the Room database.
 */
@Dao
interface BathingSiteDao {


    /**
     * Get all stored BathingSites
     *
     * @return List of all stored BathingSite objects
     */
    @Query("SELECT * FROM bathingsite")
    fun getAll(): List<BathingSite>


    /**
     * Insert one or multiple BathingSites to the database. If a BathingSite
     * already exists, the transaction will be aborted.
     *
     * @param bathingSite
     */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertAll(vararg bathingSite: BathingSite)


    /**
     * Insert a list of BathingSites to the database. If a BathingSite
     * already exists, it will not be inserted and the transaction will continue.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertList(bathingSites: List<BathingSite>)


}
