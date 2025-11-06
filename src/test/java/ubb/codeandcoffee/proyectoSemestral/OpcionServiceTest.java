package ubb.codeandcoffee.proyectoSemestral;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ubb.codeandcoffee.proyectoSemestral.modelo.DatoSolicitado;
import ubb.codeandcoffee.proyectoSemestral.modelo.Opcion;
import ubb.codeandcoffee.proyectoSemestral.repositorios.DatoSolicitadoRepository;
import ubb.codeandcoffee.proyectoSemestral.repositorios.OpcionRepository;
import ubb.codeandcoffee.proyectoSemestral.servicios.OpcionService;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class OpcionServiceTest {

    @Mock
    private OpcionRepository opcionRepository;
    
    @Mock
    private DatoSolicitadoRepository datoSolicitadoRepository;

    @InjectMocks
    private OpcionService opcionService;

    @Test
    void guardarOpcion_ConDatosValidos_DebeGuardarExitosamente() {

        DatoSolicitado datoFantasma = new DatoSolicitado();
        datoFantasma.setId_dato(1);

        Opcion opcionAGuardar = new Opcion();
        opcionAGuardar.setNombre("Opción de Prueba (Sí)");
        opcionAGuardar.setDatoSolicitado(datoFantasma);

        DatoSolicitado datoReal = new DatoSolicitado();
        datoReal.setId_dato(1);
        datoReal.setNombre("Pregunta de prueba");
        
        when(datoSolicitadoRepository.findById(1)).thenReturn(Optional.of(datoReal));
        when(opcionRepository.save(any(Opcion.class))).thenAnswer(i -> i.getArguments()[0]);

        Opcion resultado = opcionService.guardarOpcion(opcionAGuardar); 

        assertThat(resultado).isNotNull();
        assertThat(resultado.getDatoSolicitado().getNombre()).isEqualTo("Pregunta de prueba");
        verify(datoSolicitadoRepository, times(1)).findById(1);
        verify(opcionRepository, times(1)).save(opcionAGuardar);
    }

    @Test
    void guardarOpcion_CuandoDatoSolicitadoNoExiste_DebeLanzarExcepcion() {
        DatoSolicitado datoFantasma = new DatoSolicitado();
        datoFantasma.setId_dato(99); 

        Opcion opcionAGuardar = new Opcion();
        opcionAGuardar.setNombre("Opción de Prueba (No)");
        opcionAGuardar.setDatoSolicitado(datoFantasma);

        when(datoSolicitadoRepository.findById(99)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            opcionService.guardarOpcion(opcionAGuardar); 
        });

        assertThat(exception.getMessage()).isEqualTo("Error: La sección con ID 99 no existe.");

        verify(opcionRepository, never()).save(any(Opcion.class));
    }
    
    @Test
    void actualizarOpcion_DebeActualizarNombre() {
        Integer idOpcion = 1;
        
        Opcion request = new Opcion();
        request.setNombre("Nombre Actualizado");

        Opcion opcionEnBD = new Opcion();
        opcionEnBD.setId_opcion(idOpcion);
        opcionEnBD.setNombre("Nombre Viejo");

        when(opcionRepository.findById(idOpcion)).thenReturn(Optional.of(opcionEnBD));
        when(opcionRepository.save(any(Opcion.class))).thenAnswer(i -> i.getArguments()[0]);

        Opcion resultado = opcionService.updateById(request, idOpcion);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getNombre()).isEqualTo("Nombre Actualizado");
        verify(opcionRepository, times(1)).findById(idOpcion);
        verify(opcionRepository, times(1)).save(opcionEnBD);
    }

    @Test
    void eliminarOpcion_DebeLlamarADeleteById() {
        Integer idParaBorrar = 1;
        doNothing().when(opcionRepository).deleteById(idParaBorrar);

        opcionService.deleteOpcion(idParaBorrar);


        verify(opcionRepository, times(1)).deleteById(idParaBorrar);
    }
}