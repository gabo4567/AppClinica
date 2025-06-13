import kotlinx.serialization.Serializable
import kotlinx.serialization.Contextual
import kotlinx.datetime.LocalDate

@Serializable
data class ProfesionalDTO(
    val id: Long,
    val idPersona: Long,
    val dni: String,
    val nombre: String,
    val apellido: String,
    val email: String,
    val telefono: String,
    val direccion: String,
    @Contextual
    val fechaNacimiento: LocalDate,
    val idRol: Long,
    val idEspecialidad: Long? = null,
    val matriculaProfesional: String,
    val idEstado: Long
)
