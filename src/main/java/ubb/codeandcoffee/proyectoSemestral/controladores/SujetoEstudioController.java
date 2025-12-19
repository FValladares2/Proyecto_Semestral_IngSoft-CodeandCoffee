package ubb.codeandcoffee.proyectoSemestral.controladores;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ubb.codeandcoffee.proyectoSemestral.modelo.codigo_sujeto;
import ubb.codeandcoffee.proyectoSemestral.modelo.SujetoEstudio;
import ubb.codeandcoffee.proyectoSemestral.servicios.AntecedenteService;
import ubb.codeandcoffee.proyectoSemestral.servicios.SujetoEstudioService;

import java.util.ArrayList;
import java.util.Optional;

// Controlador para manejar las operaciones CRUD de SujetoEstudio
@RestController
@RequestMapping("/SujetoEstudio")
public class SujetoEstudioController {
    @Autowired
    private SujetoEstudioService sujetoEstudioService;


    @GetMapping //endpoint GET
    public ArrayList<SujetoEstudio> getSujetoEstudio(){
        return this.sujetoEstudioService.getSujetoEstudio();
    }

    @PostMapping //Endpoint POST
    public SujetoEstudio guardarSujetoEstudio(@RequestBody SujetoEstudio sujeto){
        return this.sujetoEstudioService.guardarSujetoEstudio(sujeto);
    }

    @GetMapping(path = "{id}") //Endpoint GET
    public Optional<SujetoEstudio> getSujetoEstudioById(@PathVariable("id") codigo_sujeto id){
        return this.sujetoEstudioService.getById(id);
    }

    @PutMapping(path = "{id}") //Endpoint PUT
    public SujetoEstudio updateSujetoEstudioById(@RequestBody SujetoEstudio request, @PathVariable("id") codigo_sujeto id){
        return this.sujetoEstudioService.updateById(request, id);
    }

    @PatchMapping(path = "{id}") //Endpoint PATCH
    public SujetoEstudio updateParcialById(@RequestBody SujetoEstudio request, @PathVariable("id") codigo_sujeto id){
        return this.sujetoEstudioService.updateById(request, id);
    }

    @DeleteMapping(path = "{id}") //Endpoint DELETE
    public String deleteSujetoEstudioById(@PathVariable("id") codigo_sujeto id){
        boolean ok = this.sujetoEstudioService.deleteSujetoEstudio(id);
        if(ok){
            return "El Sujeto con id: "+ id +" fue eliminado exitosamente";
        }
        return "Error, no fue posible eliminar el Sujeto con id: " + id;
    }
}
