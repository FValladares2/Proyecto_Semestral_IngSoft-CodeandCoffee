package ubb.codeandcoffee.proyectoSemestral.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ubb.codeandcoffee.proyectoSemestral.modelo.Antecedente;
import ubb.codeandcoffee.proyectoSemestral.modelo.SujetoEstudio;

import java.util.ArrayList;

@Repository
public interface AntecedenteRepository extends JpaRepository<Antecedente, Integer> {
    @Query("select a from Antecedente a where a.sujetoEstudio = ?1")
    ArrayList<Antecedente> getAllBySujeto(SujetoEstudio sujeto);
}
