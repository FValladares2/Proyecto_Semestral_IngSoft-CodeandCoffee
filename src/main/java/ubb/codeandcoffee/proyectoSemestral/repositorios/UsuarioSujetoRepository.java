package ubb.codeandcoffee.proyectoSemestral.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ubb.codeandcoffee.proyectoSemestral.modelo.SujetoEstudio;
import ubb.codeandcoffee.proyectoSemestral.modelo.Usuario;
import ubb.codeandcoffee.proyectoSemestral.modelo.Usuario_Sujeto;
import ubb.codeandcoffee.proyectoSemestral.modelo.usuario_sujeto_id;

@Repository
public interface UsuarioSujetoRepository extends JpaRepository<Usuario_Sujeto, usuario_sujeto_id> {
    @Query("select u from Usuario_Sujeto u where u.usuario = ?1 and u.sujetoEstudio = ?2")
    Usuario_Sujeto findByQuery(Usuario usuario, SujetoEstudio sujeto);
}
