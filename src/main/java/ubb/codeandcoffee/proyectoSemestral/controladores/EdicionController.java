package ubb.codeandcoffee.proyectoSemestral.controladores;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ubb.codeandcoffee.proyectoSemestral.DTO.AntecedenteDTO;
import ubb.codeandcoffee.proyectoSemestral.DTO.SujetoFormDTO;
import ubb.codeandcoffee.proyectoSemestral.modelo.*;
import ubb.codeandcoffee.proyectoSemestral.repositorios.AntecedenteRepository;
import ubb.codeandcoffee.proyectoSemestral.repositorios.DatoSolicitadoRepository;
import ubb.codeandcoffee.proyectoSemestral.repositorios.SujetoEstudioRepository;
import ubb.codeandcoffee.proyectoSemestral.servicios.DatoSolicitadoService;
import ubb.codeandcoffee.proyectoSemestral.servicios.OpcionService;
import ubb.codeandcoffee.proyectoSemestral.servicios.SeccionService;
import ubb.codeandcoffee.proyectoSemestral.servicios.SujetoEstudioService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/edicion")
public class EdicionController {

    @Autowired
    private SeccionService seccionService;

    @Autowired
    private SujetoEstudioRepository repo;

    @Autowired
    private SujetoEstudioService sujetoEstudioService;

    @Autowired
    private DatoSolicitadoService datoSolicitadoService;

    @Autowired
    private DatoSolicitadoRepository datoSolicitadoRepository;

    @Autowired
    private AntecedenteRepository antecedenteRepository;


    @Autowired
    private OpcionService opcionService;

    //entremedio voy a poner lo relativo a la actualizacion de los datos de un sujeto
    @GetMapping("/sujetos")//aqui se accede a la lista de sujetos, para elegir a cual queremos actualizar
    public String mostrarSujetos( Model modelo) {

        ArrayList<SujetoEstudio> sujetosEstudio = sujetoEstudioService.getSujetoEstudio();


        modelo.addAttribute("sujetos", sujetosEstudio);

        return "form/lista_sujetos";
    }

    @GetMapping("/sujetos/editar/{tipo}/{id}")//eligiendo el sujeto a actualizar, tenemos que elegir que actualizaremos
    public String mostrarOpcionesActualizar(@PathVariable("id") String id, @PathVariable("tipo") String tipo, Model modelo) {

        codigo_sujeto codigo = new codigo_sujeto(id, tipo);
        SujetoEstudio sujeto = sujetoEstudioService.getById(codigo)
                .orElseThrow(() -> new IllegalArgumentException("Sujeto no encontrado"));

        modelo.addAttribute("sujeto", sujeto);

        return "form/menu_actualizar_sujeto";
    }

    @GetMapping("sujetos/editar/ingreso/{tipo}/{id}")//si elegimos actualizar ingreso (sus datos personales)
    public String mostrarEdicionDatos(@PathVariable("id") String id,@PathVariable("tipo") String tipo, Model model) {

        codigo_sujeto codigo = new codigo_sujeto(id, tipo);
        SujetoEstudio sujeto = repo.findById(codigo)
                .orElseThrow(() -> new IllegalArgumentException("Sujeto no encontrado"));


        model.addAttribute("sujeto", sujeto);
        model.addAttribute("editar", true);

        return "form/ingreso_sujeto_edicion";
    }

