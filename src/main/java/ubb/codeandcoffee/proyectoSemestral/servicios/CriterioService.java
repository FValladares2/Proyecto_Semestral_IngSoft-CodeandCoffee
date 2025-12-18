package ubb.codeandcoffee.proyectoSemestral.servicios;
import java.util.ArrayList;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ubb.codeandcoffee.proyectoSemestral.modelo.Criterio;
import ubb.codeandcoffee.proyectoSemestral.repositorios.CriterioRepository;

@Service//Marca esta clase como un servicio de Spring
public class CriterioService {
    @Autowired
    CriterioRepository criterioRepository; //instancia del repositorio de Criterio

    //obtener criterios de la bdd
    public ArrayList<Criterio> getCriterios(){
        return(ArrayList<Criterio>) criterioRepository.findAll();
    }

    //guardar un nuevo criterio en la bdd
    public Criterio guardarCriterio(Criterio criterio) {

        if (criterio.getNombre() == null || criterio.getNombre().isEmpty()) {
            throw new IllegalArgumentException("El nombre del criterio es obligatorio");
        }
        if (criterio.getExpresion() == null || criterio.getExpresion().isEmpty()) {
            throw new IllegalArgumentException("La expresión del criterio es obligatoria");
        }
        if (criterio.getTipoCalculo() == null) {
            throw new IllegalArgumentException("El tipo de cálculo es obligatorio");
        }
        return criterioRepository.save(criterio);
    }

    //Buscar por ID
    public Optional<Criterio> getById(Integer id_criterio){
        return criterioRepository.findById(id_criterio);
    }

    //Actualizar por ID
    public Criterio updateById(Criterio request, Integer id_criterio) {
        Criterio criterio = criterioRepository.findById(id_criterio)
                .orElseThrow(() -> new RuntimeException("Criterio no encontrado"));

        //Solo actualiza si no es null
        if (request.getNombre() != null) {
            criterio.setNombre(request.getNombre());
        }
        if (request.getNombreStata() != null) {
            criterio.setNombreStata(request.getNombreStata());
        }
        if (request.getTipoCalculo() != null) {
            criterio.setTipoCalculo(request.getTipoCalculo());
        }
        if (request.getLeyenda() != null) {
            criterio.setLeyenda(request.getLeyenda());
        }
        if (request.getExpresion() != null) {
            criterio.setExpresion(request.getExpresion());
        }

        return criterioRepository.save(criterio);
    }

    //Eliminar por ID
    public Boolean deleteCriterio(Integer id_criterio){
        try{
            criterioRepository.deleteById(id_criterio);
            return true;
        }catch(Exception e){
            return false;
        }
    }

    public Boolean desactivarCriterio(Integer id_criterio) {
        try {
            // buscamos el criterio
            Criterio criterio = criterioRepository.findById(id_criterio).orElse(null);
            if (criterio == null) {
                return false;
            }

            // Lo 'apagamos'. No se borra nada de la tabla intermedia, el criterio queda invalido logicamente.
            criterio.setActivo(false);
            criterioRepository.save(criterio);

            return true;

        } catch (Exception e) {
            return false;
        }
    }

    public Boolean activarCriterio(Integer id_criterio) {
        try {
            // buscamos el criterio en la base de datos
            Criterio criterio = criterioRepository.findById(id_criterio).orElse(null);

            // Si no existe
            if (criterio == null) {
                return false;
            }

            // cambiamos el interruptor a verdadero para que vuelva a estar disponible
            criterio.setActivo(true);
            criterioRepository.save(criterio);
            return true;

        } catch (Exception e) {
            return false;
        }
    }
}