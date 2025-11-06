package ubb.codeandcoffee.proyectoSemestral;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ubb.codeandcoffee.proyectoSemestral.modelo.Seccion;
import ubb.codeandcoffee.proyectoSemestral.repositorios.SeccionRepository;
import ubb.codeandcoffee.proyectoSemestral.servicios.SeccionService;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class SeccionServiceTest {

    @Mock
    private SeccionRepository seccionRepository;

    @InjectMocks
    private SeccionService seccionService;

    @Test
    void guardarSeccion_ConDatosValidos_DebeGuardarExitosamente() {
        Seccion seccionAGuardar = new Seccion();
        seccionAGuardar.setNombre("Nueva Seccion");
        seccionAGuardar.setNumero(1);

        Seccion seccionGuardada = new Seccion();
        seccionGuardada.setId_seccion(10);
        seccionGuardada.setNombre("Nueva Seccion");
        seccionGuardada.setNumero(1);

        when(seccionRepository.save(any(Seccion.class))).thenReturn(seccionGuardada);

        Seccion resultado = seccionService.guardarSeccion(seccionAGuardar); 

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId_seccion()).isEqualTo(10); // Verificamos que tiene el ID
        verify(seccionRepository, times(1)).save(seccionAGuardar); // Verificamos que se llamó a save
    }

    @Test
    void guardarSeccion_CuandoNombreEsNulo_DebeLanzarExcepcion() {
        Seccion seccionSinNombre = new Seccion();
        seccionSinNombre.setNombre(null);
        seccionSinNombre.setNumero(1);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            seccionService.guardarSeccion(seccionSinNombre);
        });

        assertThat(exception.getMessage()).isEqualTo("El nombre es obligatorio");

        verify(seccionRepository, never()).save(any(Seccion.class)); 
    }

    @Test
    void updateById_DebeActualizarNombreYNumero() {
        Seccion request = new Seccion();
        request.setNombre("Nuevo Nombre");
        request.setNumero(2);

        Seccion seccionEnBD = new Seccion();
        seccionEnBD.setId_seccion(1);
        seccionEnBD.setNombre("Nombre Viejo");
        seccionEnBD.setNumero(1);

        when(seccionRepository.findById(1)).thenReturn(Optional.of(seccionEnBD));
        when(seccionRepository.save(any(Seccion.class))).thenAnswer(i -> i.getArguments()[0]);

        Seccion resultado = seccionService.updateById(request, 1);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getNombre()).isEqualTo("Nuevo Nombre");
        assertThat(resultado.getNumero()).isEqualTo(2);
        assertThat(resultado.getId_seccion()).isEqualTo(1);
        
        verify(seccionRepository, times(1)).findById(1);
        verify(seccionRepository, times(1)).save(seccionEnBD);
    }
    
    @Test
    void updateById_ConCamposNulos_SoloDebeActualizarCamposPresentes() {
        Seccion request = new Seccion();
        request.setNombre("Solo Nombre Nuevo");
        request.setNumero(null);

        Seccion seccionEnBD = new Seccion();
        seccionEnBD.setId_seccion(1);
        seccionEnBD.setNombre("Nombre Viejo");
        seccionEnBD.setNumero(5);

        when(seccionRepository.findById(1)).thenReturn(Optional.of(seccionEnBD));
        when(seccionRepository.save(any(Seccion.class))).thenAnswer(i -> i.getArguments()[0]);

        // --- 2. ACT ---
        Seccion resultado = seccionService.updateById(request, 1);

        // --- 3. ASSERT ---
        // Verificamos que el método de update (el que te pasé) fue inteligente:
        assertThat(resultado).isNotNull();
        assertThat(resultado.getNombre()).isEqualTo("Solo Nombre Nuevo");
        assertThat(resultado.getNumero()).isEqualTo(5);
    }

    @Test
    void eliminarSeccion_DebeLlamarADeleteById() {
        Integer idParaBorrar = 1;

        doNothing().when(seccionRepository).deleteById(idParaBorrar);

        seccionService.deleteSeccion(idParaBorrar); 

        verify(seccionRepository, times(1)).deleteById(idParaBorrar);
    }
}