package ubb.codeandcoffee.proyectoSemestral.servicios;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ubb.codeandcoffee.proyectoSemestral.modelo.DatoSolicitado;
import ubb.codeandcoffee.proyectoSemestral.modelo.Seccion;
import ubb.codeandcoffee.proyectoSemestral.repositorios.DatoSolicitadoRepository;
import ubb.codeandcoffee.proyectoSemestral.repositorios.SeccionRepository;

@Service //Marca esta clase como un servicio de Spring
public class DatoSolicitadoService {
    @Autowired
    DatoSolicitadoRepository datoRepository; //instancia del repositorio de DatoSolicitado

    @Autowired
    private SeccionRepository seccionRepository;

    //Método para obtener los datos de la base de datos
    public ArrayList<DatoSolicitado> getDatoSolicitados(){
        return(ArrayList<DatoSolicitado>) datoRepository.findAll();
    }

    //Método para guardar un nuevo dato en la base de datos
    
    public DatoSolicitado guardarDatoSolicitado(DatoSolicitado dato) {
        
        // --- VALIDACIONES (Las tuyas) ---
        if (dato.getNombre() == null || dato.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }
        if (dato.getEstudio() == null) {
            throw new IllegalArgumentException("El campo 'estudio' es obligatorio");
        }

        // --- VALIDACIÓN Y BÚSQUEDA DE SECCION (El paso que te falta) ---
        
        // 1. Validar que el objeto 'seccion' y su ID vengan (Forma segura)
        Seccion seccionEnviada = dato.getSeccion();
        if (seccionEnviada == null) {
            throw new IllegalArgumentException("El objeto 'seccion' es obligatorio en la solicitud");
        }
        Integer idSeccion = seccionEnviada.getId_seccion(); // Asumo que el getter es getIdSeccion()
        if (idSeccion == null) {
            throw new IllegalArgumentException("El 'idSeccion' dentro del objeto 'seccion' es obligatorio");
        }
        

        // 2. Buscar la sección REAL en la BD
        Seccion seccionCompleta = seccionRepository.findById(idSeccion)
            .orElseThrow(() -> new RuntimeException("Error: La sección con ID " + idSeccion + " no existe."));

        // 3. REEMPLAZAR la sección "fantasma" por la "real"
        dato.setSeccion(seccionCompleta);

        // 4. Guardar
        // Ahora sí guardas el 'dato' con la relación correcta
        return datoRepository.save(dato); // <-- Esta es tu línea 47
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