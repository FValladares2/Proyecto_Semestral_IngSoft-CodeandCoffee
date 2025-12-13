package ubb.codeandcoffee.proyectoSemestral.servicios;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ubb.codeandcoffee.proyectoSemestral.modelo.*;
import ubb.codeandcoffee.proyectoSemestral.repositorios.AntecedenteRepository;
import ubb.codeandcoffee.proyectoSemestral.repositorios.CriterioRepository;
import ubb.codeandcoffee.proyectoSemestral.repositorios.DatoSolicitadoRepository;
import ubb.codeandcoffee.proyectoSemestral.repositorios.SeccionRepository;

@Service //Marca esta clase como un servicio de Spring
public class DatoSolicitadoService {
    private final DatoSolicitadoRepository datoRepository; //instancia del repositorio de DatoSolicitado
    private final SeccionRepository seccionRepository;
    @Autowired
    private AntecedenteRepository antecedenteRepository;

    @Autowired
    private CriterioRepository criterioRepository;
    public DatoSolicitadoService(DatoSolicitadoRepository datoRepository, SeccionRepository seccionRepository) {
        this.datoRepository = datoRepository;
        this.seccionRepository = seccionRepository;
    }

    //Método para obtener los datos de la base de datos
    public ArrayList<DatoSolicitado> getDatoSolicitados(){
        return(ArrayList<DatoSolicitado>) datoRepository.findAll();
    }

    //Método para guardar un nuevo dato en la base de datos
    public DatoSolicitado guardarDatoSolicitado(DatoSolicitado dato) {

        if (dato.getNombre() == null || dato.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }
        if (dato.getEstudio() == null) {
            throw new IllegalArgumentException("El campo 'estudio' es obligatorio");
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

        tipoRespuestaNumero(dato);
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
        if (request.getNombreStata() != null) {
            dato.setNombreStata(request.getNombreStata());
        }
        if (request.getNombre() != null) {
            dato.setNombre(request.getNombre());
        }
        if (request.getAplicable_a() != null) {
            dato.setAplicable_a(request.getAplicable_a());
        }

        if (request.getTipoRespuesta() != null) {
            dato.setTipoRespuesta(request.getTipoRespuesta());
            tipoRespuestaNumero(request);
        }

        if (request.getValorMin() != null) {
            dato.setValorMin(request.getValorMin());
        }
        if (request.getValorMax() != null) {
            dato.setValorMax(request.getValorMax());
        }

        return datoRepository.save(dato);
    }

    private void tipoRespuestaNumero(DatoSolicitado request) {
        if(request.getTipoRespuesta()== TipoRespuesta.NUMERO){
            if(request.getValorMin()==null||request.getValorMax()==null){
                throw new IllegalArgumentException("para el tipo de respuesta: Numero, " +
                        "los campos valorMin y ValorMax son obigatorios");
            }
            if(request.getValorMin()>request.getValorMax()){
                throw new IllegalArgumentException("valorMin no puede ser Mayor a valorMax");
            }
        }
    }

    public Boolean deleteDatoSolicitado(Integer id_dato){
        try{
            datoRepository.deleteById(id_dato);
            return true;
        }catch(Exception e){
            return false;
        }
    }

    @Transactional
    public void eliminarDato(Integer datoId) {

        // buscamos el dato
        DatoSolicitado dato = datoRepository.findById(datoId)
                .orElseThrow(() -> new RuntimeException("Dato no encontrado"));

        // Verificamos dependencias
        boolean tieneAntecedentes = antecedenteRepository.verificarSiTieneAntecedentes(datoId);
        boolean tieneCriterios = criterioRepository.verificarSiEsParteDeCriterio(datoId);

        // lógica
        if (tieneAntecedentes || tieneCriterios) {
            // desactivar
            dato.setActivo(false);

            if (tieneCriterios) {
                // buscamos los criterios asociados a este dato
                List<Criterio> criteriosAsociados = criterioRepository.buscarCriteriosPorDato(datoId);

                for (Criterio c : criteriosAsociados) {
                    c.setActivo(false);
                    criterioRepository.save(c);
                }
            }

            datoRepository.save(dato);

        } else {
            // borrar de la bd, no tiene ni criterios ni antecedentes relacionados
            datoRepository.delete(dato);
        }
    }
    public List<DatoSolicitado> buscarTodosLosDatos(String tipoSujeto) {
        Aplicable_a tipo;
        if(tipoSujeto.equalsIgnoreCase("CASO")){
            tipo= Aplicable_a.CASO;
        }else{
            tipo= Aplicable_a.CONTROL;
        }
        final Aplicable_a TIPO_AMBOS = Aplicable_a.AMBOS;
        return datoRepository.buscarTodosLosDatos(tipo, TIPO_AMBOS);
    }
}