package com.recuperacion.agustin.room
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.recuperacion.agustin.modelo.ComponenteDieta
import com.recuperacion.agustin.modelo.Ingrediente
@Database(
    entities = [ComponenteDieta::class, Ingrediente::class],
    version = 8,
    exportSchema = false
)
@TypeConverters(Converters::class)
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
                    "dietas_database_v8"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}