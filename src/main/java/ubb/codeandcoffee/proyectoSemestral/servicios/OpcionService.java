package ubb.codeandcoffee.proyectoSemestral.servicios;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ubb.codeandcoffee.proyectoSemestral.modelo.DatoSolicitado;
import ubb.codeandcoffee.proyectoSemestral.modelo.Opcion;
import ubb.codeandcoffee.proyectoSemestral.repositorios.DatoSolicitadoRepository;
import ubb.codeandcoffee.proyectoSemestral.repositorios.OpcionRepository;

@Service
public class OpcionService {
    @Autowired
    OpcionRepository opcionRepository;
    DatoSolicitadoRepository datoSolicitadoRepository;
    @Autowired
    public OpcionService(OpcionRepository opcionRepository, DatoSolicitadoRepository datoSolicitadoRepository) {
        this.opcionRepository = opcionRepository;
        this.datoSolicitadoRepository = datoSolicitadoRepository; // <--- Inyección aquí
    }

    //Método para obtener las opciones de la base de datos
    public ArrayList<Opcion> getOpciones(){
        return(ArrayList<Opcion>) opcionRepository.findAll();
    }

    //Método para guardar una nueva opcion en la base de datos
    public Opcion guardarOpcion(Opcion opcion) {
        
        //valida los campos obligatorios de la opción
        if (opcion.getNombre() == null || opcion.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la opción es obligatorio");
        }
        if (opcion.getValor() == null) {
            throw new IllegalArgumentException("El valor numérico de la opción es obligatorio.");
        }

        //buscar el "padre" (DatoSolicitado)
        DatoSolicitado datoSolicitadoPadre = opcion.getDatoSolicitado();
        if (datoSolicitadoPadre == null) {
            throw new IllegalArgumentException("El objeto 'dato solicitado' es obligatorio en la solicitud");
        }
        Integer id_dato= datoSolicitadoPadre.getId_dato();
        if(id_dato == null){
            throw new IllegalArgumentException("El 'id dato' dentro del objeto 'dato solicitado' es obligatorio");
        }
        DatoSolicitado datoCompleto = datoSolicitadoRepository.findById(id_dato)
            .orElseThrow(() -> new RuntimeException("Error: La sección con ID " + id_dato + " no existe."));

        //verificamos si ya existe otra opción con el mismo valor
        boolean existeDuplicado = opcionRepository.existsByDatoAndValor(datoCompleto, opcion.getValor());

        //si existe una opcion para el dato con el mismo valor
        if (existeDuplicado) {
            throw new IllegalStateException("Ya existe una opción con el valor '" + opcion.getValor() + "' para este dato.");
        }

        //verifica que solo exista una opcion que requiera texto
        if (opcion.isRequiereTexto()) {
            boolean existeOtra = opcionRepository.existsByDatoAndRequiereTextoTrue(datoCompleto);
            if (existeOtra) {
                throw new IllegalStateException("Ya existe una opción con texto para el dato ingresado");
            }
        }
        //signa el padre a la opcion
        opcion.setDatoSolicitado(datoCompleto);

        //Guarda la opción
        return opcionRepository.save(opcion);
    }

    //Método para buscar una opcion por su ID
    public Optional<Opcion> getById(Integer id_opcion){
        return opcionRepository.findById(id_opcion);
    }

    //Método para actualizar una opcion existente por ID
    public Opcion updateById(Opcion request, Integer id_dato) {
        Opcion opcion = opcionRepository.findById(id_dato)
            .orElseThrow(() -> new RuntimeException("Opcion no encontrada"));

        // Solo actualiza si no es null
        if (request.getNombre() != null) {
            opcion.setNombre(request.getNombre());
        }
        if (request.getValor() != 0) {
            //falta hacer la logica de que no se actualice a un valor que este asignado
            //a otra opcion del dato "padre"
            opcion.setValor(request.getValor());
        }
        if(opcion.isRequiereTexto() != false){
            opcion.setRequiereTexto(request.isRequiereTexto());
        }
        // Guarda los cambios en la base de datos y retorna el la opcion actualizada
        return opcionRepository.save(opcion);
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
