package com.example.medisync.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.medisync.data.entity.BddDosis
import kotlinx.coroutines.flow.Flow

@Dao
interface DaoDosis {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(dosis: BddDosis)

    @Update
    suspend fun update(dosis: BddDosis)

    @Delete
    suspend fun delete(dosis: BddDosis)

    @Query("SELECT * FROM dosis")
    fun getAll(): Flow<List<BddDosis>>

    @Query("SELECT * FROM dosis WHERE id = :id")
    fun getById(id: String): Flow<BddDosis?>

}
