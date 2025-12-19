package ubb.codeandcoffee.proyectoSemestral.controladores;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController implements ErrorController {
    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {

        Object status = request.getAttribute("jakarta.servlet.error.status_code");

        int statusCode = 500;

        if (status != null) {
            statusCode = Integer.parseInt(status.toString());
        }

        model.addAttribute("status", statusCode);

        if (statusCode == 403) {
            return "403";
        }
        if (statusCode == 404) {
            return "404";
        }

        return "error";
    }
}
