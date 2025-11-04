package ubb.codeandcoffee.proyectoSemestral.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ubb.codeandcoffee.proyectoSemestral.modelo.DatoSolicitado;

@Repository
public interface DatoSolicitadoRepository extends JpaRepository<DatoSolicitado, Integer>{
    
}
