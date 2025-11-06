package ubb.codeandcoffee.proyectoSemestral.controladores;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {
    //al ingresar como adminen el login, opciones. Falta el html para gestionar roles de usuarios existentes:

    @GetMapping("/crear-usuario")
    public String mostrarCrearUsuario() {
        //templates/crearUsuario.html
        return "crearUsuario";
    }

    @GetMapping("/gestionar-usuarios")
    public String mostrarGestionarUsuarios() {
        //templates/gestionarUsuarios.html
        return "gestionarUsuarios";
    }

    @GetMapping("/crear-formulario")
    public String mostrarCrearFormulario() {
        //templates/form/admin_formulario_dashboard.html
        return "form/admin_formulario_dashboard";
    }
}