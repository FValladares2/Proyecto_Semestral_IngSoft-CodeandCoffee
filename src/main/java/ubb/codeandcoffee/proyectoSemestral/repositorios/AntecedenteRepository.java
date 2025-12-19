package ubb.codeandcoffee.proyectoSemestral.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ubb.codeandcoffee.proyectoSemestral.modelo.Antecedente;
import ubb.codeandcoffee.proyectoSemestral.modelo.DatoSolicitado;
import ubb.codeandcoffee.proyectoSemestral.modelo.SujetoEstudio;
import ubb.codeandcoffee.proyectoSemestral.modelo.Usuario;

import java.util.ArrayList;
import java.util.List;

// Repositorio para la entidad Antecedente
@Repository
public interface AntecedenteRepository extends JpaRepository<Antecedente, Integer> {
    List<Antecedente> findAllBySujetoEstudio(SujetoEstudio s);
    @Query("select a from Antecedente a where a.sujetoEstudio = ?1")
    ArrayList<Antecedente> getAllBySujeto(SujetoEstudio sujeto);
    List<Antecedente> findAllByDatoSolicitado(DatoSolicitado d);
}
