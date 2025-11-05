package ubb.codeandcoffee.proyectoSemestral.repositorios;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ubb.codeandcoffee.proyectoSemestral.modelo.SujetoEstudio;
import ubb.codeandcoffee.proyectoSemestral.modelo.codigo_sujeto;

@Repository
public interface SujetoEstudioRepository extends JpaRepository<SujetoEstudio, codigo_sujeto> { //funcionará con codigo_sujeto?
    @Query("select u from SujetoEstudio u where u.nombre = ?1")
    SujetoEstudio findByNombre(String nombre);
}
