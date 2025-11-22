package ubb.codeandcoffee.proyectoSemestral.controladores;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import ubb.codeandcoffee.proyectoSemestral.modelo.DatoSolicitado;
import ubb.codeandcoffee.proyectoSemestral.modelo.Seccion;
import ubb.codeandcoffee.proyectoSemestral.modelo.SujetoEstudio;
import ubb.codeandcoffee.proyectoSemestral.modelo.codigo_sujeto;
import ubb.codeandcoffee.proyectoSemestral.servicios.DatoSolicitadoService;
import ubb.codeandcoffee.proyectoSemestral.servicios.SeccionService;
import ubb.codeandcoffee.proyectoSemestral.servicios.SujetoEstudioService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private SeccionService seccionService;

    @Autowired
    private SujetoEstudioService sujetoEstudioService;

    @Autowired
    private DatoSolicitadoService datoSolicitadoService;

    @GetMapping("/crear-usuario")
    public String mostrarCrearUsuario() {
        //templates/crearUsuario.html
        return "crearUsuario";
    }

    //habria que hacerlo con el codigo del paciente CAMBIAR MAS TARDEEE
    @GetMapping("/editar/{tipo}{id}")
    public String mostrarOpcionesActualizar(@PathVariable("id") String id,@PathVariable("tipo") String tipo,  Model modelo) {

        SujetoEstudio sujetoEncontrado = sujetoEstudioService.findByCompuesta(id, tipo);

        modelo.addAttribute("sujeto", sujetoEncontrado);

        return "form/menu_actualizar_sujeto";
    }

    @GetMapping("/sujetos")
    public String mostrarSujetos( Model modelo) {

        ArrayList<SujetoEstudio> sujetosEstudio = sujetoEstudioService.getSujetoEstudio();


        modelo.addAttribute("sujetos", sujetosEstudio);

        return "form/lista_sujetos";
    }

    @GetMapping("/gestionar-usuarios")
    public String mostrarGestionarUsuarios() {
        //templates/gestionarUsuarios.html
        return "gestionarUsuarios";
    }

    @GetMapping("/crear-formulario")
    public String mostrarCrearFormulario(Model modelo) {
        //templates/form/admin_formulario_dashboard.html
        List<Seccion> listaDeSecciones = seccionService.getAllSecciones();

        modelo.addAttribute("secciones", listaDeSecciones);
        return "form/admin_formulario_dashboard";
    }

    @GetMapping("/crear-seccion")
    public String mostrarCrearSeccion(Model modelo) {
        //templates/form/crear_seccion.html
        modelo.addAttribute("seccion", new Seccion());
        return "form/crear_seccion";
    }

    @PostMapping("/seccion/guardar")
    public String guardarSeccion(@ModelAttribute("seccion") Seccion seccion) {
        seccionService.guardarSeccion(seccion);
        return "redirect:/admin/crear-formulario";
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
        return "redirect:/admin/crear-formulario";
    }
}