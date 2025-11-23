package ubb.codeandcoffee.proyectoSemestral.controladores;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ubb.codeandcoffee.proyectoSemestral.modelo.SujetoEstudio;
import ubb.codeandcoffee.proyectoSemestral.modelo.Usuario_Sujeto;
import ubb.codeandcoffee.proyectoSemestral.modelo.codigo_sujeto;
import ubb.codeandcoffee.proyectoSemestral.modelo.usuario_sujeto_id;
import ubb.codeandcoffee.proyectoSemestral.servicios.SujetoEstudioService;
import ubb.codeandcoffee.proyectoSemestral.servicios.UsuarioSujetoService;

import java.util.ArrayList;
import java.util.Optional;

@RestController
@RequestMapping("/UsuarioSujeto")
public class UsuarioSujetoController {
    @Autowired
    private UsuarioSujetoService usuarioSujetoService;


    @GetMapping //endpoint GET
    public ArrayList<Usuario_Sujeto> getUsuarioSujeto(){
        return this.usuarioSujetoService.getUsuarioSujeto();
    }

    @PostMapping //Endpoint POST
    public Usuario_Sujeto guardarUsuarioSujeto(@RequestBody Usuario_Sujeto sujeto){
        return this.usuarioSujetoService.guardarUsuarioSujeto(sujeto);
    }

    @GetMapping(path = "{id}") //Endpoint GET
    public Optional<Usuario_Sujeto> getUsuarioSujetoById(@PathVariable("id") Integer id){
        return this.usuarioSujetoService.getById(id);
    }

    @PutMapping(path = "{id}") //Endpoint PUT
    public Usuario_Sujeto updateUsuarioSujetoById(@RequestBody Usuario_Sujeto request, @PathVariable("id") Integer id){
        return this.usuarioSujetoService.updateById(request, id);
    }

    @PatchMapping(path = "{id}") //Endpoint PATCH
    public Usuario_Sujeto updateParcialById(@RequestBody Usuario_Sujeto request, @PathVariable("id") Integer id){
        return this.usuarioSujetoService.updateById(request, id);
    }

    @DeleteMapping(path = "{id}") //Endpoint DELETE
    public String deleteUsuarioSujetoById(@PathVariable("id") Integer id){
        boolean ok = this.usuarioSujetoService.deleteUsuarioSujeto(id);
        if(ok){
            return "El usuj con id: "+ id +" fue eliminado exitosamente";
        }
        return "Error, no fue posible eliminar el usuj con id: " + id;
    }
}