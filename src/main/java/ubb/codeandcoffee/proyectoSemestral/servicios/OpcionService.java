package ubb.codeandcoffee.proyectoSemestral.servicios;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ubb.codeandcoffee.proyectoSemestral.modelo.Opcion;
import ubb.codeandcoffee.proyectoSemestral.repositorios.OpcionRepository;

@Service //Marca esta clase como un servicio de Spring
public class OpcionService {
    @Autowired
    OpcionRepository opcionRepository; //instancia del repositorio de Opcion

    //Método para obtener las opciones de la base de datos
    public ArrayList<Opcion> getOpciones(){
        return(ArrayList<Opcion>) opcionRepository.findAll();
    }

    //Método para guardar una nueva opcion en la base de datos
    public Opcion guardarOpcion(Opcion opcion) {
        return opcionRepository.save(opcion);
    }

    //Método para buscar una opcion por su ID
    public Optional<Opcion> getById(Integer id_dato){
        return opcionRepository.findById(id_dato);
    }

    //Método para actualizar una opcion existente por ID
    public Opcion updateById(Opcion request, Integer id_dato) {
        Opcion dato = opcionRepository.findById(id_dato)
            .orElseThrow(() -> new RuntimeException("Opcion no encontrada"));

        // Solo actualiza si no es null
        if (request.getNombre() != null) {
            dato.setNombre(request.getNombre());
        }
        //REVISAR
        //AGREGAR SI ES QUE FALTAN

        // Guarda los cambios en la base de datos y retorna el la opcion actualizada
        return opcionRepository.save(dato);
    }

    //Método para eliminar una opcions por ID
    public Boolean deleteOpcion(Integer id_dato){
        try{
            opcionRepository.deleteById(id_dato);
            return true;
        }catch(Exception e){
            return false;
        }
    }
}
