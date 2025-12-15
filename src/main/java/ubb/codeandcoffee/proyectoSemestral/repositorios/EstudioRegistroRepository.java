package ubb.codeandcoffee.proyectoSemestral.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ubb.codeandcoffee.proyectoSemestral.modelo.EstudioRegistro;
import java.util.List;

@Repository
public interface EstudioRegistroRepository extends JpaRepository<EstudioRegistro, Integer> {
    // para listar todos
    List<EstudioRegistro> findAllByOrderByFechaCreacionDesc();
    
    //requisito de calidad, para un futuro hipotetico donde donde un admin solo puede ver sus propios estudios
    // List<EstudioRegistro> findByCreadorIdUsuario(Integer idUsuario);
}