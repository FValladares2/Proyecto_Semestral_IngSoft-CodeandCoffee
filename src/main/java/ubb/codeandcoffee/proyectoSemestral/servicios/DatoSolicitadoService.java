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

        if (dato.getNombre() == null || dato.getNombre().isEmpty()) {
            dato.setNombre(dato.getLeyenda());
        }

        Seccion seccionEnviada = dato.getSeccion();
        if (seccionEnviada == null) {
            throw new IllegalArgumentException("El objeto 'seccion' es obligatorio en la solicitud");
        }
        Integer idSeccion = seccionEnviada.getId_seccion();
        if (idSeccion == null) {
            throw new IllegalArgumentException("El 'idSeccion' dentro del objeto 'seccion' es obligatorio");
        }



        Seccion seccionCompleta = seccionRepository.findById(idSeccion)
            .orElseThrow(() -> new RuntimeException("Error: La sección con ID " + idSeccion + " no existe."));


        dato.setSeccion(seccionCompleta);


        return datoRepository.save(dato);
    }


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
    

        return datoRepository.save(dato);
    }

    public Boolean deleteDatoSolicitado(Integer id_dato){
        try{
            datoRepository.deleteById(id_dato);
            return true;
        }catch(Exception e){
            return false;
        }
    }
}