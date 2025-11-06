package ubb.codeandcoffee.proyectoSemestral.controladores;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import ubb.codeandcoffee.proyectoSemestral.modelo.SujetoEstudio;
import ubb.codeandcoffee.proyectoSemestral.repositorios.SujetoEstudioRepository;

/**
 * Controlador para manejar el ingreso y validación de Sujetos de Estudio.
 *
 * NOTA IMPORTANTE: Para que este controlador funcione, es necesario que:
 * 1. El campo 'id_sujeto' en el formulario HTML haya sido eliminado.
 * 2. La base de datos se encargue de generar el 'id_sujeto' (AUTO_INCREMENT/Trigger).
 * 3. El SujetoEstudioRepository tenga definido un método: 'findByNombre(String nombre);'
 */
@Controller
public class SujetoController {

    @Autowired
    private SujetoEstudioRepository sujetoEstudioRepository;

    /**
     * Muestra la página de "sala de espera" (ingreso_sujeto.html).
     */
    @GetMapping("/ingreso")
    public String mostrarFormularioIngreso() {
        return "form/ingreso_sujeto";
    }

    /**
     * Maneja el envío del formulario de ingreso.
     * Ya no recibe id_sujeto; la base de datos lo generará.
     */
    @PostMapping("/ingreso")
    public String procesarIngreso(
            // --- Se elimina @RequestParam String id_sujeto, ya que la base de datos lo genera ---
            @RequestParam String nombre,
            @RequestParam String tipo,

            // --- Campos Opcionales (required = false) ---
            @RequestParam(required = false) String direccion,
            @RequestParam(required = false) String ocupacion,
            @RequestParam(required = false) String telefono,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String nacionalidad,

            HttpSession session,
            RedirectAttributes redirectAttributes) {

        // 1. VALIDACIÓN POR NOMBRE (Tu tarea)
        // Buscamos si ya existe un sujeto con ese nombre
        SujetoEstudio sujetoExistente = sujetoEstudioRepository.findByNombre(nombre);

        if (sujetoExistente != null) {
            // Si el nombre ya existe, redirigimos con un mensaje de error
            redirectAttributes.addFlashAttribute("error_nombre",
                    "Error: El nombre '" + nombre + "' ya se encuentra registrado. Por favor, ingrese un nombre diferente.");
            return "redirect:/ingreso";
        }
        
        // ** Aquí iría la validación de 'email' y otros campos si fuera necesario **
        // if (emailEsInvalido(email)) { ... }

        // --- 1. CONVERSIÓN DE TIPO (Caso/Control -> CA/CO) ---
        String tipoParaBD = "";
        String tipoRecibido = tipo != null ? tipo.trim() : "";

        switch (tipoRecibido.toUpperCase()) {
            case "CASO":
                tipoParaBD = "CA";
                break;
            case "CONTROL":
                tipoParaBD = "CO";
                break;
            default:
                // Si el valor no es 'Caso' ni 'Control', es un error de formulario.
                redirectAttributes.addFlashAttribute("error_tipo", 
                    "Error: El valor de Tipo seleccionado no es válido.");
                return "redirect:/ingreso";
        }
        
        try {
            // 2. CREACIÓN DEL NUEVO OBJETO
            SujetoEstudio nuevoSujeto = new SujetoEstudio();

            // Solo seteamos el nombre y el tipo, la ID se queda NULA para que la BD la genere
            nuevoSujeto.setNombre(nombre);
            nuevoSujeto.setTipo(tipoParaBD);

            // Campos Opcionales
            nuevoSujeto.setDireccion(direccion);
            nuevoSujeto.setOcupacion(ocupacion);
            nuevoSujeto.setTelefono(telefono);
            nuevoSujeto.setEmail(email);
            nuevoSujeto.setNacionalidad(nacionalidad);

            // 3. GUARDAMOS EN LA BASE DE DATOS
            // El 'save' insertará el registro, y la BD asignará el id_sujeto automáticamente.
            sujetoEstudioRepository.save(nuevoSujeto);

            // 4. Si todo sale bien, guardamos el TIPO en la sesión
            session.setAttribute("tipo_sujeto", tipo); // Guardará "CASO" o "CONTROL"

            // 5. Redirigimos al formulario principal
            return "redirect:/formulario";

        } catch (DataIntegrityViolationException e) {
            // Capturamos cualquier otra violación de integridad que pueda ocurrir
            redirectAttributes.addFlashAttribute("error_nombre",
                    "Error inesperado al guardar el sujeto. Por favor, revise las restricciones de la base de datos.");
            return "redirect:/ingreso";
        }
    }
}