package ubb.codeandcoffee.proyectoSemestral.controladores;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import ubb.codeandcoffee.proyectoSemestral.modelo.Usuario;
import ubb.codeandcoffee.proyectoSemestral.repositorios.UsuarioRepository;
import ubb.codeandcoffee.proyectoSemestral.servicios.UsuarioService;

import java.time.LocalDateTime;
import java.util.Optional;


// Controlador para manejar el login y registro de usuarios
@Controller
public class LoginController {

    // nuevas dependencias 
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/login")
    public String verPaginaDeLogin() {
        return "login";
    }

    @GetMapping("/")
    public String redirigirALogin() {
        return "redirect:/login";
    }

    @GetMapping("/crear-usuario")
    public String verCrearUsuario() {
        return "crearUsuario";
    }
    

    
    // se encarga de mostrar el formulario para completar el registro
    @GetMapping("/completar-registro")
    public String verCompletarRegistro(
            @RequestParam(name = "token", required = false) String token,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (token == null || token.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Token de registro no proporcionado.");
            return "redirect:/login";
        }

        Optional<Usuario> usuarioOpt = usuarioRepository.findByTokenRegistro(token);

        if (usuarioOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "El token de invitación es inválido.");
            return "redirect:/login";
        }

        // Validar expiración
        Usuario usuario = usuarioOpt.get();
        if (usuario.getTokenExpiracion().isBefore(LocalDateTime.now())) {
            redirectAttributes.addFlashAttribute("error", "El token de invitación ha expirado.");
            
            // para mas adelante, se puede borrar el usuario de la BD aqui
            return "redirect:/login";
        }

        // el Token es valido, pasamos los datos a la vista
        model.addAttribute("token", token);
        model.addAttribute("correo", usuario.getCorreo());
        
        return "completar_registro";
    }
    // procesa el formulario para completar el registro
    @PostMapping("/completar-registro")
    public String procesarCompletarRegistro(
            @RequestParam("token") String token,
            @RequestParam("nombre") String nombre,
            @RequestParam("contrasena") String contrasena,
            @RequestParam("confirmar") String confirmar,
            RedirectAttributes redirectAttributes) {

        if (!contrasena.equals(confirmar)) {
            redirectAttributes.addFlashAttribute("error", "Las contraseñas no coinciden.");
            // se re-envia el token para que el GET no falle
            return "redirect:/completar-registro?token=" + token;
        }

        try {
            // se llama al servicio para completar el registro
            usuarioService.completarRegistro(token, nombre, contrasena);

            //  exito
            redirectAttributes.addFlashAttribute("exito", "¡Registro completado! Ahora puedes iniciar sesión.");
            return "redirect:/login";

        } catch (RuntimeException e) {
            //  error (token expirado o inválido)
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/login";
        }
    }


}
