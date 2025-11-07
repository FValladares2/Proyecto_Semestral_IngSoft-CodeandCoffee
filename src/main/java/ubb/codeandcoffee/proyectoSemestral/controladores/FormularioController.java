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
    // --- 1. Inyección de TODOS los servicios ---
    private final SeccionService seccionService;
    private final SujetoEstudioService sujetoService;
    private final AntecedenteService antecedenteService;
    private final DatoSolicitadoService datoSolicitadoService;

    // --- 2. Un ÚNICO constructor para todos ---
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

    /**
     * Muestra el formulario (GET).
     * Recibe el id_sujeto (al azar) desde el SujetoController.
     */
    @GetMapping
    public String mostrarFormulario(
            @RequestParam("id_sujeto") String idSujeto, // <-- Acepta el ID al azar
            Model model, HttpSession session) {

        // Verifica que el usuario haya pasado por /ingreso
        String tipoSujeto = (String) session.getAttribute("tipo_sujeto"); // "CASO" o "CONTROL"
        if (tipoSujeto == null) {
            return "redirect:/ingreso";
        }

        // --- 3. LÓGICA DE BÚSQUEDA CORREGIDA ---
        // Convierte el tipo de la sesión (ej: "CASO") al tipo de la BDD (ej: "CA")
        String tipoBD = tipoSujeto.equalsIgnoreCase("CASO") ? "CA" : "CO";

        // Busca al sujeto usando la llave compuesta (ID al azar + Tipo)
        // (Esto requiere el método 'findByCompuesta' en tu SujetoEstudioService)
        SujetoEstudio sujeto = sujetoService.findByCompuesta(idSujeto, tipoBD);

        if (sujeto == null) {
            // Si el ID es inválido o no coincide con el tipo, lo envía de vuelta
            return "redirect:/ingreso?error=sujeto_no_encontrado";
        }

        // Carga las secciones filtradas (CASO/CONTROL)
        model.addAttribute("secciones", seccionService.getSeccionesFiltradas(tipoSujeto));

        // Pasa el objeto 'sujeto' completo al HTML (para los campos ocultos)
        model.addAttribute("sujeto", sujeto);

        return "form/formulario";
    }

    /**
     * Recibe y guarda los datos del formulario (POST).
     */
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