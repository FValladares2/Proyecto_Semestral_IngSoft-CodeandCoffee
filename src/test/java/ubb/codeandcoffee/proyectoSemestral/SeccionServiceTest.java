package ubb.codeandcoffee.proyectoSemestral;

// Imports necesarios
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
    private SeccionService seccionService; // Asumo que se llama así

    // ----------------------------------------------
    // Pruebas para guardarSeccion (NUEVAS)
    // ----------------------------------------------

    @Test
    void guardarSeccion_ConDatosValidos_DebeGuardarExitosamente() {
        // --- 1. ARRANGE ---
        // La seccion que queremos guardar
        Seccion seccionAGuardar = new Seccion();
        seccionAGuardar.setNombre("Nueva Seccion");
        seccionAGuardar.setNumero(1);

        // Lo que simulamos que devuelve el save
        Seccion seccionGuardada = new Seccion();
        seccionGuardada.setId_seccion(10); // ID simulado
        seccionGuardada.setNombre("Nueva Seccion");
        seccionGuardada.setNumero(1);

        // Configuramos el mock:
        // "CUANDO se llame a save(...), ENTONCES devuelve la seccionGuardada"
        when(seccionRepository.save(any(Seccion.class))).thenReturn(seccionGuardada);

        // --- 2. ACT ---
        // Asumo que tu método se llama 'guardarSeccion'
        Seccion resultado = seccionService.guardarSeccion(seccionAGuardar); 

        // --- 3. ASSERT ---
        assertThat(resultado).isNotNull();
        assertThat(resultado.getId_seccion()).isEqualTo(10); // Verificamos que tiene el ID
        verify(seccionRepository, times(1)).save(seccionAGuardar); // Verificamos que se llamó a save
    }

    @Test
    void guardarSeccion_CuandoNombreEsNulo_DebeLanzarExcepcion() {
        // --- 1. ARRANGE ---
        Seccion seccionSinNombre = new Seccion();
        seccionSinNombre.setNombre(null); // <-- El dato inválido
        seccionSinNombre.setNumero(1);

        // Asumimos que tu servicio valida esto (¡debería!)
        // "CUANDO se llame al método, ENTONCES debe lanzar esta excepción"
        
        // --- 2. ACT & 3. ASSERT ---
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            seccionService.guardarSeccion(seccionSinNombre);
        });

        // Verificamos el mensaje de error
        assertThat(exception.getMessage()).isEqualTo("El nombre es obligatorio");
        
        // Verificamos que NUNCA se intentó guardar
        verify(seccionRepository, never()).save(any(Seccion.class)); 
    }

    // ----------------------------------------------
    // Pruebas para updateById (LA QUE TENÍAS + LA NUEVA)
    // ----------------------------------------------

    @Test
    void updateById_DebeActualizarNombreYNumero() {
        // --- 1. ARRANGE ---
        // El objeto 'request' que viene del controlador (los cambios)
        Seccion request = new Seccion();
        request.setNombre("Nuevo Nombre");
        request.setNumero(2);

        // La seccion que ya existe en la BD
        Seccion seccionEnBD = new Seccion();
        seccionEnBD.setId_seccion(1);
        seccionEnBD.setNombre("Nombre Viejo");
        seccionEnBD.setNumero(1);

        // Configuramos los mocks:
        when(seccionRepository.findById(1)).thenReturn(Optional.of(seccionEnBD));
        when(seccionRepository.save(any(Seccion.class))).thenAnswer(i -> i.getArguments()[0]);

        // --- 2. ACT ---
        // Llamamos al método de actualización que arreglamos antes
        Seccion resultado = seccionService.updateById(request, 1);

        // --- 3. ASSERT ---
        assertThat(resultado).isNotNull();
        assertThat(resultado.getNombre()).isEqualTo("Nuevo Nombre"); // Verificamos que el nombre cambió
        assertThat(resultado.getNumero()).isEqualTo(2); // Verificamos que el número cambió
        assertThat(resultado.getId_seccion()).isEqualTo(1); // El ID no debe cambiar
        
        verify(seccionRepository, times(1)).findById(1); // Se buscó
        verify(seccionRepository, times(1)).save(seccionEnBD); // Se guardó
    }
    
    @Test
    void updateById_ConCamposNulos_SoloDebeActualizarCamposPresentes() {
        // --- 1. ARRANGE ---
        // El request solo trae el nombre, el número es nulo
        Seccion request = new Seccion();
        request.setNombre("Solo Nombre Nuevo");
        request.setNumero(null); // <-- Campo nulo

        // La seccion en la BD tiene un número original
        Seccion seccionEnBD = new Seccion();
        seccionEnBD.setId_seccion(1);
        seccionEnBD.setNombre("Nombre Viejo");
        seccionEnBD.setNumero(5); // <-- Número original

        when(seccionRepository.findById(1)).thenReturn(Optional.of(seccionEnBD));
        when(seccionRepository.save(any(Seccion.class))).thenAnswer(i -> i.getArguments()[0]);

        // --- 2. ACT ---
        Seccion resultado = seccionService.updateById(request, 1);

        // --- 3. ASSERT ---
        // Verificamos que el método de update (el que te pasé) fue inteligente:
        assertThat(resultado).isNotNull();
        assertThat(resultado.getNombre()).isEqualTo("Solo Nombre Nuevo"); // El nombre SÍ cambió
        assertThat(resultado.getNumero()).isEqualTo(5); // El número NO cambió (se quedó con el 5 original)
    }

    // ----------------------------------------------
    // Prueba para eliminar (NUEVA)
    // ----------------------------------------------
    
    @Test
    void eliminarSeccion_DebeLlamarADeleteById() {
        // --- 1. ARRANGE ---
        Integer idParaBorrar = 1;
        // doNothing() se usa para métodos 'void' (que no devuelven nada)
        doNothing().when(seccionRepository).deleteById(idParaBorrar);

        // --- 2. ACT ---
        // Asumo que tu método de borrado se llama así
        seccionService.deleteSeccion(idParaBorrar); 

        // --- 3. ASSERT ---
        // Verificamos que el método deleteById SÍ fue llamado, una vez, con el ID 1.
        verify(seccionRepository, times(1)).deleteById(idParaBorrar);
    }
}