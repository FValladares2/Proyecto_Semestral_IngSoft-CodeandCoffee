package ubb.codeandcoffee.proyectoSemestral.controladores;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ubb.codeandcoffee.proyectoSemestral.modelo.DatoSolicitado;
import ubb.codeandcoffee.proyectoSemestral.modelo.Opcion;
import ubb.codeandcoffee.proyectoSemestral.modelo.Seccion;
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
    public String guardarPregunta(@ModelAttribute("datoSolicitado") DatoSolicitado datoSolicitado) {
        datoSolicitadoService.guardarDatoSolicitado(datoSolicitado);
        return "redirect:/admin/formulario/crear-formulario";
    }

    // --- MÉTODOS DE OPCIÓN (Corregidos) ---

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
            // CORREGIDO: Redirige a tu vista principal real
            return "redirect:/admin/formulario/crear-formulario";
        }
    }

    @PostMapping("/opcion/guardar")
    public String guardarOpcion(@ModelAttribute("opcion") Opcion opcion, Model modelo) {
        try {
            // 1. Intentamos guardar. Si falla, saltará al bloque 'catch'
            opcionService.guardarOpcion(opcion);

            // 2. Si pasa, redirigimos al éxito
            return "redirect:/admin/formulario/crear-formulario";

        } catch (RuntimeException e) {
            // 3. AQUÍ ATRAPAMOS EL ERROR (IllegalStateException o IllegalArgumentException)

            // Pasamos el mensaje de error a la vista (Ej: "Ya existe una opción con valor 1")
            modelo.addAttribute("error", e.getMessage());

            // Devolvemos el objeto 'opcion' para que el usuario no tenga que escribir todo de nuevo
            modelo.addAttribute("opcion", opcion);

            // RE-CARGAMOS EL NOMBRE DE LA PREGUNTA
            // Como la vista espera 'nombrePregunta' para el título, hay que buscarlo de nuevo
            if (opcion.getDatoSolicitado() != null && opcion.getDatoSolicitado().getId_dato() != null) {
                // Buscamos la pregunta original para sacar el nombre (leyenda)
                datoSolicitadoService.getById(opcion.getDatoSolicitado().getId_dato())
                        .ifPresent(pregunta -> modelo.addAttribute("nombrePregunta", pregunta.getLeyenda()));
            }

            // 4. Retornamos la VISTA "crear_opcion" (No redirect) para mostrar el error
            return "form/crear_opcion";
        }
    }

    // URL: /admin/formulario/pregunta/editar/5
    @GetMapping("/pregunta/editar/{id}")
    public String mostrarEditarPregunta(@PathVariable("id") Integer id, Model modelo) {
        Optional<DatoSolicitado> preguntaExistente = datoSolicitadoService.getById(id);

        if (preguntaExistente.isPresent()) {
            // 1. Mandamos la pregunta existente al modelo
            modelo.addAttribute("datoSolicitado", preguntaExistente.get());

            // 2. También necesitamos la lista de secciones para el <select>
            modelo.addAttribute("secciones", seccionService.getAllSecciones());

            // Reutilizamos la misma vista de creación
            return "form/crear_pregunta";
        } else {
            return "redirect:/admin/formulario/crear-formulario";
        }
    }
}


