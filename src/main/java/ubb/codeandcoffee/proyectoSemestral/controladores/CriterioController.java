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

import ubb.codeandcoffee.proyectoSemestral.modelo.Criterio;
import ubb.codeandcoffee.proyectoSemestral.servicios.CriterioService;

import org.springframework.web.bind.annotation.PutMapping;

@RestController //se usa para indicar que esta clase es un controlador REST y sus métodos devuelven JSON
@RequestMapping("/Criterio")//define la ruta base para todos los endpoints de esta clase
public class CriterioController {
    @Autowired
    private CriterioService criterioService;//instancia del servicio de criterio


    @GetMapping //Endpoint GET: obtiene todos los criterios
    public ArrayList<Criterio> getCriterios(){
        return this.criterioService.getCriterios(); //Llama al servicio get
    }

    @PostMapping //Endpoint POST: crea un nuevo criterio
    public Criterio guardarCriterio(@RequestBody Criterio criterio){
        // @RequestBody: JSON de la solicitud se convierte a Criterio
        return this.criterioService.guardarCriterio(criterio);
    }

    @GetMapping(path = "{id}")//Endpoint GET: obtiene un criterio por su ID
    public Optional<Criterio> getCriterioById(@PathVariable("id") Integer id_criterio){
        return this.criterioService.getById(id_criterio);
    }

    @PutMapping(path = "{id}")//Endpoint PUT: actualizar un criterio por su ID
    public Criterio updateCriterioById(@RequestBody Criterio request, @PathVariable("id") Integer id_criterio){
        return this.criterioService.updateById(request, id_criterio);
    }

    @PatchMapping(path = "{id}")//Endpoint PATCH: actualiza parcialmente un criterio por su ID
    public Criterio updateParcialById(@RequestBody Criterio request, @PathVariable("id") Integer id_criterio){
        return this.criterioService.updateById(request, id_criterio);
    }

    @DeleteMapping(path = "{id}")//Endpoint DELETE: elimina un criterio por su ID
    public String deleteCriterioById(@PathVariable("id") Integer id_criterio){
        boolean ok = this.criterioService.deleteCriterio(id_criterio);
        if(ok){
            return "El criterio con id: "+ id_criterio +" fue eliminado exitosamente";
        }
        return "Error, no fue posible eliminar el criterio con id: " + id_criterio;
    }
}