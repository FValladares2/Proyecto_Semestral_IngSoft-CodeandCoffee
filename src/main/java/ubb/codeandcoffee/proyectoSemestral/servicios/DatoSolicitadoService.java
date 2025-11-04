package ubb.codeandcoffee.proyectoSemestral.servicios;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ubb.codeandcoffee.proyectoSemestral.modelo.DatoSolicitado;
import ubb.codeandcoffee.proyectoSemestral.repositorios.DatoSolicitadoRepository;

@Service //Marca esta clase como un servicio de Spring
public class DatoSolicitadoService {
    @Autowired
    DatoSolicitadoRepository datoRepository; //instancia del repositorio de DatoSolicitado

    //Método para obtener los datos de la base de datos
    public ArrayList<DatoSolicitado> getDatoSolicitados(){
        return(ArrayList<DatoSolicitado>) datoRepository.findAll();
    }

    //Método para guardar un nuevo dato en la base de datos
    public DatoSolicitado guardarDatoSolicitado(DatoSolicitado dato) {
        // Validación: el nombre no puede ser null
        if (dato.getNombre() == null || dato.getNombre().isEmpty()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }
            //AGREGAR SI ES QUE FALTAN
        return datoRepository.save(dato);
    }

    //Método para buscar un dato por su ID
    public Optional<DatoSolicitado> getById(Integer id_dato){
        return datoRepository.findById(id_dato);
    }

    //Método para actualizar un dato existente por ID
    public DatoSolicitado updateById(DatoSolicitado request, Integer id_dato) {
        DatoSolicitado dato = datoRepository.findById(id_dato)
            .orElseThrow(() -> new RuntimeException("Dato no encontrado"));

        // Solo actualiza si no es null
        if (request.getLeyenda() != null) {
            dato.setLeyenda(request.getLeyenda());
        }
        //AGREGAR SI ES QUE FALTAN (Y REVISAR)
    
        // Guarda los cambios en la base de datos y retorna el dato actualizado
        return datoRepository.save(dato);
    }

    //Método para eliminar un dato por ID
    public Boolean deleteDatoSolicitado(Integer id_dato){
        try{
            datoRepository.deleteById(id_dato);
            return true;
        }catch(Exception e){
            return false;
        }
    }
}