package com.example.medisync.data.dao

import androidx.room.* // importamos todo de Room
import com.example.medisync.data.entity.BddMedicamentos
import kotlinx.coroutines.flow.Flow

@Dao
interface DaoMedicamentos {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(medicamento: BddMedicamentos)

    @Update
    suspend fun update(medicamento: BddMedicamentos)

    @Delete
    suspend fun delete(medicamento: BddMedicamentos)

    @Query("SELECT * FROM medicamentos")
    fun getAll(): Flow<List<BddMedicamentos>>

    @Query("SELECT * FROM medicamentos WHERE id = :id")
    fun getById(id: String): Flow<BddMedicamentos?>

}