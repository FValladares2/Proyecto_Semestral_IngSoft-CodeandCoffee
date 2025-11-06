package ubb.codeandcoffee.proyectoSemestral.servicios;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ubb.codeandcoffee.proyectoSemestral.modelo.Usuario;
import ubb.codeandcoffee.proyectoSemestral.repositorios.UsuarioRepository;

import java.util.ArrayList;
import java.util.Optional;

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

    public Boolean deleteUsuario(Integer id){
        try{
            usuarioRepository.deleteById(id);
            return true;
        }catch(Exception e){
            return false;
        }
    }
}