package ubb.codeandcoffee.proyectoSemestral.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ubb.codeandcoffee.proyectoSemestral.modelo.Seccion;

// Repositorio para la entidad Seccion
@Repository
public interface SeccionRepository extends JpaRepository<Seccion, Integer>{
    
}
