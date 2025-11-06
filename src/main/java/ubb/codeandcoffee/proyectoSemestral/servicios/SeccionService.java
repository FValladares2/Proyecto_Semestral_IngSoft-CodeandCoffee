package ubb.codeandcoffee.proyectoSemestral.servicios;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ubb.codeandcoffee.proyectoSemestral.modelo.Aplicable_a;
import ubb.codeandcoffee.proyectoSemestral.modelo.DatoSolicitado;
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

    public List<Seccion> getSeccionesFiltradas(String tipoSujeto) {
        
        // 1. Convertimos el String "CASO" al Enum Aplicable_a.CASO
        // (toUpperCase() se asegura de que "caso" también funcione)
        Aplicable_a tipoEnum = Aplicable_a.valueOf(tipoSujeto.toUpperCase());

        // 2. Obtenemos todas las secciones de la BD
        List<Seccion> secciones = (List<Seccion>) seccionRepository.findAll();

        // 3. Iteramos sobre cada sección para filtrar su lista de datos
        for (Seccion seccion : secciones) {
            
            // 4. Obtenemos la lista original de datos
            List<DatoSolicitado> datosOriginales = seccion.getDatosSolicitados();
            
            // 5. Usamos un Stream de Java para filtrar la lista
            List<DatoSolicitado> datosFiltrados = datosOriginales.stream()
                .filter(dato -> 
                    // Mantenemos el dato SI es AMBOS
                    dato.getAplicable_a() == Aplicable_a.AMBOS || 
                    // O SI es igual al tipo que buscamos
                    dato.getAplicable_a() == tipoEnum
                )
                .collect(Collectors.toList()); // Convertimos el resultado a una nueva lista

            // 6. Reemplazamos la lista vieja por la lista filtrada
            seccion.setDatosSolicitados(datosFiltrados);
        }

        // 7. Devolvemos las secciones con los datos ya filtrados
        return secciones;
    }

    //Método para guardar una nueva seccion en la base de datos
    public Seccion guardarSeccion(Seccion seccion) {
        
        // --- ¡ESTA ES LA LÍNEA QUE TE FALTA! ---
        if (seccion.getNombre() == null || seccion.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }
        // ----------------------------------------
        
        // Ahora sí, guarda
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
        if (request.getNumero() != null) {
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
