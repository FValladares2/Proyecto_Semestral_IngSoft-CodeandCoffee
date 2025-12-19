package ubb.codeandcoffee.proyectoSemestral.controladores;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


// Controlador para manejar la visualización de menús según el rol del usuario
@Controller
public class MenuController {

    @GetMapping("/menu/admin")
    public String mostrarMenuAdmin() {
        return "menuUsers/menuAdministrador";
    }

    @GetMapping("/menu/analista")
    public String mostrarMenuAnalista() {
        return "menuUsers/menuAnalista";
    }

    @GetMapping("/menu/recolector")
    public String mostrarMenuRecolector() {
        return "menuUsers/menuRecolector";
    }
}
