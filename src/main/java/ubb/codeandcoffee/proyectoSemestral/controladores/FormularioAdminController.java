package ubb.codeandcoffee.proyectoSemestral.controladores;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ubb.codeandcoffee.proyectoSemestral.modelo.DatoSolicitado;
import ubb.codeandcoffee.proyectoSemestral.modelo.Opcion;
import ubb.codeandcoffee.proyectoSemestral.modelo.Seccion;
import ubb.codeandcoffee.proyectoSemestral.modelo.TipoRespuesta;
import ubb.codeandcoffee.proyectoSemestral.servicios.DatoSolicitadoService;
import ubb.codeandcoffee.proyectoSemestral.servicios.OpcionService;
import ubb.codeandcoffee.proyectoSemestral.servicios.SeccionService;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;


// Controlador para la administración del formulario (secciones, preguntas, opciones)
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

    /*@GetMapping("/crear-formulario")
    public String mostrarCrearFormulario(Model modelo) {
        // Obtenemos todas
        List<Seccion> listaDeSecciones = seccionService.getAllSecciones();

        // ORDENAMOS: Usamos el campo 'numero' para que salgan 1, 2, 3...
        listaDeSecciones.sort(Comparator.comparing(Seccion::getNumero));

        modelo.addAttribute("secciones", listaDeSecciones);
        return "form/admin_formulario_dashboard";
    }*/

    @GetMapping("/crear-formulario")
    public String mostrarCrearFormulario(Model modelo) {
        // 1. Verificamos si el modelo YA contiene un datoSolicitado (viene de un error de validación)
        // Si no existe (es una entrada limpia), creamos uno nuevo
        if (!modelo.containsAttribute("datoSolicitado")) {
            modelo.addAttribute("datoSolicitado", new DatoSolicitado());
        }

        // 2. Cargar secciones ordenadas
        List<Seccion> listaDeSecciones = seccionService.getAllSecciones();
        listaDeSecciones.sort(Comparator.comparing(Seccion::getNumero));
        modelo.addAttribute("secciones", listaDeSecciones);

        return "form/admin_formulario_dashboard";
    }

    //Seccion----------------------------------------------------------------------

    @GetMapping("/crear-seccion")
    public String mostrarCrearSeccion(Model modelo) {
        Seccion nuevaSeccion = new Seccion();
        Integer ultimo = seccionService.obtenerUltimoNumero();
        nuevaSeccion.setNumero(ultimo + 1);

        // traemos las EXISTENTES para la vista previa
        List<Seccion> existentes = seccionService.listarTodasOrdenadas();

        modelo.addAttribute("seccion", nuevaSeccion);
        modelo.addAttribute("seccionesExistentes", existentes);

        return "form/crear_seccion";
    }

    @PostMapping("/seccion/guardar")
    public String guardarSeccion(@ModelAttribute("seccion") Seccion seccion) {
        seccionService.guardarSeccion(seccion);
        // CORREGIDO: Faltaba el /formulario intermedio
        return "redirect:/admin/formulario/crear-formulario";
    }


    @GetMapping("/seccion/subir/{id}")
    public String subirSeccion(@PathVariable Integer id) {
        seccionService.cambiarOrden(id, "SUBIR");
        return "redirect:/admin/formulario/crear-formulario"; // Recarga la página para ver el cambio
    }

    @GetMapping("/seccion/bajar/{id}")
    public String bajarSeccion(@PathVariable Integer id) {
        seccionService.cambiarOrden(id, "BAJAR");
        return "redirect:/admin/formulario/crear-formulario";
    }

    //datoSolicitado---------------------------------------------------------------------

    @GetMapping("/crear-pregunta")
    public String mostrarCrearPregunta(Model modelo) {
        /*modelo.addAttribute("datoSolicitado", new DatoSolicitado());
        modelo.addAttribute("secciones", seccionService.getAllSecciones());*/

        if (!modelo.containsAttribute("datoSolicitado")) {
            modelo.addAttribute("datoSolicitado", new DatoSolicitado());
        }

        // Cargamos las secciones para el <select>
        List<Seccion> listaDeSecciones = seccionService.getAllSecciones();
        listaDeSecciones.sort(Comparator.comparing(Seccion::getNumero));
        modelo.addAttribute("secciones", listaDeSecciones);

        return "form/crear_pregunta";
    }


    @PostMapping("/pregunta/guardar")
    public String guardarPregunta(@ModelAttribute DatoSolicitado datoSolicitado, RedirectAttributes redirectAttributes) {
        try {
            // 1. Lógica de Nombre Stata
            if (Boolean.TRUE.equals(datoSolicitado.getEstudio()) &&
                    datoSolicitado.getNombreStata() != null &&
                    !datoSolicitado.getNombreStata().trim().isEmpty()) {

                String limpio = datoSolicitado.getNombreStata().replaceAll("[^a-zA-Z0-9]", "");
                datoSolicitado.setNombreStata(limpio);
            } else {
                // Si el checkbox está apagado, forzamos null para liberar el nombre único
                datoSolicitado.setNombreStata(null);
                datoSolicitado.setEstudio(false);
            }

            // 2. Limpieza de datos según tipo (por si acaso)
            if (datoSolicitado.getTipoRespuesta() != TipoRespuesta.NUMERO) {
                datoSolicitado.setValorMin(null);
                datoSolicitado.setValorMax(null);
            }

            // 3. Guardar (El service detectará si es Update o Insert por el ID)
            datoSolicitadoService.guardarDatoSolicitado(datoSolicitado);
            redirectAttributes.addFlashAttribute("mensajeExito", "Operación realizada con éxito");

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            redirectAttributes.addFlashAttribute("datoSolicitado", datoSolicitado);

            // Si hay error, redirigimos según si es edición o creación
            if(datoSolicitado.getId_dato() != null) {
                return "redirect:/admin/formulario/pregunta/editar/" + datoSolicitado.getId_dato();
            }
            return "redirect:/admin/formulario/crear-pregunta";
        }

        return "redirect:/admin/formulario/crear-formulario";
    }

    /*@PostMapping("/pregunta/guardar")
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
    }*/

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

    @GetMapping("/pregunta/activar/{id}")
    public String activarPregunta(@PathVariable("id") Integer id) {

        // Llamamos al servicio para que cambie el estado a true
        datoSolicitadoService.activarDato(id);

        // Redirigimos a la misma pagina de edicion para ver el cambio instantaneamente
        return "redirect:/admin/formulario/crear-formulario";
    }

    // Ruta para recibir la orden de eliminar (o desactivar)
    @GetMapping("/pregunta/eliminar/{id}")
    public String eliminarPregunta(@PathVariable("id") Integer id) {

        // Delegamos al servicio la logica de borrar o desactivar
        datoSolicitadoService.eliminarDato(id);

        // Recargamos la pagina para ver los cambios
        return "redirect:/admin/formulario/crear-formulario";
    }

    // opcion-------------------------------------------------------------------------------------

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
}


