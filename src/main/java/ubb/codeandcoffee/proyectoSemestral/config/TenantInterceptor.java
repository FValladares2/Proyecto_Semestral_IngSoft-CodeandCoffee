package ubb.codeandcoffee.proyectoSemestral.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
public class TenantInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        HttpSession session = request.getSession();
        String currentStudyDb = (String) session.getAttribute("CURRENT_STUDY_DB");

        if (currentStudyDb != null && !currentStudyDb.isEmpty()) {

            // le decimos al hilo actual: "Usa la conexión de ESTA base de datos"
            DBContextHolder.setCurrentDb(currentStudyDb);
        } else {

            // Si no hay nada en la sesión, nos aseguramos de usar la base principal
            // (Esto evita que un hilo reutilizado se quede "pegado" en la DB anterior)
            DBContextHolder.clear(); 
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        // ? podríamos inyectar variables globales a la vista aquí si quisieramos
        // ? por ejemplo, el nombre del estudio actual
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        //! LIMPIEZA OBLIGATORIA
        // una vez terminada la petición, borramos el contexto del hilo para evitar fugas de memoria
        DBContextHolder.clear();
    }
}