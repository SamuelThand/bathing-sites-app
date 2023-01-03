package se.miun.sath2102.dt031g.bathingsites

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface BathingSiteDao {
    @Query("SELECT * FROM bathingsite")
    fun getAll(): List<BathingSite>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertAll(vararg bathingSites: BathingSite)
}