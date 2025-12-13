package ubb.codeandcoffee.proyectoSemestral.controladores;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ubb.codeandcoffee.proyectoSemestral.modelo.DatoSolicitado;
import ubb.codeandcoffee.proyectoSemestral.modelo.Opcion;
import ubb.codeandcoffee.proyectoSemestral.modelo.Seccion;
import ubb.codeandcoffee.proyectoSemestral.modelo.TipoRespuesta;
import ubb.codeandcoffee.proyectoSemestral.servicios.DatoSolicitadoService;
import ubb.codeandcoffee.proyectoSemestral.servicios.OpcionService;
import ubb.codeandcoffee.proyectoSemestral.servicios.SeccionService;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/formulario")
public class FormularioAdminController {

    private final SeccionService seccionService;
    private final OpcionService opcionService;
    private final DatoSolicitadoService datoSolicitadoService;

    public FormularioAdminController(SeccionService seccionService, DatoSolicitadoService datoSolicitadoService,
                                     OpcionService opcionService) {
        this.seccionService = seccionService;
        this.datoSolicitadoService = datoSolicitadoService;
        this.opcionService = opcionService;
    }

    @GetMapping("/crear-formulario")
    public String mostrarCrearFormulario(Model modelo) {
        List<Seccion> listaDeSecciones = seccionService.getAllSecciones();
        modelo.addAttribute("secciones", listaDeSecciones);
        return "form/admin_formulario_dashboard";
    }

    @GetMapping("/crear-seccion")
    public String mostrarCrearSeccion(Model modelo) {
        modelo.addAttribute("seccion", new Seccion());
        return "form/crear_seccion";
    }

    @PostMapping("/seccion/guardar")
    public String guardarSeccion(@ModelAttribute("seccion") Seccion seccion) {
        seccionService.guardarSeccion(seccion);
        // CORREGIDO: Faltaba el /formulario intermedio
        return "redirect:/admin/formulario/crear-formulario";
    }

    @GetMapping("/crear-pregunta")
    public String mostrarCrearPregunta(Model modelo) {
        modelo.addAttribute("datoSolicitado", new DatoSolicitado());
        modelo.addAttribute("secciones", seccionService.getAllSecciones());
        return "form/crear_pregunta";
    }

    @PostMapping("/pregunta/guardar")
    public String guardarPregunta(@ModelAttribute DatoSolicitado datoSolicitado) {
        // logica para limpiar el nombre stata si es para estudio
        if (Boolean.TRUE.equals(datoSolicitado.getEstudio()) && datoSolicitado.getNombreStata() != null) {
            // esto borra todo lo que NO sea letra o numero (quita espacios, tildes, simbolos)
            String limpio = datoSolicitado.getNombreStata().replaceAll("[^a-zA-Z0-9]", "");
            datoSolicitado.setNombreStata(limpio);
        } else {
            // si no es estudio, mejor lo dejamos nulo para no ensuciar la bd
            datoSolicitado.setNombreStata(null);
        }

        // validacion basica para rangos
        if (datoSolicitado.getTipoRespuesta() != TipoRespuesta.NUMERO) {
            datoSolicitado.setValorMin(null);
            datoSolicitado.setValorMax(null);
        }

        datoSolicitadoService.guardarDatoSolicitado(datoSolicitado);
        return "redirect:/admin/formulario/crear-formulario"; // o donde redirijas
    }

    @GetMapping("/opcion/nueva/{idDato}")
    public String mostrarCrearOpcion(@PathVariable("idDato") Integer idDato, Model modelo) {

        Optional<DatoSolicitado> posiblePregunta = datoSolicitadoService.getById(idDato);

        if (posiblePregunta.isPresent()) {
            DatoSolicitado preguntaPadre = posiblePregunta.get();

            Opcion nuevaOpcion = new Opcion();
            nuevaOpcion.setDatoSolicitado(preguntaPadre);

            modelo.addAttribute("opcion", nuevaOpcion);
            modelo.addAttribute("nombrePregunta", preguntaPadre.getNombre());

            return "form/crear_opcion";

        } else {
            return "redirect:/admin/formulario/crear-formulario";
        }
    }

    @PostMapping("/opcion/guardar")
    public String guardarOpcion(@ModelAttribute("opcion") Opcion opcion, Model modelo) {
        try {
            // intentamos guardar
            opcionService.guardarOpcion(opcion);
            
            return "redirect:/admin/formulario/crear-formulario";

        } catch (RuntimeException e) {
            // ATRAPAMOS EL ERROR 

            // pasamos el mensaje de error a la vista 
            modelo.addAttribute("error", e.getMessage());

            // devolvemos el objeto 'opcion' para que el usuario no tenga que escribir todo de nuevo
            modelo.addAttribute("opcion", opcion);

            // como la vista espera 'nombrePregunta' para el título, hay que buscarlo de nuevo
            if (opcion.getDatoSolicitado() != null && opcion.getDatoSolicitado().getId_dato() != null) {
                // buscamos la pregunta original para sacar el nombre (leyenda)
                datoSolicitadoService.getById(opcion.getDatoSolicitado().getId_dato())
                        .ifPresent(pregunta -> modelo.addAttribute("nombrePregunta", pregunta.getLeyenda()));
            }

            // fetornamos la VISTA "crear_opcion" (no redirect) para mostrar el error
            return "form/crear_opcion";
        }
    }

    @GetMapping("/pregunta/editar/{id}")
    public String mostrarEditarPregunta(@PathVariable("id") Integer id, Model modelo) {
        Optional<DatoSolicitado> preguntaExistente = datoSolicitadoService.getById(id);

        if (preguntaExistente.isPresent()) {
            // mandamos la pregunta existente al modelo
            modelo.addAttribute("datoSolicitado", preguntaExistente.get());

            // también necesitamos la lista de secciones para el <select>
            modelo.addAttribute("secciones", seccionService.getAllSecciones());

            // reutilizamos la misma vista de creación
            return "form/crear_pregunta";
        } else {
            return "redirect:/admin/formulario/crear-formulario";
        }
    }
}


