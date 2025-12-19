package ubb.codeandcoffee.proyectoSemestral.repositorios;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ubb.codeandcoffee.proyectoSemestral.modelo.SujetoEstudio;
import ubb.codeandcoffee.proyectoSemestral.modelo.codigo_sujeto;
import java.util.*;
// Repositorio para la entidad SujetoEstudio
@Repository
public interface SujetoEstudioRepository extends JpaRepository<SujetoEstudio, codigo_sujeto> { //funcionará con codigo_sujeto?
    @Query("select s from SujetoEstudio s where s.nombre = ?1")
    SujetoEstudio findByNombre(String nombre);

    @Query("select s from SujetoEstudio s where s.id_sujeto = ?1 and s.tipo = ?2")
    Optional<SujetoEstudio> findByIdSujetoAndTipo(String idSujeto, String tipo);
}
