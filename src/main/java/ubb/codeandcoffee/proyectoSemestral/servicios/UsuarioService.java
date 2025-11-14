package ubb.codeandcoffee.proyectoSemestral.servicios;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ubb.codeandcoffee.proyectoSemestral.modelo.Usuario;
import ubb.codeandcoffee.proyectoSemestral.repositorios.UsuarioRepository;

import java.util.ArrayList;
import java.util.Optional;

import ubb.codeandcoffee.proyectoSemestral.modelo.Estado;
import ubb.codeandcoffee.proyectoSemestral.modelo.Rol;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class UsuarioService {
    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;//necesario para lo de spring security

    

    public ArrayList<Usuario> getUsuario(){
        return(ArrayList<Usuario>) usuarioRepository.findAll();
    }

    public Usuario guardarUsuario(Usuario usuario) {
        usuario.setContraseña(passwordEncoder.encode(usuario.getContraseña()));
        usuario.setEstado(Estado.ACTIVO);
        
        // se limpia el token
        usuario.setTokenRegistro(null);
        usuario.setTokenExpiracion(null);
        
        return usuarioRepository.save(usuario);
    }

    public Optional<Usuario> getById(Integer id){
        return usuarioRepository.findById(id);
    }

    public Usuario updateById(Usuario request, Integer id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!request.getNombre().equals(usuario.getNombre())) {
            usuario.setNombre(request.getNombre());
        }

        if (request.getContraseña()!=null && !request.getContraseña().isEmpty()) {
            usuario.setContraseña(passwordEncoder.encode(request.getContraseña()));
        }

        if (!request.getCorreo().equals(usuario.getCorreo())) {
            usuario.setCorreo(request.getCorreo());
        }
        if (!request.getEstado().equals(usuario.getEstado())) {
            usuario.setEstado(request.getEstado());
        }
        if (!request.getRol().equals(usuario.getRol())) {
            usuario.setRol(request.getRol());
        }

        return usuarioRepository.save(usuario);
    }
    public Usuario crearInvitacion(String correo, Rol rol) {
        // verificar si el correo ya está registrado
        if (usuarioRepository.findByCorreo(correo).isPresent()) {
            throw new RuntimeException("El correo " + correo + " ya está registrado.");
        }
        
        // se generar token
        String token = UUID.randomUUID().toString();
        
        // usuario temporal
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setCorreo(correo);
        nuevoUsuario.setRol(rol);
        nuevoUsuario.setEstado(Estado.INICIADO); // Estado inicial
        nuevoUsuario.setTokenRegistro(token);
        nuevoUsuario.setTokenExpiracion(LocalDateTime.now().plusHours(24)); // Token válido por 24h
        
        // Se guardar sin hashear contraseña, ya que no tiene
        return usuarioRepository.save(nuevoUsuario);
    }

    public Boolean deleteUsuario(Integer id){
        try{
            usuarioRepository.deleteById(id);
            return true;
        }catch(Exception e){
            return false;
        }
    }

    
}