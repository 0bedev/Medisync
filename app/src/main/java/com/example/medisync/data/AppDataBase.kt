package com.example.medisync.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
// Importamos los DAOs
import com.example.medisync.data.dao.DaoDosis
import com.example.medisync.data.dao.DaoMedicamentos
import com.example.medisync.data.dao.DaoCitas
// Importamos las entidades
import com.example.medisync.data.entity.BddCitas
import com.example.medisync.data.entity.BddMedicamentos
import com.example.medisync.data.entity.BddDosis

@Database(
    entities = [
        BddCitas::class,
        BddMedicamentos::class,
        BddDosis::class
    ],
    version = 5, // Incrementar el número de versión cuando se realicen cambios en la BBDD
    exportSchema = false
)

abstract class AppDataBase : RoomDatabase() {

    // DAOs
    abstract fun daoCitas(): DaoCitas
    abstract fun daoMedicamentos(): DaoMedicamentos
    abstract fun daoDosis(): DaoDosis

    companion object {

        @Volatile
        private var INSTANCE: AppDataBase? = null

        fun getDatabase(context: Context): AppDataBase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDataBase::class.java,
                    "medisync_database"
                )
                .fallbackToDestructiveMigration() // Permite recrear la BBDD si cambia el esquema
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
