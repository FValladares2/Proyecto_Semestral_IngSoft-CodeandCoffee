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

import ubb.codeandcoffee.proyectoSemestral.modelo.Antecedente;
import ubb.codeandcoffee.proyectoSemestral.servicios.AntecedenteService;

import org.springframework.web.bind.annotation.PutMapping;

@RestController //Indica que esta clase es un controlador REST y sus métodos devuelven JSON
@RequestMapping("/Antecedente")//Define la ruta base para todos los endpoints de esta clase
public class AntecedenteController {
    @Autowired
    private AntecedenteService antecedenteService;//instancia del servicio de antecedente


    @GetMapping //endpoint GET: obtiene todos los antecedentes
    public ArrayList<Antecedente> getAntecedentes(){
        return this.antecedenteService.getAntecedentes(); //Llama al servicio get
    }

    @PostMapping //Endpoint POST: crea un nuevo antecedente
    public Antecedente guardarAntecedente(@RequestBody Antecedente antecedente){
        // @RequestBody: JSON de la solicitud se convierte a Antecedente
        return this.antecedenteService.guardarAntecedente(antecedente);
    }

    @GetMapping(path = "{id}") //Endpoint GET: obtiene un antecedente por su ID
    public Optional<Antecedente> getAntecedenteById(@PathVariable("id") Integer id_antecedente){
        return this.antecedenteService.getById(id_antecedente);
    }

    @PutMapping(path = "{id}") //Endpoint PUT: actualizar un antecedente por su ID
    public Antecedente updateAntecedenteById(@RequestBody Antecedente request, @PathVariable("id") Integer id_antecedente){
        return this.antecedenteService.updateById(request, id_antecedente);
    }

    @PatchMapping(path = "{id}") //Endpoint PATCH: actualiza parcialmente un antecedente por su ID
    public Antecedente updateParcialById(@RequestBody Antecedente request, @PathVariable("id") Integer id_antecedente){
        return this.antecedenteService.updateById(request, id_antecedente);
    }

    @DeleteMapping(path = "{id}") //Endpoint DELETE: elimina un antecedente por su ID
    public String deleteAntecedenteById(@PathVariable("id") Integer id_antecedente){
        boolean ok = this.antecedenteService.deleteAntecedente(id_antecedente);
        if(ok){
            return "El antecedente con id: "+ id_antecedente +" fue eliminado exitosamente";
        }
        return "Error, no fue posible eliminar el antecedente con id: " + id_antecedente;
    }
}