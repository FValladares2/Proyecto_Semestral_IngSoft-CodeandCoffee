package ubb.codeandcoffee.proyectoSemestral.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ubb.codeandcoffee.proyectoSemestral.modelo.DatoSolicitado;
import ubb.codeandcoffee.proyectoSemestral.modelo.Opcion;

@Repository
public interface OpcionRepository extends JpaRepository<Opcion, Integer>{
    boolean existsByDatoAndRequiereTextoTrue(DatoSolicitado dato);

    boolean existsByDatoSolicitadoAndValor(DatoSolicitado datoSolicitado, Integer valor);
    
} 