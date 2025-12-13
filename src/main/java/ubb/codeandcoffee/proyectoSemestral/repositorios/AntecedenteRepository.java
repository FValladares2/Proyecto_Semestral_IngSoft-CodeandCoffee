package ubb.codeandcoffee.proyectoSemestral.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ubb.codeandcoffee.proyectoSemestral.modelo.Antecedente;
import ubb.codeandcoffee.proyectoSemestral.modelo.SujetoEstudio;

import java.util.List;

@Repository
public interface AntecedenteRepository extends JpaRepository<Antecedente, Integer> {
    List<Antecedente> findAllBySujetoEstudio(SujetoEstudio sujeto);
    @Query("SELECT COUNT(a) > 0 FROM Antecedente a WHERE a.datoSolicitado.id_dato = :id")
    boolean verificarSiTieneAntecedentes(@Param("id") Integer id);
}
