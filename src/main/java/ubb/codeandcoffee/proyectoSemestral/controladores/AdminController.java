package ubb.codeandcoffee.proyectoSemestral.controladores;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ubb.codeandcoffee.proyectoSemestral.modelo.Rol;
import ubb.codeandcoffee.proyectoSemestral.modelo.Usuario;
import ubb.codeandcoffee.proyectoSemestral.servicios.EmailService;
import ubb.codeandcoffee.proyectoSemestral.servicios.UsuarioService;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ubb.codeandcoffee.proyectoSemestral.modelo.DatoSolicitado;
import ubb.codeandcoffee.proyectoSemestral.modelo.Seccion;
import ubb.codeandcoffee.proyectoSemestral.modelo.SujetoEstudio;
import ubb.codeandcoffee.proyectoSemestral.modelo.codigo_sujeto;
import ubb.codeandcoffee.proyectoSemestral.repositorios.SujetoEstudioRepository;
import ubb.codeandcoffee.proyectoSemestral.servicios.DatoSolicitadoService;
import ubb.codeandcoffee.proyectoSemestral.servicios.SeccionService;
import ubb.codeandcoffee.proyectoSemestral.servicios.SujetoEstudioService;

import java.util.ArrayList;
import java.util.List;

import ubb.codeandcoffee.proyectoSemestral.modelo.Estado; 


@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private SeccionService seccionService;

    @Autowired
    private SujetoEstudioRepository repo;

    @Autowired
    private SujetoEstudioService sujetoEstudioService;

    @Autowired
    private DatoSolicitadoService datoSolicitadoService;

    // nuevas inyecciones para el servicio de usuarios y email
    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private EmailService emailService;

    @GetMapping("/crear-usuario")
    public String mostrarCrearUsuario() {
        //templates/crearUsuario.html
        return "crearUsuario";
    }


    @GetMapping("/gestionar-usuarios")
    public String verGestionarUsuarios(Model model) {

        List<Usuario> usuarios = usuarioService.listarTodos();
        
        model.addAttribute("listaUsuarios", usuarios);
        
        return "gestionar_usuarios";
    }
/*
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
    // nuevo metodo para procesar la creacion de usuarios con invitación por email*/
    @PostMapping("/crear-usuario")
    public String procesarCrearUsuario(
            @RequestParam("correo") String correo,
            @RequestParam("rol") String rolString,
            RedirectAttributes redirectAttributes) {
        
        try {
            //  se convierte el String del rol a un Enum.
            Rol rol = Rol.valueOf(rolString.toUpperCase().replace(" ", "_"));
            
            // se crea la invitación, en donde se guarda en BD con token y estado INICIADO
            Usuario usuarioInvitado = usuarioService.crearInvitacion(correo, rol);
            
            // se usa para enviar el correo con el token
            emailService.enviarEmailInvitacion(correo, usuarioInvitado.getTokenRegistro());

            redirectAttributes.addFlashAttribute("exito", "¡Invitación enviada correctamente a " + correo + "!");
        
        } catch (IllegalArgumentException e) {
            // Error si el Rol no existe
            redirectAttributes.addFlashAttribute("error", "El rol seleccionado no es válido.");
        } catch (RuntimeException e) {
            // Error si el correo ya existe
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            // Error general (ej. no se pudo enviar el email)
            redirectAttributes.addFlashAttribute("error", "Error al procesar la invitación: " + e.getMessage());
        }

        return "redirect:/admin/crear-usuario";
    }
    
    @PostMapping("/actualizar-estado")
    public String cambiarEstadoUsuario(
            @RequestParam("idUsuario") int idUsuario,
            @RequestParam("nuevoEstado") Estado nuevoEstado,
            RedirectAttributes redirectAttributes) {
        
        try {
            usuarioService.actualizarEstado(idUsuario, nuevoEstado);
            redirectAttributes.addFlashAttribute("exito", "Estado actualizado correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar estado: " + e.getMessage());
        }

        // Redirigimos a la misma página para ver los cambios
        return "redirect:/admin/gestionar-usuarios";
    }
}