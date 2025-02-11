import androidx.room.Entity
import androidx.room.PrimaryKey
import com.recuperacion.agustin.modelo.TipoComponente
import java.io.Serializable

@Entity(tableName = "componentes_dieta")
data class ComponenteDieta(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    var nombre: String = "",
    var tipo: TipoComponente = TipoComponente.SIMPLE,
    var grHC_ini: Double = 0.0,
    var grLip_ini: Double = 0.0,
    var grPro_ini: Double = 0.0,
    var Kcal_ini: Double = 0.0
) : Serializable

