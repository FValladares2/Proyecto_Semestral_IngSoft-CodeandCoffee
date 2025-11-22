package ubb.codeandcoffee.proyectoSemestral.controladores;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.*;

import ubb.codeandcoffee.proyectoSemestral.modelo.SujetoEstudio;
import ubb.codeandcoffee.proyectoSemestral.repositorios.SujetoEstudioRepository;


@Controller
public class SujetoController {

    @Autowired
    private SujetoEstudioRepository sujetoEstudioRepository;

    @GetMapping("/ingreso")
    public String mostrarFormularioIngreso(Model model) {

        model.addAttribute("sujetoEstudio", new SujetoEstudio());
        return "form/ingreso_sujeto";
    }

    //Maneja el envío del formulario de ingreso.
    @PostMapping("/ingreso")
    public String procesarIngreso(
            @RequestParam String nombre,
            @RequestParam String tipo,
            @RequestParam(required = false) String direccion,
            @RequestParam(required = false) String ocupacion,
            @RequestParam(required = false) String telefono,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String nacionalidad,

            HttpSession session,
            RedirectAttributes redirectAttributes) {

        SujetoEstudio sujetoExistente = sujetoEstudioRepository.findByNombre(nombre);


        if (sujetoExistente != null) {
            redirectAttributes.addFlashAttribute("error_nombre",
                    "Error: El nombre '" + nombre + "' ya se encuentra registrado.");
            return "redirect:/ingreso";
        }

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
                redirectAttributes.addFlashAttribute("error_tipo",
                        "Error: El valor de Tipo seleccionado no es válido.");
                return "redirect:/ingreso";
        }

        try {

            SujetoEstudio nuevoSujeto = new SujetoEstudio();

            nuevoSujeto.setNombre(nombre);
            nuevoSujeto.setTipo(tipoParaBD);

            nuevoSujeto.setDireccion(direccion);
            nuevoSujeto.setOcupacion(ocupacion);
            nuevoSujeto.setTelefono(telefono);
            nuevoSujeto.setEmail(email);
            nuevoSujeto.setNacionalidad(nacionalidad);
            //se crea el sujeto
            sujetoEstudioRepository.save(nuevoSujeto);

            session.setAttribute("SUJETO_PENDIENTE", nuevoSujeto);
            session.setAttribute("tipo_sujeto", tipo);

            return "redirect:/formulario";

        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute("error_nombre",
                    "Error inesperado al guardar el sujeto. Revise las restricciones de la base de datos.");
            return "redirect:/ingreso";
        }
    }
}