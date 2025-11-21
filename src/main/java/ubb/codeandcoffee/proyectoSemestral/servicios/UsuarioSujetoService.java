package ubb.codeandcoffee.proyectoSemestral.servicios;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ubb.codeandcoffee.proyectoSemestral.modelo.Usuario;
import ubb.codeandcoffee.proyectoSemestral.modelo.Usuario_Sujeto;
import ubb.codeandcoffee.proyectoSemestral.modelo.usuario_sujeto_id;
import ubb.codeandcoffee.proyectoSemestral.repositorios.UsuarioRepository;
import ubb.codeandcoffee.proyectoSemestral.repositorios.UsuarioSujetoRepository;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class UsuarioSujetoService {
    @Autowired
    UsuarioSujetoRepository usuarioSujetoRepository;

    @Autowired
    UsuarioRepository usuarioRepository;

    public ArrayList<Usuario_Sujeto> getUsuarioSujeto(){
        return(ArrayList<Usuario_Sujeto>) usuarioSujetoRepository.findAll();
    }

    public ArrayList<Usuario_Sujeto> getAllId0(){
        return usuarioSujetoRepository.findAllId0();
    }

    public Usuario_Sujeto guardarUsuarioSujeto(Usuario_Sujeto sujeto) {
        usuarioSujetoRepository.save(sujeto);
        return usuarioSujetoRepository.findByQuery(sujeto.getUsuario(), sujeto.getSujetoEstudio());
    }

    public Optional<Usuario_Sujeto> getById(usuario_sujeto_id id){
        return usuarioSujetoRepository.findById(id);
    }

    public Boolean setLatestChangesAsUsuario() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String correo = "correo@ubiobio.cl"; //por ahora
        // TODO: fix autenticación de sesión actual
        //  String correo = auth.getPrincipal(); //auth.getName()? asumo que principal = username = correo del usuario
        //  System.out.println("correo: " + correo); //con ambos métodos, entrega un anonimousUser. Puede ser por la config de spring security
        Optional<Usuario> query = usuarioRepository.findByCorreo(correo);

        Usuario usuario;
        if (query.isPresent()) usuario = query.get();
        else return false;

        ArrayList<Usuario_Sujeto> cambios = getAllId0();

        if (cambios.isEmpty()) return false;
        while (!cambios.isEmpty()) {
            Usuario_Sujeto cambio = cambios.removeFirst();
            cambio.setUsuario(usuario);
            usuarioSujetoRepository.save(cambio);
        }
        return true;
    }

    public Usuario_Sujeto updateById(Usuario_Sujeto request, usuario_sujeto_id id) {
        Usuario_Sujeto sujeto = usuarioSujetoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sujeto no encontrado"));

        if (!request.getUsuario().equals(sujeto.getUsuario())) {
            sujeto.setUsuario(request.getUsuario());
        }
        if (!request.getSujetoEstudio().equals(sujeto.getSujetoEstudio())) {
            sujeto.setSujetoEstudio(request.getSujetoEstudio());
        }
        if (!request.getAccion().equals(sujeto.getAccion())) {
            sujeto.setAccion(request.getAccion());
        }

        return usuarioSujetoRepository.save(sujeto);
    }

    public Boolean deleteUsuarioSujeto(usuario_sujeto_id id){
        try{
            usuarioSujetoRepository.deleteById(id);
            return true;
        }catch(Exception e){
            return false;
        }
    }
}