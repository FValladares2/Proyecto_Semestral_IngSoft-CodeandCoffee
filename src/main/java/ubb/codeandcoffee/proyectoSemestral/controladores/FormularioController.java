package ubb.codeandcoffee.proyectoSemestral.controladores;

import jakarta.servlet.http.HttpSession; // <-- 1. Importa HttpSession
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.ui.Model;
import ubb.codeandcoffee.proyectoSemestral.servicios.SeccionService;

@Controller
@RequestMapping("/formulario")
public class FormularioController {
    private final SeccionService seccionService;

    public FormularioController(SeccionService seccionService) {
        this.seccionService = seccionService;
    }

    @GetMapping
    public String mostrarFormulario(Model model, HttpSession session) { // <-- 2. Pide la sesión
        
        // 3. Verificamos si el "tipo_sujeto" fue guardado en la sesión
        String tipoSujeto = (String) session.getAttribute("tipo_sujeto");

        if (tipoSujeto == null) {
            // 4. Si no existe, el usuario no pasó por el ingreso.
            // Lo forzamos a volver.
            return "redirect:/ingreso";
        }

        // 5. Si existe, llamamos al NUEVO método de servicio (Paso 4)
        // y le pasamos el tipo ("CASO" o "CONTROL")
        model.addAttribute("secciones", seccionService.getSeccionesFiltradas(tipoSujeto));
        
        // También pasamos el tipo a la vista, por si quieres mostrar
        // "Mostrando formulario para: CASO"
        model.addAttribute("tipo_sujeto", tipoSujeto);

        return "form/formulario";
    }
}