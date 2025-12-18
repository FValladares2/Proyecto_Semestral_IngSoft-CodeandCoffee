package ubb.codeandcoffee.proyectoSemestral.servicios;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ubb.codeandcoffee.proyectoSemestral.modelo.*;
import ubb.codeandcoffee.proyectoSemestral.repositorios.AntecedenteRepository;

@Service //Marca esta clase como un servicio de Spring
public class AntecedenteService {
    @Autowired
    AntecedenteRepository antecedenteRepository; //instancia del repositorio de Antecedente

    @Autowired
    UsuarioSujetoService usujService;

    //obtener antecedentes
    public ArrayList<Antecedente> getAntecedentes(){
        return(ArrayList<Antecedente>) antecedenteRepository.findAll();
    }

    public ArrayList<Antecedente> getAntecedentesBySujeto(SujetoEstudio sujeto){
        return antecedenteRepository.getAllBySujeto(sujeto);
    }

    //guardar nuevo antecedente
    public Antecedente guardarAntecedente(Antecedente antecedente) {
        //Validación: las claves foráneas no pueden ser null
        if (antecedente.getDatoSolicitado() == null) {
            throw new IllegalArgumentException("El DatoSolicitado es obligatorio");
        }
        if (antecedente.getSujetoEstudio() == null) {
            throw new IllegalArgumentException("El SujetoEstudio es obligatorio");
        }
        Antecedente ret = antecedenteRepository.save(antecedente);
        usujService.setLatestChangesAsUsuario();
        return ret;
    }

    //guardar un grupo de antecedentes
    public boolean guardarAntecedentes(List<Antecedente> antecedentes) {
        //Validación: las claves foráneas no pueden ser null
        for (Antecedente antecedente : antecedentes) {
            if (antecedente.getDatoSolicitado() == null) {
                throw new IllegalArgumentException("El DatoSolicitado es obligatorio");
            }
            if (antecedente.getSujetoEstudio() == null) {
                throw new IllegalArgumentException("El SujetoEstudio es obligatorio");
            }
        }
        antecedenteRepository.saveAll(antecedentes);
        usujService.setLatestChangesAsUsuario();
        return true;
    }

    //Buscar por ID
    public Optional<Antecedente> getById(Integer id_antecedente){
        return antecedenteRepository.findById(id_antecedente);
    }

    public List<Antecedente> getAllByDatoSolicitado(DatoSolicitado d){
        return antecedenteRepository.findAllByDatoSolicitado(d);
    }

    //Actualizar por ID
    public Antecedente updateById(Antecedente request, Integer id_antecedente) {
        Antecedente antecedente = antecedenteRepository.findById(id_antecedente)
                .orElseThrow(() -> new RuntimeException("Antecedente no encontrado"));

        //Solo actualiza los valores, no las claves foráneas
        if (request.getValorString() != null) {
            antecedente.setValorString(request.getValorString());
        }
        if (request.getValorNum() != null) {
            antecedente.setValorNum(request.getValorNum());
        }

        //Guarda los cambios en la base de datos y retorna el antecedente actualizado
        Antecedente ret = antecedenteRepository.save(antecedente);
        usujService.setLatestChangesAsUsuario();
        return ret;
    }

    //Eliminar por ID
    public Boolean deleteAntecedente(Integer id_antecedente){
        try{
            antecedenteRepository.deleteById(id_antecedente);
            usujService.setLatestChangesAsUsuario();
            return true;
        }catch(Exception e){
            return false;
        }
    }
}