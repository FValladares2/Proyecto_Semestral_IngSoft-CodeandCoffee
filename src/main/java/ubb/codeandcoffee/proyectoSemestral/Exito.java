package ubb.codeandcoffee.proyectoSemestral;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.util.*;
import java.io.IOException;

@Component
public class Exito implements AuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        String targetUrl = "/"; //url por defecto

        for (GrantedAuthority authority : authorities) {
            String role = authority.getAuthority();

            if (role.equals("ROLE_ADMINISTRADOR")) {
                targetUrl = "/menu/admin";
                break;
            } else if (role.equals("ROLE_ANALISTA")) {
                targetUrl = "/menu/analista";
                break;
            } else if (role.equals("ROLE_RECOLECTOR_DE_DATOS")) {
                targetUrl = "/menu/recolector";
                break;
            }
        }

        response.sendRedirect(request.getContextPath() + targetUrl);
    }
}