    //envio de actualizacion
    //necesita el springsecurity funcionando para funcionar bien
    @PostMapping("/sujetos/editar/ingreso/{tipo}/{id}")
    public String procesarActualizacionIngreso(
            @PathVariable("id") String id,
            @RequestParam String nombre,
            @PathVariable("tipo") String tipo,
            @RequestParam(required = false) String direccion,
            @RequestParam(required = false) String ocupacion,
            @RequestParam(required = false) String telefono,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String nacionalidad,
            HttpSession session,
            RedirectAttributes redirectAttributes,
            HttpServletRequest request) {

        codigo_sujeto codigo = new codigo_sujeto(id, tipo);
        SujetoEstudio sujeto = sujetoEstudioService.findByCompuesta(id, tipo);

        if (request.isUserInRole("ROLE_ADMINISTRADOR") && sujeto == null) {
            redirectAttributes.addFlashAttribute("error",
                    "El sujeto con ID " + id + " no existe.");
            return "redirect:/menu/admin";
        }else{
            if(request.isUserInRole("ROLE_RECOLECTOR_DE_DATOS") && sujeto == null){
                redirectAttributes.addFlashAttribute("error",
                        "El sujeto con ID " + id + " no existe.");
                return "redirect:/menu/recolector";
            }

        }


        try {
            sujeto.setNombre(nombre);
            sujeto.setDireccion(direccion);
            sujeto.setOcupacion(ocupacion);
            sujeto.setTelefono(telefono);
            sujeto.setEmail(email);
            sujeto.setNacionalidad(nacionalidad);

            sujetoEstudioService.updateById(sujeto, codigo);

            redirectAttributes.addFlashAttribute("exito",
                    "Sujeto actualizado correctamente.");

            if (request.isUserInRole("ROLE_ADMINISTRADOR")) {
                return "redirect:/menu/admin";
            }else{
                return "redirect:/menu/recolector";
            }


        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute("error",
                    "Error al actualizar el sujeto. Revise los datos.");
            return "redirect:/sujetos/editar/ingreso/{tipo}/{id}";
        }
    }

    // EDICION DE FORMULARIO

    @GetMapping("/sujetos/editar/formulario/{tipo}/{id}")
    public String editarFormularioRespuesta(@PathVariable("id") String id, @PathVariable("tipo") String tipo, Model model) {

        //validacion
        codigo_sujeto codigo = new codigo_sujeto(id, tipo);
        SujetoEstudio sujeto = sujetoEstudioService.getById(codigo)
                .orElseThrow(() -> new IllegalArgumentException("Sujeto no encontrado"));

        //se traen las preguntas y respuestas
        List<DatoSolicitado> todasLasPreguntas = datoSolicitadoService.buscarTodosLosDatos(tipo);
        List<Antecedente> antecedentesExistentes = antecedenteRepository.findAllBySujetoEstudio(sujeto);

        SujetoFormDTO formWrapper = new SujetoFormDTO();
        formWrapper.setId_sujeto(sujeto.getId_sujeto());
        formWrapper.setTipo(tipo);

        List<AntecedenteDTO> dtosParaVista = new ArrayList<>();

        //revisar cada pregunta
        for (DatoSolicitado pregunta : todasLasPreguntas) {
            AntecedenteDTO dto = new AntecedenteDTO();
            dto.setDatoSolicitado(pregunta);

            // comprobar si la pregunta tiene respuesta
            Optional<Antecedente> respuestaExiste = antecedentesExistentes.stream()
                    .filter(a -> a.getDatoSolicitado().getId_dato() == pregunta.getId_dato())
                    .findFirst();

            if (respuestaExiste.isPresent()) {
                Antecedente ant = respuestaExiste.get();
                dto.setId_antecedente(ant.getIdAntecedentes());

                dto.setRespuestaIngresada(ant.getValorString());

                if (pregunta.getTipoRespuesta() == TipoRespuesta.OPCION_MULTIPLE) {

                    if (ant.getValorNum() != null) {

                        int valorGuardado = ant.getValorNum().intValue();

                        Optional<Opcion> opcionMatch = pregunta.getOpciones().stream()
                                .filter(op -> op.getValor() != null && op.getValor() == valorGuardado)
                                .findFirst();

                        opcionMatch.ifPresent(op -> dto.setId_opcion(op.getId_opcion()));
                    }
                }

                else if (pregunta.getTipoRespuesta() == TipoRespuesta.NUMERO && ant.getValorNum() != null) {

                    float val = ant.getValorNum();

                    if(val % 1 == 0) {
                        dto.setRespuestaIngresada(String.valueOf((int) val));
                    } else {
                        dto.setRespuestaIngresada(String.valueOf(val));
                    }
                }
            }
            dtosParaVista.add(dto);
        }

        formWrapper.setAntecedentes(dtosParaVista);
        model.addAttribute("formWrapper", formWrapper);
        model.addAttribute("sujeto", sujeto);

        return "form/formulario_edicion";
    }


