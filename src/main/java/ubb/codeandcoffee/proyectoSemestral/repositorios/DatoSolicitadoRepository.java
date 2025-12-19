package ubb.codeandcoffee.proyectoSemestral.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ubb.codeandcoffee.proyectoSemestral.modelo.Aplicable_a;
import ubb.codeandcoffee.proyectoSemestral.modelo.DatoSolicitado;

import java.util.List;
// Repositorio para la entidad DatoSolicitado
@Repository
public interface DatoSolicitadoRepository extends JpaRepository<DatoSolicitado, Integer>{
    @Query("SELECT d FROM DatoSolicitado d WHERE d.aplicable_a = :tipoSujeto OR d.aplicable_a = :tipoAmbos")
    List<DatoSolicitado> buscarTodosLosDatos(
            @Param("tipoSujeto") Aplicable_a tipoSujeto,
            @Param("tipoAmbos") Aplicable_a tipoAmbos
    );
    List<DatoSolicitado> findAllById(Iterable<Integer> ids);
    boolean existsByNombreStataIgnoreCase(String nombreStata);

    Optional<DatoSolicitado> findByNombreStataIgnoreCase(String nombreStata);
}
