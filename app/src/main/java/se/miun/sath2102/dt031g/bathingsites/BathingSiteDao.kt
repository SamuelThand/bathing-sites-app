package se.miun.sath2102.dt031g.bathingsites

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface BathingSiteDao {
    @Query("SELECT * FROM bathingsite")
    fun getAll(): List<BathingSite>

//    @Query("SELECT * FROM bathingsite WHERE id IN (:userIds)")
//    fun loadAllByIds(userIds: IntArray): List<User>
//
//    @Query("SELECT * FROM user WHERE first_name LIKE :first AND " +
//            "last_name LIKE :last LIMIT 1")
//    fun findByName(first: String, last: String): User

    @Insert
    fun insertAll(vararg bathingSites: BathingSite)

    @Delete
    fun delete(bathingSite: BathingSite)

}