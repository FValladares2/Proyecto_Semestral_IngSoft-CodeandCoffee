package ubb.codeandcoffee.proyectoSemestral.servicios;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ubb.codeandcoffee.proyectoSemestral.modelo.Antecedente;
import ubb.codeandcoffee.proyectoSemestral.modelo.SujetoEstudio;
import ubb.codeandcoffee.proyectoSemestral.modelo.codigo_sujeto;
import ubb.codeandcoffee.proyectoSemestral.repositorios.AntecedenteRepository;
import ubb.codeandcoffee.proyectoSemestral.repositorios.SujetoEstudioRepository;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class SujetoEstudioService {
    @Autowired
    SujetoEstudioRepository sujetoestudioRepository;

    public ArrayList<SujetoEstudio> getSujetoEstudio(){
        return(ArrayList<SujetoEstudio>) sujetoestudioRepository.findAll();
    }

    public SujetoEstudio guardarSujetoEstudio(SujetoEstudio sujeto) {
        if (sujeto.getTipo() == null) {
            throw new IllegalArgumentException("El tipo es obligatorio");
        }
        if (sujeto.getNombre() == null) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }

        SujetoEstudio save = sujetoestudioRepository.save(sujeto);
        //se hace una query para obtener el código (num) que debería ser asignado por la bdd,
        //  con debug, se ve que "save" lo mantiene como null, no como "retorno"
        SujetoEstudio retorno = sujetoestudioRepository.findByNombre(sujeto.getNombre());
        return retorno;
    }

    public codigo_sujeto getCodigo(String tipo, String id_sujeto){return new codigo_sujeto(tipo,id_sujeto);}

    public Optional<SujetoEstudio> getById(codigo_sujeto codigo){
        return sujetoestudioRepository.findById(codigo);
    }

    public SujetoEstudio updateById(SujetoEstudio request, codigo_sujeto codigo) {
        SujetoEstudio sujeto = sujetoestudioRepository.findById(codigo)
                .orElseThrow(() -> new RuntimeException("Sujeto no encontrado"));

        if (!request.getNombre().equals(sujeto.getNombre())) {
            sujeto.setNombre(request.getNombre());
        }
        if (request.getDireccion() != null) {
            sujeto.setDireccion(request.getDireccion());
        }
        if (request.getOcupacion() != null) {
            sujeto.setOcupacion(request.getOcupacion());
        }
        if (request.getTelefono() != null) {
            sujeto.setTelefono(request.getTelefono());
        }
        if (request.getEmail() != null) {
            sujeto.setEmail(request.getEmail());
        }
        if (request.getNacionalidad() != null) {
            sujeto.setNacionalidad(request.getNacionalidad());
        }

        return sujetoestudioRepository.save(sujeto);
    }

    public Boolean deleteSujetoEstudio(codigo_sujeto codigo){
        try{
            sujetoestudioRepository.deleteById(codigo);
            return true;
        }catch(Exception e){
            return false;
        }
    }
}
