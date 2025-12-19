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

import ubb.codeandcoffee.proyectoSemestral.modelo.DatoSolicitado;
import ubb.codeandcoffee.proyectoSemestral.servicios.DatoSolicitadoService;

import org.springframework.web.bind.annotation.PutMapping;

@RestController //se usa para indicar que esta clase es un controlador REST y sus métodos devuelven JSON
@RequestMapping("/DatoSolicitado")//define la ruta base para todos los endpoints de esta clase
public class DatoSolicitadoController {
    @Autowired
    private DatoSolicitadoService datoService;//instancia del servicio de datos solicitados


    @GetMapping // Endpoint GET: obtiene todos los datos
    public ArrayList<DatoSolicitado> getDatoSolicitados(){
        return this.datoService.getDatoSolicitados(); //Llama al servicio get
    }

    @PostMapping //Endpoint POST: crea un nuevo dato
    public DatoSolicitado guardarDatoSolicitado(@RequestBody DatoSolicitado dato){
        // @RequestBody: JSON de la solicitud se convierte a DatoSolicitado
        return this.datoService.guardarDatoSolicitado(dato);
    }
    
    @GetMapping(path = "{id}") //Endpoint GET: obtiene un dato por su ID
    public Optional<DatoSolicitado> getDatoById(@PathVariable("id") Integer id_dato){
        return this.datoService.getById(id_dato);
    }

    @PutMapping(path = "{id}") //"\{id}" //Endpoint PUT: actualizar un dato por su ID
    public DatoSolicitado updateDatoById(@RequestBody DatoSolicitado request, @PathVariable("id") Integer id_dato){
        return this.datoService.updateById(request, id_dato);
    }

    @PatchMapping(path = "{id}") //"\{id}" // Endpoint PATCH: actualiza parcialmente un dato por su ID
    public DatoSolicitado updateParcialById(@RequestBody DatoSolicitado request, @PathVariable("id") Integer id_dato){
        return this.datoService.updateById(request, id_dato);
    }
    
    @DeleteMapping(path = "{id}") // Endpoint DELETE: elimina un dato por su ID
    public String deleteDatoById(@PathVariable("id") Integer id_dato){
        boolean ok = this.datoService.deleteDatoSolicitado(id_dato);
        if(ok){
            return "el dato solicitado con id: "+ id_dato +"fue eliminado exitosamente";
        }
        return "Error, no fue posible eliminar al dato solicitado con id: " + id_dato;
    }
}
