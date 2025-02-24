package com.recuperacion.agustin.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.recuperacion.agustin.modelo.Ingrediente
import com.recuperacion.agustin.modelo.ComponenteDieta

@Database(entities = [ComponenteDieta::class, Ingrediente::class], version = 4, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun componenteDietaDao(): ComponenteDietaDao
    abstract fun ingredienteDao(): IngredienteDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
