package ubb.codeandcoffee.proyectoSemestral.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ubb.codeandcoffee.proyectoSemestral.modelo.Criterio;
import java.util.List;

@Repository
public interface CriterioRepository extends JpaRepository<Criterio, Integer> {
    @Query("SELECT COUNT(c) > 0 FROM Criterio c JOIN c.datosSolicitados d WHERE d.id_dato = :id")
    boolean verificarSiEsParteDeCriterio(@Param("id") Integer id);

    @Query("SELECT c FROM Criterio c JOIN c.datosSolicitados d WHERE d.id_dato = :id")
    List<Criterio> buscarCriteriosPorDato(@Param("id") Integer id);

}
