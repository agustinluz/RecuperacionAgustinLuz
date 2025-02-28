package com.recuperacion.agustin.room
import androidx.room.TypeConverter
import com.recuperacion.agustin.modelo.TipoComponente
import java.util.Date
class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }
    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
    @TypeConverter
    fun fromTipoComponente(value: TipoComponente): String {
        return value.name
    }
    @TypeConverter
    fun toTipoComponente(value: String): TipoComponente {
        return TipoComponente.valueOf(value)
    }
}