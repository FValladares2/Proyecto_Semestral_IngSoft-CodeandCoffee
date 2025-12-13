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

    public ArrayList<Usuario_Sujeto> getAllNull(){
        return usuarioSujetoRepository.findAllNull();
    }

    public Usuario_Sujeto guardarUsuarioSujeto(Usuario_Sujeto sujeto) {
        usuarioSujetoRepository.save(sujeto);
        return usuarioSujetoRepository.findByQuery(sujeto.getUsuario(), sujeto.getSujetoEstudio());
    }

    public Optional<Usuario_Sujeto> getById(Integer id){
        return usuarioSujetoRepository.findById(id);
    }

    public Boolean setLatestChangesAsUsuario() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        //String correo = "admin@correo.cl"; //para testearlo con postman / security .permitAll()
        String correo = auth.getName(); //Me funciona testeandolo via web, por postman me entrega la pantalla inicio

        Optional<Usuario> query = usuarioRepository.findByCorreo(correo);

        Usuario usuario;
        if (query.isPresent()) usuario = query.get();
        else return false;

        ArrayList<Usuario_Sujeto> cambios = getAllNull();

        if (cambios.isEmpty()) return false;

        cambios.forEach(c -> c.setUsuario(usuario));
        usuarioSujetoRepository.saveAll(cambios);

        /* codigo anterior, por si algo no funciona con el de arriba
        while (!cambios.isEmpty()) {
            Usuario_Sujeto cambio = cambios.removeFirst();
            cambio.setUsuario(usuario);
            usuarioRepository.save(usuario);
        }
        */
        return true;
    }

    public Usuario_Sujeto updateById(Usuario_Sujeto request, Integer id) {
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

    public Boolean deleteUsuarioSujeto(Integer id){
        try{
            usuarioSujetoRepository.deleteById(id);
            return true;
        }catch(Exception e){
            return false;
        }
    }
}