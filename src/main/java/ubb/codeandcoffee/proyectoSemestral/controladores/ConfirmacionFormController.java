package ubb.codeandcoffee.proyectoSemestral.controladores;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ubb.codeandcoffee.proyectoSemestral.modelo.Antecedente;
import ubb.codeandcoffee.proyectoSemestral.modelo.SujetoEstudio;
import ubb.codeandcoffee.proyectoSemestral.repositorios.AntecedenteRepository;
import ubb.codeandcoffee.proyectoSemestral.repositorios.SujetoEstudioRepository;
import ubb.codeandcoffee.proyectoSemestral.servicios.SujetoEstudioService;

import java.util.List;


@Controller
public class ConfirmacionFormController {
    private final SujetoEstudioRepository sujetoEstudioRepository;
    private final AntecedenteRepository antecedenteRepository;

    @Autowired
    public ConfirmacionFormController(SujetoEstudioRepository sujetoEstudioRepository, AntecedenteRepository antecedenteRepository) {
        this.sujetoEstudioRepository = sujetoEstudioRepository;
        this.antecedenteRepository = antecedenteRepository;
    }

    @Autowired
    private SujetoEstudioService sujetoEstudioService;

    @GetMapping("/confirmacion")
    public String mostrarConfirmacion(HttpSession session, Model model) {

        SujetoEstudio sujeto = (SujetoEstudio) session.getAttribute("SUJETO_PENDIENTE");
        List<Antecedente> antecedentes = (List<Antecedente>) session.getAttribute("ANTECEDENTES_PENDIENTES");

        //si falta alguno de los datos, redirigir de vuelta al ingreso
        if (sujeto == null || antecedentes == null) {
            //limpiar sesion de cualquier dato inconsistente
            session.removeAttribute("SUJETO_PENDIENTE");
            session.removeAttribute("ANTECEDENTES_PENDIENTES");
            session.removeAttribute("tipo_sujeto");
            return "redirect:/ingreso";
        }

        //agregar el sujeto al Model
        model.addAttribute("sujeto", sujeto);
        model.addAttribute("antecedentes", antecedentes);

        return "form/confirmacion_form";
    }

    @PostMapping("/confirmar-guardado")
    public String confirmarGuardado(HttpSession session, RedirectAttributes redirectAttributes) {

        //recuperar los datos de la sesion
        SujetoEstudio sujetoPendiente = (SujetoEstudio) session.getAttribute("SUJETO_PENDIENTE");
        List<Antecedente> antecedentesParaGuardar = (List<Antecedente>) session.getAttribute("ANTECEDENTES_PENDIENTES");

        if (sujetoPendiente == null || antecedentesParaGuardar == null) {
            //manejar si los datos ya no están en la sesión
            redirectAttributes.addFlashAttribute("error", "Error de sesión: Los datos a guardar no se encontraron.");
            return "redirect:/ingreso";
        }

        //logica de guardado en la base
        try {

            //guardar y obtener el sujeto completo
            SujetoEstudio sujeto = sujetoEstudioService.guardarSujetoEstudio(sujetoPendiente);

            //despues de obtener el sujeto completo lo agregamos a cada antecedente
            for (Antecedente antecedente : antecedentesParaGuardar) {
                antecedente.setSujetoEstudio(sujeto);
            }

            //guardamos todos los antecedentes en la base
            antecedenteRepository.saveAll(antecedentesParaGuardar);

            //limpiar la seccion
            session.removeAttribute("SUJETO_PENDIENTE");
            session.removeAttribute("ANTECEDENTES_PENDIENTES");
            session.removeAttribute("tipo_sujeto");

            //redirigir, creo que deberia redirigir al menu pero ni idea como XD
            return "redirect:/ingreso";

        } catch (Exception e) {
            // Manejo de errores de guardado
            System.err.println("Error al guardar en la base de datos: " + e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Ocurrió un error al intentar guardar los datos. Intente de nuevo.");
            return "redirect:/confirmacion";
        }
    }
}
