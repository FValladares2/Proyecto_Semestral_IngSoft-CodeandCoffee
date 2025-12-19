package ubb.codeandcoffee.proyectoSemestral.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ubb.codeandcoffee.proyectoSemestral.modelo.Seccion;

// Repositorio para la entidad Seccion
@Repository
public interface SeccionRepository extends JpaRepository<Seccion, Integer>{
    // buscamos cual es el numero mas alto para saber que sugerir al final
    @Query("SELECT COALESCE(MAX(s.numero), 0) FROM Seccion s")
    Integer obtenerUltimoNumero();

    // verificamos si un numero de orden ya existe
    boolean existsByNumero(Integer numero);

    // traemos todas las secciones pero ordenadas por numero
    List<Seccion> findAllByOrderByNumeroAsc();

    // empujamos las secciones hacia adelante (+1) para hacer espacio en el medio
    @Modifying
    @Query("UPDATE Seccion s SET s.numero = s.numero + 1 WHERE s.numero >= :numero")
    void empujarSecciones(@Param("numero") Integer numeroBase);

    // al borrar, retrocedemos las secciones (-1) para tapar el hueco
    @Modifying
    @Query("UPDATE Seccion s SET s.numero = s.numero - 1 WHERE s.numero > :numeroBorrado")
    void reordenarDespuesDeBorrar(@Param("numeroBorrado") Integer numeroBorrado);
}
