package ubb.codeandcoffee.proyectoSemestral;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ubb.codeandcoffee.proyectoSemestral.controladores.SujetoEstudioController;
import ubb.codeandcoffee.proyectoSemestral.controladores.UsuarioController;
import ubb.codeandcoffee.proyectoSemestral.controladores.UsuarioSujetoController;
import ubb.codeandcoffee.proyectoSemestral.modelo.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class ProyectoSemestralApplicationTests {
    @Autowired
    private UsuarioController uc;
    @Autowired
    private SujetoEstudioController sec;
    @Autowired
    private UsuarioSujetoController usc;

	@Test
	void contextLoads() {
	}


    @Test
    @Order(1)
    void crudUsuario() {
        Usuario usuario = new Usuario();
        usuario.setNombre("Usuariotest");
        usuario.setCorreo("correo@test.cl");
        usuario.setContraseña("12345");
        usuario.setEstado(Estado.INICIADO);
        usuario.setRol(Rol.RECOLECTOR_DE_DATOS);
        //Create
        Usuario guardado = uc.guardarUsuario(usuario);

        int id = guardado.getId_usuario();
        //Read
        assertEquals(guardado.getCorreo(), uc.getUsuarioById(id).get().getCorreo());

        usuario.setCorreo("otro@correo");
        //Update
        uc.updateUsuarioById(usuario, id);
        assertEquals("otro@correo", uc.getUsuarioById(id).get().getCorreo());
        //Delete Usuario
        assertEquals("El Usuario con id: "+ id +" fue eliminado exitosamente",
                uc.deleteUsuarioById(id));
    }

    /* necesita el contexto web para verificarse el funcionamiento completo :( (sino no puede obtener la autenticación)
    @Test
    @Order(2)
    void crudSujeto() {
        SujetoEstudio suj = new SujetoEstudio();
        suj.setNombre("sujeto testing crud");
        suj.setTipo("CO");
        suj.setDireccion("casa");
        //Create
        SujetoEstudio guardado = sec.guardarSujetoEstudio(suj);

        codigo_sujeto id = guardado.getCodigo_sujeto();
        //Read
        assertEquals(guardado.getDireccion(), sec.getSujetoEstudioById(id).get().getDireccion());

        suj.setDireccion("otra casa");
        //Update
        sec.updateSujetoEstudioById(suj, id);
        assertEquals("otra casa", sec.getSujetoEstudioById(id).get().getDireccion());
        //Delete Sujeto
        assertEquals("El Sujeto con id: "+ id +" fue eliminado exitosamente",
                sec.deleteSujetoEstudioById(id));
    }

     */

    /* en teoría debería ser automático por triggers en bdd
    @Test
    @Order(3)
    void crudUsuj() {
        Usuario usuario = new Usuario();
        usuario.setNombre("Usuariotest");
        usuario.setCorreo("correo@test.cl");
        usuario.setContraseña("12345");
        usuario.setEstado(Estado.INICIADO);
        usuario.setRol(Rol.RECOLECTOR_DE_DATOS);
        usuario = uc.guardarUsuario(usuario);

        int id1 = usuario.getId_usuario();

        SujetoEstudio suj = new SujetoEstudio();
        suj.setNombre("sujeto testing crud");
        suj.setTipo("CO");
        suj.setDireccion("casa");
        //Create
        suj = sec.guardarSujetoEstudio(suj);

        codigo_sujeto id2 = suj.getCodigo_sujeto();

        usuario = uc.getUsuarioById(id1).get();
        suj = sec.getSujetoEstudioById(id2).get();

        Usuario_Sujeto nuevo =  new Usuario_Sujeto();
        nuevo.setUsuario(usuario);
        nuevo.setSujetoEstudio(suj);
        nuevo.setAccion("Creacion de sujeto");
        nuevo = usc.guardarUsuarioSujeto(nuevo);

        System.out.println("Date iniciado: "+nuevo.getFecha().toString());

        Integer id3 = nuevo.getId();

        assertEquals("El Usuario con id: "+ id1 +" fue eliminado exitosamente",
                uc.deleteUsuarioById(id1));
        assertEquals("El Sujeto con id: "+ id2 +" fue eliminado exitosamente",
                sec.deleteSujetoEstudioById(id2));
        assertEquals("El usuj con id: "+ id3 +" fue eliminado exitosamente",
                usc.deleteUsuarioSujetoById(id3));
    }

     */
}
