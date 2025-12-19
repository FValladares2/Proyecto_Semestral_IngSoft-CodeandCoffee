package ubb.codeandcoffee.proyectoSemestral.controladores;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;


// Controlador de asesoramiento para definir plantillas y páginas de inicio según el rol del usuario
@ControllerAdvice
public class PlantillaController {

    @ModelAttribute("plantilla")
    public String definirPlantillaBase(Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return "login";
        }

        var autoridades = authentication.getAuthorities();

        if (autoridades.stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMINISTRADOR")
                || a.getAuthority().equals("ADMINISTRADOR"))) {
            return "plantilla_admin";
        }

        if (autoridades.stream().anyMatch(a -> a.getAuthority().equals("ROLE_RECOLECTOR_DE_DATOS")
                || a.getAuthority().equals("RECOLECTOR_DE_DATOS"))) {
            return "plantilla_recolector";
        }

        if (autoridades.stream().anyMatch(a -> a.getAuthority().equals("ROLE_ANALISTA")
                || a.getAuthority().equals("ANALISTA"))) {
            return "plantilla_analista";
        }

        return "plantilla_admin";
    }

    // define la página de inicio según el rol del usuario
    @ModelAttribute("inicio")
    public String definirInicio(Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return "login";
        }

        var autoridades = authentication.getAuthorities();

        if (autoridades.stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMINISTRADOR"))) {
            return "menu/admin";
        }

        if (autoridades.stream().anyMatch(a -> a.getAuthority().equals("ROLE_RECOLECTOR_DE_DATOS"))) {
            return "menu/recolector";
        }

        if (autoridades.stream().anyMatch(a -> a.getAuthority().equals("ROLE_ANALISTA"))) {
            return "menu/analista";
        }

        return "login";
    }
}