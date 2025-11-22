package ubb.codeandcoffee.proyectoSemestral.controladores;

import jakarta.servlet.http.HttpSession; // <-- 1. Importa HttpSession
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;
import ubb.codeandcoffee.proyectoSemestral.modelo.Antecedente;
import ubb.codeandcoffee.proyectoSemestral.modelo.DatoSolicitado;
import ubb.codeandcoffee.proyectoSemestral.modelo.SujetoEstudio;
import ubb.codeandcoffee.proyectoSemestral.servicios.AntecedenteService;
import ubb.codeandcoffee.proyectoSemestral.servicios.DatoSolicitadoService;
import ubb.codeandcoffee.proyectoSemestral.servicios.SeccionService;
import ubb.codeandcoffee.proyectoSemestral.servicios.SujetoEstudioService;

@Controller
@RequestMapping("/formulario")
public class FormularioController {
    // inyección de los servicios
    private final SeccionService seccionService;
    private final SujetoEstudioService sujetoService;
    private final AntecedenteService antecedenteService;
    private final DatoSolicitadoService datoSolicitadoService;

    // constructor
    @Autowired
    public FormularioController(SeccionService seccionService,
                                SujetoEstudioService sujetoService,
                                AntecedenteService antecedenteService,
                                DatoSolicitadoService datoSolicitadoService) {
        this.seccionService = seccionService;
        this.sujetoService = sujetoService;
        this.antecedenteService = antecedenteService;
        this.datoSolicitadoService = datoSolicitadoService;
    }

    //Muestra el formulario
    @GetMapping
    public String mostrarFormulario(
            Model model, HttpSession session) {

        // Verifica que el usuario haya pasado por /ingreso
        String tipoSujeto = (String) session.getAttribute("tipo_sujeto"); // "CASO" o "CONTROL"
        if (tipoSujeto == null) {
            return "redirect:/ingreso";
        }

        String tipoBD = tipoSujeto.equalsIgnoreCase("CASO") ? "CA" : "CO";

        model.addAttribute("secciones", seccionService.getSeccionesFiltradas(tipoSujeto));
        model.addAttribute("sujeto", tipoSujeto);

        return "form/formulario";
    }


    //Recibe y guarda los datos del formulario
    @PostMapping
    public String guardarFormulario(@RequestParam MultiValueMap<String, String> allParams, HttpSession session) {

        //datos ocultos en el html
        String idSujeto = allParams.getFirst("id_sujeto");
        String tipoSujetoBD = allParams.getFirst("tipo_sujeto");

        SujetoEstudio sujeto = sujetoService.findByCompuesta(idSujeto, tipoSujetoBD);
        if (sujeto == null) {
            return "redirect:/ingreso?error=sujeto_invalido";
        }

        allParams.remove("id_sujeto");
        allParams.remove("tipo_sujeto");

        for (String idDatoString : allParams.keySet()) {

            try {
                Integer idDato = Integer.parseInt(idDatoString);
                String valorRespuesta = allParams.get(idDatoString).get(0);


                DatoSolicitado dato = datoSolicitadoService.getById(idDato)
                        .orElse(null);

                if (dato != null && valorRespuesta != null && !valorRespuesta.isEmpty()) {

                    Antecedente antecedente = new Antecedente();
                    antecedente.setSujetoEstudio(sujeto);
                    antecedente.setDatoSolicitado(dato);

                    try {
                        antecedente.setValorNum(Float.parseFloat(valorRespuesta));
                    } catch (NumberFormatException e) {
                        antecedente.setValorString(valorRespuesta);
                    }


                    antecedenteService.guardarAntecedente(antecedente);
                }

            } catch (NumberFormatException e) {

            }
        }

        //caso de exito
        session.removeAttribute("tipo_sujeto");
        return "redirect:/ingreso?exito=true";
    }
}