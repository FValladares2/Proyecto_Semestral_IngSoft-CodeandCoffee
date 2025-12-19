package ubb.codeandcoffee.proyectoSemestral.controladores;

import jakarta.servlet.http.HttpSession; // <-- 1. Importa HttpSession
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import org.springframework.ui.Model;
import ubb.codeandcoffee.proyectoSemestral.DTO.AntecedenteDTO;
import ubb.codeandcoffee.proyectoSemestral.DTO.SujetoFormDTO;
import ubb.codeandcoffee.proyectoSemestral.modelo.*;
import ubb.codeandcoffee.proyectoSemestral.repositorios.AntecedenteRepository;
import ubb.codeandcoffee.proyectoSemestral.repositorios.SujetoEstudioRepository;
import ubb.codeandcoffee.proyectoSemestral.servicios.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.*;

import java.util.*;

// controlador para manejar el formulario de ingreso de datos por parte del usuario final
@Controller
@RequestMapping("/formulario")
public class FormularioController {
    private final SeccionService seccionService;
    private final SujetoEstudioService sujetoService;
    private final AntecedenteService antecedenteService;
    private final OpcionService opcionService;
    private final DatoSolicitadoService datoSolicitadoService;
    @Autowired
    private SujetoEstudioRepository sujetoEstudioRepository;
    @Autowired
    private AntecedenteRepository antecedenteRepository;

    // constructor
    @Autowired
    public FormularioController(SeccionService seccionService,
                                SujetoEstudioService sujetoService,
                                AntecedenteService antecedenteService,
                                DatoSolicitadoService datoSolicitadoService,
                                OpcionService opcionService,
                                SujetoEstudioRepository sujetoEstudioRepository,
                                AntecedenteRepository antecedenteRepository) {
        this.seccionService = seccionService;
        this.sujetoService = sujetoService;
        this.antecedenteService = antecedenteService;
        this.datoSolicitadoService = datoSolicitadoService;
        this.opcionService = opcionService;
        this.sujetoEstudioRepository=sujetoEstudioRepository;
        this.antecedenteRepository=antecedenteRepository;
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

        //crea el DTO principal
        SujetoFormDTO formWrapper = new SujetoFormDTO();
        List<AntecedenteDTO> antecedentesIniciales = new ArrayList<>();
        //recupera los datos solicitados de la base de datos
        List<DatoSolicitado> datosSolicitados = datoSolicitadoService.buscarTodosLosDatos(tipoSujeto);
        //llena la coleccion, crea un antecedente DTO y le pasa uno de los datos de la lista de datosSolicitados
        //y lo agrega a la collecion, asi por cada dato de la lista
        for (DatoSolicitado dato : datosSolicitados) {
            AntecedenteDTO dto = new AntecedenteDTO();
            dto.setDatoSolicitado(dato);
            antecedentesIniciales.add(dto);
        }
        //asignar la lista de antecedentes al DTO principal
        formWrapper.setAntecedentes(antecedentesIniciales);
        //mapear el objeto al modelo
        model.addAttribute("formWrapper", formWrapper);

        model.addAttribute("secciones", seccionService.getSeccionesFiltradas(tipoSujeto));
        model.addAttribute("sujeto", tipoSujeto);

        return "form/formulario";
    }


