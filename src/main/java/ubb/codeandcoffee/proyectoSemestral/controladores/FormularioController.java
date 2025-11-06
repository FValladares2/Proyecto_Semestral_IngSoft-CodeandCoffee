package ubb.codeandcoffee.proyectoSemestral.controladores;

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
    public String mostrarFormulario(Model model) {
        model.addAttribute("secciones", seccionService.getAllSecciones());
        return "form/formulario";
    }
}
