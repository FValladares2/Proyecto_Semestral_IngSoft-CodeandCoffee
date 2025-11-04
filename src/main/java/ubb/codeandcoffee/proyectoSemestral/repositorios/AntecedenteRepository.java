package ubb.codeandcoffee.proyectoSemestral.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ubb.codeandcoffee.proyectoSemestral.modelo.Antecedente;

@Repository
public interface AntecedenteRepository extends JpaRepository<Antecedente, Integer> {

}
