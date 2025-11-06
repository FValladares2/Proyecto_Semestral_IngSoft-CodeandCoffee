package ubb.codeandcoffee.proyectoSemestral.servicios;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ubb.codeandcoffee.proyectoSemestral.modelo.Seccion;
import ubb.codeandcoffee.proyectoSemestral.repositorios.SeccionRepository;

@Service //Marca esta clase como un servicio de Spring
public class SeccionService {
    @Autowired
    SeccionRepository seccionRepository; //instancia del repositorio de Seccion

    //Método para obtener las secciones de la base de datos
    public ArrayList<Seccion> getAllSecciones(){
        return(ArrayList<Seccion>) seccionRepository.findAll();
    }

    //Método para guardar una nueva seccion en la base de datos
    public Seccion guardarSeccion(Seccion seccion) {
        return seccionRepository.save(seccion);
    }

    //Método para buscar una seccion por su ID
    public Optional<Seccion> getById(Integer id_seccion){
        return seccionRepository.findById(id_seccion);
    }


    // Método para actualizar una seccion existente por ID
    public Seccion updateById(Seccion request, Integer id_seccion) {
        
        // 1. Busca la sección en la base de datos
        Seccion seccion = seccionRepository.findById(id_seccion)
                .orElseThrow(() -> new RuntimeException("Seccion no encontrada"));

        // 2. Actualiza el número SOLO SI se proporcionó uno nuevo (no es null)
        if (request.getNumero() != 0) {
            seccion.setNumero(request.getNumero());
        }

        // 3. Actualiza el nombre SOLO SI se proporcionó uno nuevo (no es null)
        if (request.getNombre() != null) {
            seccion.setNombre(request.getNombre());
        }

        // 4. Guarda los cambios en la base de datos y retorna la seccion actualizada
        return seccionRepository.save(seccion);
    }

    //Método para eliminar una seccion por ID
    public Boolean deleteSeccion(Integer id_seccion){
        try{
            seccionRepository.deleteById(id_seccion);
            return true;
        }catch(Exception e){
            return false;
        }
    }
}
