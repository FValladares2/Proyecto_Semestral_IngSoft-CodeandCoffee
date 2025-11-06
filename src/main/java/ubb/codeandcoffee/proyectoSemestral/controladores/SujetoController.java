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
     */
    @PostMapping("/ingreso")
    public String procesarIngreso(
            // --- Campos Obligatorios ---
            @RequestParam String id_sujeto,
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

        try {
            // 1. Verificamos si el nombre ya existe (tu modelo ya tiene un UniqueConstraint)
            // No necesitamos buscar, intentaremos guardar directamente.

            // 2. Creamos el nuevo objeto SujetoEstudio
            SujetoEstudio nuevoSujeto = new SujetoEstudio();
            
            // Obligatorios
            nuevoSujeto.setId_sujeto(id_sujeto);
            nuevoSujeto.setNombre(nombre);
            nuevoSujeto.setTipo(tipo);
            
            // Opcionales (Spring los pasará como null si están vacíos)
            nuevoSujeto.setDireccion(direccion);
            nuevoSujeto.setOcupacion(ocupacion);
            nuevoSujeto.setTelefono(telefono);
            nuevoSujeto.setEmail(email);
            nuevoSujeto.setNacionalidad(nacionalidad);

            // 3. Guardamos en la Base de Datos
            sujetoEstudioRepository.save(nuevoSujeto);

            // 4. Si todo sale bien, guardamos el TIPO en la sesión
            session.setAttribute("tipo_sujeto", tipo); // Guardará "CASO" o "CONTROL"

            // 5. Redirigimos al formulario principal
            return "redirect:/formulario";

        } catch (DataIntegrityViolationException e) {
            // 6. Si falla, es porque se violó una restricción (ej. el 'nombre' ya existía)
            // (O el id_sujeto + tipo ya existían)
            redirectAttributes.addFlashAttribute("error_nombre", 
                "Error: El nombre '" + nombre + "' o el ID '" + id_sujeto + "' ya existen en la base de datos.");
            return "redirect:/ingreso";
        }
    }
}
