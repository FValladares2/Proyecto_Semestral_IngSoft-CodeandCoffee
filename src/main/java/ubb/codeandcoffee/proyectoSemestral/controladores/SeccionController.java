package ubb.codeandcoffee.proyectoSemestral.controladores;

import java.util.ArrayList;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ubb.codeandcoffee.proyectoSemestral.modelo.Seccion;
import ubb.codeandcoffee.proyectoSemestral.servicios.SeccionService;

import org.springframework.web.bind.annotation.PutMapping;

@RestController //Indica que esta clase es un controlador REST y sus métodos devuelven JSON
@RequestMapping("/Seccion")//Define la ruta base para todos los endpoints de esta clase
public class SeccionController {
    @Autowired
    private SeccionService seccionService;//instancia del servicio de seccion


    @GetMapping // Endpoint GET: obtiene todas las secciones
    public ArrayList<Seccion> getSeccion(){
        return this.seccionService.getSeccion(); //Llama al servicio get
    }

    @PostMapping //Endpoint POST: crea una nueva seccion
    public Seccion guardarSeccion(@RequestBody Seccion seccion){
        // @RequestBody: JSON de la solicitud se convierte a Seccion
        return this.seccionService.guardarSeccion(seccion);
    }
    
    @GetMapping(path = "{id}") //Endpoint GET: obtiene una seccion por su ID
    public Optional<Seccion> getSeccionById(@PathVariable("id") Integer id_seccion){
        return this.seccionService.getById(id_seccion);
    }

    @PutMapping(path = "{id}") //"\{id}" //Endpoint PUT: actualizar una seccion por su ID
    public Seccion updateSeccionById(@RequestBody Seccion request, @PathVariable("id") Integer id_seccion){
        return this.seccionService.updateById(request, id_seccion);
    }

    @PatchMapping(path = "{id}") //"\{id}" // Endpoint PATCH: actualiza parcialmente una seccion por su ID
    public Seccion updateParcialById(@RequestBody Seccion request, @PathVariable("id") Integer id_seccion){
        return this.seccionService.updateById(request, id_seccion);
    }
    
    @DeleteMapping(path = "{id}") // Endpoint DELETE: elimina una seccion por su ID
    public String deleteSeccionById(@PathVariable("id") Integer id_seccion){
        boolean ok = this.seccionService.deleteSeccion(id_seccion);
        if(ok){
            return "la seccion con id: "+ id_seccion +"fue eliminado exitosamente";
        }
        return "Error, no fue posible eliminar la seccion con id: " + id_seccion;
    }
}
