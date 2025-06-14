import kotlinx.serialization.Serializable

@Serializable
data class RegistroPacienteDTO(
    val dni: String,
    val nombre: String,
    val apellido: String,
    val email: String,
    val telefono: String,
    val direccion: String,
    val idRol: Long = 4,            // Suponiendo que 4 es paciente fijo
    val idEspecialidad: Long? = null, // null porque paciente no tiene especialidad
    val idEstadoPersona: Long = 1,  // Activo, o puedes parametrizar
    val fechaNacimiento: String,
    val obraSocial: String,
    val idEstadoPaciente: Long = 1  // Activo, o parametrizable
)
