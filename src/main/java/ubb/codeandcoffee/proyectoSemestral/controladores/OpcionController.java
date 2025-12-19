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

import ubb.codeandcoffee.proyectoSemestral.modelo.Opcion;
import ubb.codeandcoffee.proyectoSemestral.servicios.OpcionService;

import org.springframework.web.bind.annotation.PutMapping;

@RestController //indica que esta clase es un controlador REST y sus métodos devuelven JSON
@RequestMapping("/Opcion")//define la ruta base para todos los endpoints de esta clase
public class OpcionController {
    @Autowired
    private OpcionService opcionService;//instancia del servicio de opcion


    @GetMapping // Endpoint GET: obtiene todas las opciones
    public ArrayList<Opcion> getOpciones(){
        return this.opcionService.getOpciones(); //Llama al servicio get
    }

    @PostMapping //Endpoint POST: crea una nueva opcion
    public Opcion guardarOpcion(@RequestBody Opcion opcion){
        // @RequestBody: JSON de la solicitud se convierte a Opcion
        return this.opcionService.guardarOpcion(opcion);
    }
    
    @GetMapping(path = "{id}") //Endpoint GET: obtiene una opcion por su ID
    public Optional<Opcion> getOpcionById(@PathVariable("id") Integer id_opcion){
        return this.opcionService.getById(id_opcion);
    }

    @PutMapping(path = "{id}") //"\{id}" //Endpoint PUT: actualizar una opcion por su ID
    public Opcion updateOpcionById(@RequestBody Opcion request, @PathVariable("id") Integer id_opcion){
        return this.opcionService.updateById(request, id_opcion);
    }

    @PatchMapping(path = "{id}") //"\{id}" // Endpoint PATCH: actualiza parcialmente una opcion por su ID
    public Opcion updateParcialById(@RequestBody Opcion request, @PathVariable("id") Integer id_opcion){
        return this.opcionService.updateById(request, id_opcion);
    }
    
    @DeleteMapping(path = "{id}") // Endpoint DELETE: elimina una opcion por su ID
    public String deleteOpcionById(@PathVariable("id") Integer id_opcion){
        boolean ok = this.opcionService.deleteOpcion(id_opcion);
        if(ok){
            return "la opcion con id: "+ id_opcion +"fue eliminado exitosamente";
        }
        return "Error, no fue posible eliminar la opcion con id: " + id_opcion;
    }
}
