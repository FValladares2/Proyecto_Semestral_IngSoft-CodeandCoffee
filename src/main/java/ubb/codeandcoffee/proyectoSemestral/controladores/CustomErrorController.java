package ubb.codeandcoffee.proyectoSemestral.controladores;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;


// controlador personalizado para manejar errores HTTP
@Controller
public class CustomErrorController implements ErrorController {
    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {

        Object status = request.getAttribute("jakarta.servlet.error.status_code"); // obtener el código de estado HTTP

        int statusCode = 500;

        if (status != null) {
            statusCode = Integer.parseInt(status.toString()); 
        }

        model.addAttribute("status", statusCode); // agregar el código de estado al modelo para usar en la vista


        // redirigir a páginas de error específicas según el código de estado
        if (statusCode == 403) {
            return "403";
        }
        if (statusCode == 404) {
            return "404";
        }

        return "error";
    }
}
