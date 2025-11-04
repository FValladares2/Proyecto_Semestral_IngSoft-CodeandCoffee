package ubb.codeandcoffee.proyectoSemestral.servicios;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ubb.codeandcoffee.proyectoSemestral.modelo.Usuario_Sujeto;
import ubb.codeandcoffee.proyectoSemestral.modelo.usuario_sujeto_id;
import ubb.codeandcoffee.proyectoSemestral.repositorios.UsuarioSujetoRepository;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class UsuarioSujetoService {
    @Autowired
    UsuarioSujetoRepository usuarioSujetoRepository;

    public ArrayList<Usuario_Sujeto> getUsuarioSujeto(){
        return(ArrayList<Usuario_Sujeto>) usuarioSujetoRepository.findAll();
    }

    public Usuario_Sujeto guardarUsuarioSujeto(Usuario_Sujeto sujeto) {
        return usuarioSujetoRepository.save(sujeto);
    }

    public Optional<Usuario_Sujeto> getById(usuario_sujeto_id id){
        return usuarioSujetoRepository.findById(id);
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