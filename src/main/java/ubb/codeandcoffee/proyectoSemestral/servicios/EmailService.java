package ubb.codeandcoffee.proyectoSemestral.servicios;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void enviarEmailInvitacion(String toEmail, String token) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("tu-correo@gmail.com"); // El mismo que configuraste
            message.setTo(toEmail);
            message.setSubject("¡Has sido invitado al proyecto!");
            
            // Este es el enlace que el usuario recibirá
            String urlBase = "http://localhost:8085"; // El puerto de tu app
            String link = urlBase + "/completar-registro?token=" + token;

            String texto = "¡Hola!\n\nHas sido invitado a unirte al sistema.\n\n"
                         + "Por favor, completa tu registro haciendo clic en el siguiente enlace:\n"
                         + link + "\n\n"
                         + "El enlace expirará en 24 horas.\n\n"
                         + "Saludos,\nEl equipo de Code & Coffee.";
            
            message.setText(texto);
            mailSender.send(message);
            
            System.out.println("Correo de invitación enviado a " + toEmail);
        } catch (Exception e) {
            System.err.println("Error al enviar el correo: " + e.getMessage());
            // Aquí podrías lanzar una excepción personalizada si lo prefieres
        }
    }
}