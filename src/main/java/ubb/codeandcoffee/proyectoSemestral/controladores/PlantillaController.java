package ubb.codeandcoffee.proyectoSemestral.controladores;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class PlantillaController {

    @ModelAttribute("plantilla")
    public String definirPlantillaBase(Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return "/plantilla_admin"; //aqui deberia redirigir al login, pero como el security aun no lo implementamos
            //lo dejare asi
            //TODO CAMBIAR MAS TARDE POR LOGIN
        }

        var autoridades = authentication.getAuthorities();

        if (autoridades.stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMINISTRADOR")
                || a.getAuthority().equals("ADMINISTRADOR"))) {
            return "/plantilla_admin";
        }

        if (autoridades.stream().anyMatch(a -> a.getAuthority().equals("ROLE_RECOLECTOR_DE_DATOS")
                || a.getAuthority().equals("RECOLECTOR_DE_DATOS"))) {
            return "/plantilla_recolector";
        }

        if (autoridades.stream().anyMatch(a -> a.getAuthority().equals("ROLE_ANALISTA")
                || a.getAuthority().equals("ANALISTA"))) {
            return "/plantilla_analista";
        }

        return "/plantilla_admin";
    }
}