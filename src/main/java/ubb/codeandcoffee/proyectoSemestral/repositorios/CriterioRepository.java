package ubb.codeandcoffee.proyectoSemestral.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ubb.codeandcoffee.proyectoSemestral.modelo.Criterio;
import ubb.codeandcoffee.proyectoSemestral.modelo.DatoSolicitado;

import java.util.Set;

@Repository
public interface CriterioRepository extends JpaRepository<Criterio, Integer> {
    Set<Criterio> getAllByDatosSolicitados(DatoSolicitado d);
}
