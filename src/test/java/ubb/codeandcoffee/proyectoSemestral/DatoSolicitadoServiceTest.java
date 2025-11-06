package ubb.codeandcoffee.proyectoSemestral;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ubb.codeandcoffee.proyectoSemestral.modelo.DatoSolicitado;
import ubb.codeandcoffee.proyectoSemestral.modelo.Seccion;
import ubb.codeandcoffee.proyectoSemestral.repositorios.DatoSolicitadoRepository;
import ubb.codeandcoffee.proyectoSemestral.repositorios.SeccionRepository;
import ubb.codeandcoffee.proyectoSemestral.servicios.DatoSolicitadoService;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class DatoSolicitadoServiceTest {

    @Mock
    private DatoSolicitadoRepository datoRepository;
    
    @Mock
    private SeccionRepository seccionRepository;


    @InjectMocks
    private DatoSolicitadoService datoService;

    @Test
    void guardarDatoSolicitado_ConDatosValidos_DebeGuardarExitosamente() {

        Seccion seccionFantasma = new Seccion();
        seccionFantasma.setId_seccion(1);

        DatoSolicitado datoAGuardar = new DatoSolicitado();
        datoAGuardar.setNombre("Test Nombre");
        datoAGuardar.setEstudio(true);
        datoAGuardar.setSeccion(seccionFantasma);

        Seccion seccionReal = new Seccion();
        seccionReal.setId_seccion(1);
        seccionReal.setNombre("Seccion de Prueba");

        when(seccionRepository.findById(1)).thenReturn(Optional.of(seccionReal));

        when(datoRepository.save(any(DatoSolicitado.class))).thenAnswer(i -> i.getArguments()[0]);

        DatoSolicitado resultado = datoService.guardarDatoSolicitado(datoAGuardar);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getSeccion().getNombre()).isEqualTo("Seccion de Prueba");

        verify(seccionRepository, times(1)).findById(1);
        verify(datoRepository, times(1)).save(datoAGuardar);
    }

    @Test
    void guardarDatoSolicitado_CuandoSeccionNoExiste_DebeLanzarExcepcion() {
        Seccion seccionFantasma = new Seccion();
        seccionFantasma.setId_seccion(99);

        DatoSolicitado datoAGuardar = new DatoSolicitado();
        datoAGuardar.setNombre("Test Nombre");
        datoAGuardar.setEstudio(true);
        datoAGuardar.setSeccion(seccionFantasma);

        when(seccionRepository.findById(99)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            datoService.guardarDatoSolicitado(datoAGuardar);
        });

        assertThat(exception.getMessage()).isEqualTo("Error: La sección con ID 99 no existe.");

        verify(datoRepository, never()).save(any(DatoSolicitado.class));
    }

    @Test
    void guardarDatoSolicitado_CuandoNombreEsNulo_DebeLanzarExcepcion() {
        DatoSolicitado datoAGuardar = new DatoSolicitado();
        datoAGuardar.setNombre(null);
        datoAGuardar.setEstudio(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            datoService.guardarDatoSolicitado(datoAGuardar);
        });

        assertThat(exception.getMessage()).isEqualTo("El nombre es obligatorio");
        verify(datoRepository, never()).save(any(DatoSolicitado.class));
    }
    
    @Test
    void eliminarDatoSolicitado_DebeLlamarADeleteById() {

        Integer idParaBorrar = 1;

        doNothing().when(datoRepository).deleteById(idParaBorrar);


        datoService.deleteDatoSolicitado(idParaBorrar);

        verify(datoRepository, times(1)).deleteById(idParaBorrar);
    }
}