    @PostMapping("/sujetos/editar/formulario/{tipo}/{id}")
    public String actualizarFormulario(
            @PathVariable("tipo") String tipo,
            @PathVariable("id") String id,
            @ModelAttribute SujetoFormDTO formWrapper,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) {

        //validaciones
        codigo_sujeto codigo = new codigo_sujeto(id, tipo);
        SujetoEstudio sujeto = repo.findById(codigo).orElse(null);
        if (sujeto == null) {
            redirectAttributes.addFlashAttribute("error", "Sujeto no encontrado.");
            return request.isUserInRole("ROLE_ADMINISTRADOR") ? "redirect:/menu/admin" : "redirect:/menu/recolector";
        }

        try {
            if (formWrapper.getAntecedentes() == null || formWrapper.getAntecedentes().isEmpty()) {
                System.err.println("ERROR: sin antecedentes");
                redirectAttributes.addFlashAttribute("error", "no se recibio nada");
                return "redirect:/edicion/sujetos/editar/formulario/" + tipo + "/" + id;
            }

            for (AntecedenteDTO dto : formWrapper.getAntecedentes()) {
                Antecedente antecedente;
                DatoSolicitado dato;

                if (dto.getId_antecedente() != null && dto.getId_antecedente() > 0) {
                    //editar
                    antecedente = antecedenteRepository.findById(dto.getId_antecedente()).orElse(null);
                    if (antecedente == null) continue;
                    dato = antecedente.getDatoSolicitado();
                } else {
                    //responder sobre un blanco (no habia respuesta guardada)
                    if (dto.getDatoSolicitado() == null || dto.getDatoSolicitado().getId_dato() == 0) {
                        System.err.println("Saltando DTO sin ID de dato solicitado.");
                        continue;
                    }
                    dato = datoSolicitadoRepository.findById(dto.getDatoSolicitado().getId_dato())
                            .orElse(null);

                    if (dato == null) continue;

                    antecedente = new Antecedente(dato);
                    antecedente.setSujetoEstudio(sujeto);
                }

                antecedente.setValorNum(null);
                antecedente.setValorString(null);
                antecedente.setTextoOpcion(null);

                if (dato.getTipoRespuesta() == TipoRespuesta.OPCION_MULTIPLE) {
                    int idOpcion = dto.getId_opcion();

                    Opcion opcion = (idOpcion > 0) ? opcionService.getById(idOpcion).orElse(null) : null;

                    if (opcion != null) {
                        antecedente.setValorNum((float) opcion.getValor());
                        antecedente.setTextoOpcion(opcion.getNombre());

                        if (opcion.isRequiereTexto()) {
                            antecedente.setValorString(dto.getRespuestaIngresada());
                        }
                    }
                } else if (dato.getTipoRespuesta() == TipoRespuesta.NUMERO) {
                    String input = dto.getRespuestaIngresada();
                    if (input != null && !input.trim().isEmpty()) {
                        try {
                            antecedente.setValorNum(Float.parseFloat(input));
                        } catch (NumberFormatException e) {

                        }
                    }
                } else {

                    antecedente.setValorString(dto.getRespuestaIngresada());
                }

            }

            redirectAttributes.addFlashAttribute("exito", "datos actualizados correctamente");

            if (request.isUserInRole("ROLE_ADMINISTRADOR")) {
                return "redirect:/menu/admin";
            } else {
                return "redirect:/menu/recolector";
            }

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "error: " + e.getMessage());
            return "redirect:/edicion/sujetos/editar/formulario/" + tipo + "/" + id;
        }
    }
}
