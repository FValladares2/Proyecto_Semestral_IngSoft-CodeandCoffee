package ubb.codeandcoffee.proyectoSemestral;

// Imports de JUnit 5 y Mockito
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*; // Para asserts (afirmaciones)
import static org.junit.jupiter.api.Assertions.assertThrows;

// Imports de tus clases
import ubb.codeandcoffee.proyectoSemestral.modelo.DatoSolicitado;
import ubb.codeandcoffee.proyectoSemestral.modelo.Seccion;
import ubb.codeandcoffee.proyectoSemestral.repositorios.DatoSolicitadoRepository;
import ubb.codeandcoffee.proyectoSemestral.repositorios.SeccionRepository;
import ubb.codeandcoffee.proyectoSemestral.servicios.DatoSolicitadoService;

import java.util.Optional;

@ExtendWith(MockitoExtension.class) // <-- Habilita Mockito
class DatoSolicitadoServiceTest {

    // 1. Mocks (Dependencias simuladas)
    // Simula los repositorios para que no usen la BD real
    @Mock
    private DatoSolicitadoRepository datoRepository;
    
    @Mock
    private SeccionRepository seccionRepository;

    // 2. Clase bajo prueba
    // Crea una instancia real del servicio e inyecta los Mocks de arriba
    @InjectMocks
    private DatoSolicitadoService datoService;

    // ----------------------------------------------
    // Pruebas para guardarDatoSolicitado
    // ----------------------------------------------

    @Test
    void guardarDatoSolicitado_ConDatosValidos_DebeGuardarExitosamente() {
        // --- 1. ARRANGE (Organizar) ---
        // Preparamos los datos de entrada
        Seccion seccionFantasma = new Seccion();
        seccionFantasma.setId_seccion(1);

        DatoSolicitado datoAGuardar = new DatoSolicitado();
        datoAGuardar.setNombre("Test Nombre");
        datoAGuardar.setEstudio(true);
        datoAGuardar.setSeccion(seccionFantasma);

        // Preparamos las respuestas de los Mocks
        Seccion seccionReal = new Seccion();
        seccionReal.setId_seccion(1);
        seccionReal.setNombre("Seccion de Prueba");
        
        // Configuramos los mocks:
        // "CUANDO se llame a seccionRepository.findById(1), ENTONCES devuelve la seccionReal"
        when(seccionRepository.findById(1)).thenReturn(Optional.of(seccionReal));
        
        // "CUANDO se llame a datoRepository.save(...), ENTONCES devuelve el mismo objeto que se le pasó"
        when(datoRepository.save(any(DatoSolicitado.class))).thenAnswer(i -> i.getArguments()[0]);

        // --- 2. ACT (Actuar) ---
        DatoSolicitado resultado = datoService.guardarDatoSolicitado(datoAGuardar);

        // --- 3. ASSERT (Afirmar) ---
        assertThat(resultado).isNotNull();
        // Verificamos que el servicio reemplazó la sección "fantasma" por la "real"
        assertThat(resultado.getSeccion().getNombre()).isEqualTo("Seccion de Prueba");
        
        // Verificamos que los mocks fueron llamados
        verify(seccionRepository, times(1)).findById(1); // Verificamos que SÍ se buscó la sección
        verify(datoRepository, times(1)).save(datoAGuardar); // Verificamos que SÍ se guardó
    }

    @Test
    void guardarDatoSolicitado_CuandoSeccionNoExiste_DebeLanzarExcepcion() {
        // --- 1. ARRANGE ---
        Seccion seccionFantasma = new Seccion();
        seccionFantasma.setId_seccion(99); // ID que no existe

        DatoSolicitado datoAGuardar = new DatoSolicitado();
        datoAGuardar.setNombre("Test Nombre");
        datoAGuardar.setEstudio(true);
        datoAGuardar.setSeccion(seccionFantasma);

        // Configuramos el mock para que falle:
        // "CUANDO se llame a seccionRepository.findById(99), ENTONCES devuelve un Optional vacío"
        when(seccionRepository.findById(99)).thenReturn(Optional.empty());

        // --- 2. ACT & 3. ASSERT ---
        // Verificamos que el método lanza la excepción que esperamos
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            datoService.guardarDatoSolicitado(datoAGuardar);
        });

        // Verificamos que el mensaje de error es el que programamos
        assertThat(exception.getMessage()).isEqualTo("Error: La sección con ID 99 no existe.");

        // MUY IMPORTANTE: Verificamos que el 'save' NUNCA fue llamado
        verify(datoRepository, never()).save(any(DatoSolicitado.class));
    }

    @Test
    void guardarDatoSolicitado_CuandoNombreEsNulo_DebeLanzarExcepcion() {
        // --- 1. ARRANGE ---
        DatoSolicitado datoAGuardar = new DatoSolicitado();
        datoAGuardar.setNombre(null); // <-- El dato inválido
        datoAGuardar.setEstudio(true);

        // --- 2. ACT & 3. ASSERT ---
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            datoService.guardarDatoSolicitado(datoAGuardar);
        });

        assertThat(exception.getMessage()).isEqualTo("El nombre es obligatorio");
        verify(datoRepository, never()).save(any(DatoSolicitado.class)); // El guardado nunca debe ocurrir
    }

    // ----------------------------------------------
    // Prueba para eliminar (CRUD)
    // ----------------------------------------------
    
    @Test
    void eliminarDatoSolicitado_DebeLlamarADeleteById() {
        // --- 1. ARRANGE ---
        Integer idParaBorrar = 1;
        // doNothing() se usa para métodos 'void' (que no devuelven nada)
        // Le decimos a Mockito que no haga nada cuando se llame a deleteById.
        doNothing().when(datoRepository).deleteById(idParaBorrar);

        // --- 2. ACT ---
        datoService.deleteDatoSolicitado(idParaBorrar); // Asumo que tu método se llama así

        // --- 3. ASSERT ---
        // Verificamos que el método deleteById SÍ fue llamado, una vez, con el ID 1.
        verify(datoRepository, times(1)).deleteById(idParaBorrar);
    }
}