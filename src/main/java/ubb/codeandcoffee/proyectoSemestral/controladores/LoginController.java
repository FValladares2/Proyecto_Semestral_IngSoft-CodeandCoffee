package ubb.codeandcoffee.proyectoSemestral.controladores;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

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

    @GetMapping("/completar-registro")
    public String verCompletarRegistro() {
        return "completar_registro";
    }
}
