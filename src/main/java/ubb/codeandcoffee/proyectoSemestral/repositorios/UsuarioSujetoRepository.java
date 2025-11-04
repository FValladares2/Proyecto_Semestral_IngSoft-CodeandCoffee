package ubb.codeandcoffee.proyectoSemestral.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ubb.codeandcoffee.proyectoSemestral.modelo.Usuario_Sujeto;
import ubb.codeandcoffee.proyectoSemestral.modelo.usuario_sujeto_id;

@Repository
public interface UsuarioSujetoRepository extends JpaRepository<Usuario_Sujeto, usuario_sujeto_id> {
}
