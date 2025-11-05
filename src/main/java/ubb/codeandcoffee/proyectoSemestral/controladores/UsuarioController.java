package ubb.codeandcoffee.proyectoSemestral.controladores;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ubb.codeandcoffee.proyectoSemestral.modelo.Usuario;
import ubb.codeandcoffee.proyectoSemestral.modelo.codigo_sujeto;
import ubb.codeandcoffee.proyectoSemestral.servicios.SujetoEstudioService;
import ubb.codeandcoffee.proyectoSemestral.servicios.UsuarioService;

import java.util.ArrayList;
import java.util.Optional;

@RestController
@RequestMapping("/Usuario")
public class UsuarioController {
    @Autowired
    private UsuarioService usuarioService;


    @GetMapping //endpoint GET
    public ArrayList<Usuario> getUsuario(){
        return this.usuarioService.getUsuario();
    }

    @PostMapping //Endpoint POST
    public Usuario guardarUsuario(@RequestBody Usuario sujeto){
        return this.usuarioService.guardarUsuario(sujeto);
    }

    @GetMapping(path = "{id}") //Endpoint GET
    public Optional<Usuario> getUsuarioById(@PathVariable("id") Integer id){
        return this.usuarioService.getById(id);
    }

    @PutMapping(path = "{id}") //Endpoint PUT
    public Usuario updateUsuarioById(@RequestBody Usuario request, @PathVariable("id") Integer id){
        return this.usuarioService.updateById(request, id);
    }

    @PatchMapping(path = "{id}") //Endpoint PATCH
    public Usuario updateParcialById(@RequestBody Usuario request, @PathVariable("id") Integer id){
        return this.usuarioService.updateById(request, id);
    }

    @DeleteMapping(path = "{id}") //Endpoint DELETE
    public String deleteUsuarioById(@PathVariable("id") Integer id){
        boolean ok = this.usuarioService.deleteUsuario(id);
        if(ok){
            return "El Usuario con id: "+ id +" fue eliminado exitosamente";
        }
        return "Error, no fue posible eliminar el Usuario con id: " + id;
    }
}
