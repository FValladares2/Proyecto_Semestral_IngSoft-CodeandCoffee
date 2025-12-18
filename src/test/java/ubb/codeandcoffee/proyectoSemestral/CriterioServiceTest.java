package ubb.codeandcoffee.proyectoSemestral;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ubb.codeandcoffee.proyectoSemestral.modelo.Criterio;
import ubb.codeandcoffee.proyectoSemestral.modelo.Tipo_Calculo;
import ubb.codeandcoffee.proyectoSemestral.repositorios.CriterioRepository;
import ubb.codeandcoffee.proyectoSemestral.servicios.CriterioService;

import java.util.ArrayList;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class CriterioServiceTest {

    @Mock
    private CriterioRepository criterioRepository;

    @InjectMocks
    private CriterioService criterioService;

    private Criterio criterioPrueba;


    private final Tipo_Calculo tipoCalculoPrueba = Tipo_Calculo.PROMEDIO;// Ejemplo si es Enum

    @BeforeEach
    void setUp() {
        criterioPrueba = new Criterio();
        criterioPrueba.setNombre("Criterio Valido");
        criterioPrueba.setExpresion("a > 10");
        criterioPrueba.setTipoCalculo(tipoCalculoPrueba);
    }


    @Test
    void getCriterios_DebeDevolverListaDeCriterios() {
        ArrayList<Criterio> lista = new ArrayList<>();
        lista.add(criterioPrueba);
        when(criterioRepository.findAll()).thenReturn(lista);

        ArrayList<Criterio> resultado = criterioService.getCriterios();

        assertThat(resultado).isNotNull();
        assertThat(resultado.size()).isEqualTo(1);
        verify(criterioRepository, times(1)).findAll();
    }

    @Test
    void guardarCriterio_ConDatosValidos_DebeGuardarExitosamente() {
        when(criterioRepository.save(any(Criterio.class))).thenReturn(criterioPrueba);

        Criterio resultado = criterioService.guardarCriterio(criterioPrueba);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getNombre()).isEqualTo("Criterio Valido");
        verify(criterioRepository, times(1)).save(criterioPrueba);
    }

    @Test
    void guardarCriterio_CuandoNombreEsNulo_DebeLanzarExcepcion() {
        criterioPrueba.setNombre(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> criterioService.guardarCriterio(criterioPrueba));

        assertThat(exception.getMessage()).isEqualTo("El nombre del criterio es obligatorio");
        verify(criterioRepository, never()).save(any(Criterio.class));
    }

    @Test
    void guardarCriterio_CuandoNombreEstaVacio_DebeLanzarExcepcion() {
        criterioPrueba.setNombre("");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> criterioService.guardarCriterio(criterioPrueba));

        assertThat(exception.getMessage()).isEqualTo("El nombre del criterio es obligatorio");
        verify(criterioRepository, never()).save(any(Criterio.class));
    }

    @Test
    void guardarCriterio_CuandoExpresionEsNula_DebeLanzarExcepcion() {
        criterioPrueba.setExpresion(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> criterioService.guardarCriterio(criterioPrueba));

        assertThat(exception.getMessage()).isEqualTo("La expresión del criterio es obligatoria");
        verify(criterioRepository, never()).save(any(Criterio.class));
    }

    @Test
    void guardarCriterio_CuandoTipoCalculoEsNulo_DebeLanzarExcepcion() {
        criterioPrueba.setTipoCalculo(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> criterioService.guardarCriterio(criterioPrueba));

        assertThat(exception.getMessage()).isEqualTo("El tipo de cálculo es obligatorio");
        verify(criterioRepository, never()).save(any(Criterio.class));
    }

    @Test
    void getById_CuandoExiste_DebeDevolverOptionalConDatos() {
        when(criterioRepository.findById(1)).thenReturn(Optional.of(criterioPrueba));

        Optional<Criterio> resultado = criterioService.getById(1);

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getNombre()).isEqualTo("Criterio Valido");
        verify(criterioRepository, times(1)).findById(1);
    }

    @Test
    void getById_CuandoNoExiste_DebeDevolverOptionalVacio() {
        when(criterioRepository.findById(99)).thenReturn(Optional.empty());

        Optional<Criterio> resultado = criterioService.getById(99);

        assertThat(resultado).isNotPresent();
        verify(criterioRepository, times(1)).findById(99);
    }

    @Test
    void updateById_CuandoExiste_DebeActualizarParcialmente() {
        Criterio request = new Criterio();
        request.setNombre("Nombre Actualizado");
        request.setNombreStata("nuevo_stata");

        when(criterioRepository.findById(1)).thenReturn(Optional.of(criterioPrueba));
        when(criterioRepository.save(any(Criterio.class))).thenAnswer(i -> i.getArguments()[0]);

        Criterio resultado = criterioService.updateById(request, 1);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getNombre()).isEqualTo("Nombre Actualizado");
        assertThat(resultado.getNombreStata()).isEqualTo("nuevo_stata");
        assertThat(resultado.getExpresion()).isEqualTo("a > 10");
        assertThat(resultado.getTipoCalculo()).isEqualTo(tipoCalculoPrueba);

        verify(criterioRepository, times(1)).findById(1);
        verify(criterioRepository, times(1)).save(criterioPrueba);
    }

    @Test
    void updateById_CuandoNoExiste_DebeLanzarExcepcion() {
        Criterio request = new Criterio();
        request.setNombre("Nombre Actualizado");

        when(criterioRepository.findById(99)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> criterioService.
                updateById(request, 99));

        assertThat(exception.getMessage()).isEqualTo("Criterio no encontrado");
        verify(criterioRepository, times(1)).findById(99);
        verify(criterioRepository, never()).save(any(Criterio.class));
    }

    @Test
    void deleteCriterio_CuandoExitoso_DebeDevolverTrue() {
        Integer id = 1;
        doNothing().when(criterioRepository).deleteById(id);

        Boolean resultado = criterioService.deleteCriterio(id);

        assertThat(resultado).isTrue();
        verify(criterioRepository, times(1)).deleteById(id);
    }

    @Test
    void deleteCriterio_CuandoLanzaExcepcion_DebeDevolverFalse() {
        Integer id = 1;
        doThrow(new RuntimeException("Error de BD")).when(criterioRepository).deleteById(id);

        Boolean resultado = criterioService.deleteCriterio(id);

        assertThat(resultado).isFalse();
        verify(criterioRepository, times(1)).deleteById(id);
    }
}