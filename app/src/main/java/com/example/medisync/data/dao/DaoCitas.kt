package com.example.medisync.data.dao

import androidx.room.* // importamos todo de Room
import com.example.medisync.data.entity.BddCitas
import kotlinx.coroutines.flow.Flow

@Dao
interface DaoCitas {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(citas: BddCitas)

    @Update
    suspend fun update(citas: BddCitas)

    @Delete
    suspend fun delete(citas: BddCitas)

    @Query("SELECT * FROM citas")
    fun getAll(): Flow<List<BddCitas>>

    @Query("SELECT * FROM citas WHERE id = :id")
    fun getById(id: String): Flow<BddCitas?>

}