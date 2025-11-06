package ubb.codeandcoffee.proyectoSemestral;

// Imports de JUnit 5 y Mockito
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

// Imports de tus clases
import ubb.codeandcoffee.proyectoSemestral.modelo.DatoSolicitado;
import ubb.codeandcoffee.proyectoSemestral.modelo.Opcion;
import ubb.codeandcoffee.proyectoSemestral.repositorios.DatoSolicitadoRepository;
import ubb.codeandcoffee.proyectoSemestral.repositorios.OpcionRepository;
import ubb.codeandcoffee.proyectoSemestral.servicios.OpcionService;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class OpcionServiceTest {

    // 1. Mocks (Dependencias simuladas)
    @Mock
    private OpcionRepository opcionRepository;
    
    @Mock
    private DatoSolicitadoRepository datoSolicitadoRepository; // Dependencia necesaria

    // 2. Clase bajo prueba
    @InjectMocks
    private OpcionService opcionService; // Asumo que se llama así

    // ----------------------------------------------
    // Pruebas para guardarOpcion (El método más complejo)
    // ----------------------------------------------

    @Test
    void guardarOpcion_ConDatosValidos_DebeGuardarExitosamente() {
        // --- 1. ARRANGE (Organizar) ---
        
        // 1.a. Creamos el DatoSolicitado "fantasma" (como vendría del JSON)
        DatoSolicitado datoFantasma = new DatoSolicitado();
        datoFantasma.setId_dato(1); // Asumimos que el setter es setId_dato

        // 1.b. Creamos la Opcion y le asignamos el "fantasma"
        Opcion opcionAGuardar = new Opcion();
        opcionAGuardar.setNombre("Opción de Prueba (Sí)");
        opcionAGuardar.setDatoSolicitado(datoFantasma); // <-- ¡ESTE ES EL ARREGLO!

        // 1.c. Preparamos las respuestas de los Mocks
        DatoSolicitado datoReal = new DatoSolicitado();
        datoReal.setId_dato(1);
        datoReal.setNombre("Pregunta de prueba");
        
        when(datoSolicitadoRepository.findById(1)).thenReturn(Optional.of(datoReal));
        when(opcionRepository.save(any(Opcion.class))).thenAnswer(i -> i.getArguments()[0]);

        // --- 2. ACT (Actuar) ---
        // Llamamos al método con UN solo parámetro
        Opcion resultado = opcionService.guardarOpcion(opcionAGuardar); 

        // --- 3. ASSERT (Afirmar) ---
        assertThat(resultado).isNotNull();
        // Verificamos que el servicio reemplazó el "fantasma" por el "real"
        assertThat(resultado.getDatoSolicitado().getNombre()).isEqualTo("Pregunta de prueba");
        verify(datoSolicitadoRepository, times(1)).findById(1);
        verify(opcionRepository, times(1)).save(opcionAGuardar);
    }

    @Test
    void guardarOpcion_CuandoDatoSolicitadoNoExiste_DebeLanzarExcepcion() {
        // --- 1. ARRANGE ---
        
        // 1.a. Creamos el "fantasma" con un ID que no existe
        DatoSolicitado datoFantasma = new DatoSolicitado();
        datoFantasma.setId_dato(99); 

        // 1.b. Creamos la Opcion y le asignamos el "fantasma"
        Opcion opcionAGuardar = new Opcion();
        opcionAGuardar.setNombre("Opción de Prueba (No)");
        opcionAGuardar.setDatoSolicitado(datoFantasma); // <-- ¡ESTE ES EL ARREGLO!

        // 1.c. Configuramos el mock para que falle
        when(datoSolicitadoRepository.findById(99)).thenReturn(Optional.empty());

        // --- 2. ACT & 3. ASSERT ---
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            // Llamamos al método con UN solo parámetro
            opcionService.guardarOpcion(opcionAGuardar); 
        });

        // Verificamos el mensaje de error (tuve que cambiarlo un poco
        // para que coincida con tu nuevo código)
        assertThat(exception.getMessage()).isEqualTo("Error: La sección con ID 99 no existe.");

        verify(opcionRepository, never()).save(any(Opcion.class));
    }

    // ----------------------------------------------
    // Pruebas para el resto del CRUD (Actualizar, Borrar)
    // ----------------------------------------------
    
    @Test
    void actualizarOpcion_DebeActualizarNombre() {
        // --- 1. ARRANGE ---
        Integer idOpcion = 1;
        
        Opcion request = new Opcion();
        request.setNombre("Nombre Actualizado");

        Opcion opcionEnBD = new Opcion();
        opcionEnBD.setId_opcion(idOpcion); // Asumo que se llama así el ID
        opcionEnBD.setNombre("Nombre Viejo");

        when(opcionRepository.findById(idOpcion)).thenReturn(Optional.of(opcionEnBD));
        when(opcionRepository.save(any(Opcion.class))).thenAnswer(i -> i.getArguments()[0]);

        // --- 2. ACT ---
        Opcion resultado = opcionService.updateById(request, idOpcion); // Asumo que se llama así

        // --- 3. ASSERT ---
        assertThat(resultado).isNotNull();
        assertThat(resultado.getNombre()).isEqualTo("Nombre Actualizado");
        verify(opcionRepository, times(1)).findById(idOpcion);
        verify(opcionRepository, times(1)).save(opcionEnBD);
    }

    @Test
    void eliminarOpcion_DebeLlamarADeleteById() {
        // --- 1. ARRANGE ---
        Integer idParaBorrar = 1;
        doNothing().when(opcionRepository).deleteById(idParaBorrar);

        // --- 2. ACT ---
        opcionService.deleteOpcion(idParaBorrar); // Asumo que se llama así

        // --- 3. ASSERT ---
        verify(opcionRepository, times(1)).deleteById(idParaBorrar);
    }
}