    // Endpoint POST para guardar el formulario completo
    @PostMapping
    public String guardarFormulario(@ModelAttribute SujetoFormDTO formWrapper, HttpSession session, RedirectAttributes redirectAttributes){
        //crear lista de Antecedentes, estos son los antecedentes que se guardan en la base
        List<Antecedente> antecedentesParaGuardar = new ArrayList<>();

        boolean errorValidacion = false;

        //iterar por cada antecedenteDTO guardado anteriormente
        for (AntecedenteDTO dto : formWrapper.getAntecedentes()) {
            DatoSolicitado dato = dto.getDatoSolicitado();
            //crear antecedente y guardar el dato en el
            Antecedente antecedente = new Antecedente(dato);

            //las respuestas a guardar para el antecedente seran diferentes segun el tipo de respuesta esperada
            //si el tipo de respuesta es con opciones:
            if(dato.getTipoRespuesta() == TipoRespuesta.OPCIONES){
                //obtenemos el id de la opcion con el dto desde el HTML
                int id_opcion = dto.getId_opcion();
                Opcion opcion;
                //si el id es 0 la opcion es null (entonces ambos valores seran null)
                if(id_opcion == 0){
                    opcion= null;
                }else{
                    //si no buscar la opcion por su id
                    Optional<Opcion> optionalOpcion = opcionService.getById(id_opcion);
                    opcion = optionalOpcion.orElseThrow(() ->
                            new RuntimeException("Error al procesar formulario: Opción múltiple con ID " + id_opcion + " no encontrada.")
                    );
                }
                //al valorNum de antecedente le pasamos el valor de la opcion
                if (opcion != null) {
                    //como valor de opcion es int y valorNum es float se debe hacer una transformacion
                    Integer valorOpcion = opcion.getValor();
                    if (valorOpcion != null) {
                        // Convertir Integer a Float
                        Float valorFloat = (float) opcion.getValor();
                        // asignar
                        antecedente.setValorNum(valorFloat);
                        antecedente.setTextoOpcion(opcion.getNombre());
                    } else {
                        antecedente.setValorNum(null);
                    }

                    //si la opcion tiene texto adicional
                    if (opcion.isRequiereTexto()) {
                        // Guardar en valorString
                        antecedente.setValorString(dto.getRespuestaIngresada());
                    } else {
                        // Si no dejar en null
                        antecedente.setValorString(null);
                    }
                } else {
                    //caso donde no se selecciono una opcion
                    antecedente.setValorNum(null);
                    antecedente.setValorString(null);
                }
            }else{
                //si se espera un numero
                if (dato.getTipoRespuesta()== TipoRespuesta.NUMERO) {
                    if(dto.getRespuestaIngresada().trim().isEmpty()){
                        antecedente.setValorString(null);
                    }else{
                        //intentamos parsear la respuesta
                        Float valorNum;
                        try {
                            valorNum = Float.parseFloat(dto.getRespuestaIngresada());
                        } catch (NumberFormatException e) {
                            redirectAttributes.addFlashAttribute("error_nombre",
                                    "Error: el formato del valor ingresado no es valido");
                            throw new RuntimeException("Error: formato no valido");
                        }
                        //obtener los límites
                        Integer min = dato.getValorMin();
                        Integer max = dato.getValorMax();

                        //VALIDACIÓN DE RANGO

                        //si el valor es menor que el mínimo
                        if (min != null && valorNum < min) {
                            redirectAttributes.addFlashAttribute("error_validacion",
                                    "Error: El formato del valor ingresado para " + dato.getLeyenda() + " no es válido.");
                            return "redirect:/formulario";
                            //errorValidacion = true;
                            //continue; // Salta este antecedente inválido
                        }

                        // Si el valor es mayor que el máximo
                        if (max != null && valorNum > max) {
                            redirectAttributes.addFlashAttribute("error_validacion",
                                    "Error: El formato del valor ingresado para " + dato.getLeyenda() + " no es válido.");
                            return "redirect:/formulario";
                            //errorValidacion = true;
                            //continue; // Salta este antecedente inválido

                        }
                        antecedente.setValorNum(valorNum);
                    }
                    antecedente.setValorString(null); // Aseguramos que el otro campo esté nulo

                } else {
                    // Si se espera texto o una fecha
                    if(dto.getRespuestaIngresada().trim().isEmpty()){
                        antecedente.setValorString(null);
                    }else{
                        antecedente.setValorString(dto.getRespuestaIngresada());
                    }
                    antecedente.setValorNum(null); // Aseguramos que el otro campo esté nulo
                }
            }
            //guardar antecedente en la coleccion
            antecedentesParaGuardar.add(antecedente);
        }

        //guardar el sujeto y los antecedentes
        SujetoEstudio sujetoPendiente = (SujetoEstudio) session.getAttribute("SUJETO_PENDIENTE");
        if (sujetoPendiente == null) {
            //manejo de error si el sujeto no se encontró en la sesión (debería venir del ingreso)
            redirectAttributes.addFlashAttribute("error", "Error: Sujeto de estudio no encontrado. Reingrese los datos.");
            return "redirect:/ingreso";
        }
        session.setAttribute("ANTECEDENTES_PENDIENTES", antecedentesParaGuardar);
        //redirigir a la vista de la confirmación
        return "redirect:/confirmacion";

        /*

        // Esta parte se traslado a la confirmacion del form XD

        //pero lo conservamos en caso de nesesitar usarlo desde aqui
        //obtener sujeto de la seccion (se guardo ahi en el ingreso)
        SujetoEstudio sujetoPendiente = (SujetoEstudio) session.getAttribute("SUJETO_PENDIENTE");
        if (sujetoPendiente == null) {
            return "redirect:/ingreso";
        }
        //Guardar el sujeto en la base (se debe guardar para obtener el id que se crea en la base)
        sujetoEstudioRepository.save(sujetoPendiente);
        //obtener el sujeto completo buscando por el nombre
        SujetoEstudio sujeto = sujetoEstudioRepository.findByNombre(sujetoPendiente.getNombre());

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
*/
    }
}
