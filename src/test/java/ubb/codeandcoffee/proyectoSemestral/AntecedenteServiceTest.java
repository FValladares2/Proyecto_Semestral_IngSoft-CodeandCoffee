package ubb.codeandcoffee.proyectoSemestral;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*; // Para asserts (afirmaciones)
import static org.junit.jupiter.api.Assertions.assertThrows;

import ubb.codeandcoffee.proyectoSemestral.modelo.Antecedente;
import ubb.codeandcoffee.proyectoSemestral.modelo.DatoSolicitado;
import ubb.codeandcoffee.proyectoSemestral.modelo.SujetoEstudio;
import ubb.codeandcoffee.proyectoSemestral.repositorios.AntecedenteRepository;
import ubb.codeandcoffee.proyectoSemestral.servicios.AntecedenteService;

import java.util.ArrayList;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class AntecedenteServiceTest {

    @Mock
    private AntecedenteRepository antecedenteRepository;

    @InjectMocks
    private AntecedenteService antecedenteService;

    private Antecedente antecedentePrueba;
    private SujetoEstudio sujetoPrueba;

    @BeforeEach
    void setUp() {
        // Inicializamos datos base antes de cada test
        sujetoPrueba = new SujetoEstudio();
        DatoSolicitado datoPrueba = new DatoSolicitado();

        antecedentePrueba = new Antecedente();
        antecedentePrueba.setSujetoEstudio(sujetoPrueba);
        antecedentePrueba.setDatoSolicitado(datoPrueba);
        antecedentePrueba.setValorString("Valor Original");
    }

    @Test
    void getAntecedentes_DebeDevolverListaDeAntecedentes() {
        // Arrange
        ArrayList<Antecedente> lista = new ArrayList<>();
        lista.add(antecedentePrueba);
        when(antecedenteRepository.findAll()).thenReturn(lista);

        // Act
        ArrayList<Antecedente> resultado = antecedenteService.getAntecedentes();

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.size()).isEqualTo(1);
        verify(antecedenteRepository, times(1)).findAll();
    }

    @Test
    void guardarAntecedente_ConDatosValidos_DebeGuardarExitosamente() {
        when(antecedenteRepository.save(any(Antecedente.class))).thenReturn(antecedentePrueba);

        Antecedente resultado = antecedenteService.guardarAntecedente(antecedentePrueba);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getValorString()).isEqualTo("Valor Original");
        verify(antecedenteRepository, times(1)).save(antecedentePrueba);
    }

    @Test
    void guardarAntecedente_CuandoDatoSolicitadoEsNulo_DebeLanzarExcepcion() {
        antecedentePrueba.setDatoSolicitado(null); // <-- El dato inválido

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> antecedenteService.guardarAntecedente(antecedentePrueba));

        assertThat(exception.getMessage()).isEqualTo("El DatoSolicitado es obligatorio");
        verify(antecedenteRepository, never()).save(any(Antecedente.class)); // El guardado NUNCA debe ocurrir
    }

    @Test
    void guardarAntecedente_CuandoSujetoEstudioEsNulo_DebeLanzarExcepcion() {
        antecedentePrueba.setSujetoEstudio(null); // <-- El dato inválido

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> antecedenteService.guardarAntecedente(antecedentePrueba));

        assertThat(exception.getMessage()).isEqualTo("El SujetoEstudio es obligatorio");
        verify(antecedenteRepository, never()).save(any(Antecedente.class));
    }

    @Test
    void getById_CuandoExiste_DebeDevolverOptionalConDatos() {
        when(antecedenteRepository.findById(1)).thenReturn(Optional.of(antecedentePrueba));

        Optional<Antecedente> resultado = antecedenteService.getById(1);

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getValorString()).isEqualTo("Valor Original");
        verify(antecedenteRepository, times(1)).findById(1);
    }

    @Test
    void getById_CuandoNoExiste_DebeDevolverOptionalVacio() {
        when(antecedenteRepository.findById(99)).thenReturn(Optional.empty());

        Optional<Antecedente> resultado = antecedenteService.getById(99);

        assertThat(resultado).isNotPresent();
        verify(antecedenteRepository, times(1)).findById(99);
    }

    @Test
    void updateById_CuandoExiste_DebeActualizarParcialmente() {
        Antecedente request = new Antecedente();
        request.setValorString("Valor Actualizado");
        request.setValorNum(123.45f);

        when(antecedenteRepository.findById(1)).thenReturn(Optional.of(antecedentePrueba));

        when(antecedenteRepository.save(any(Antecedente.class))).thenAnswer(i -> i.getArguments()[0]);

        Antecedente resultado = antecedenteService.updateById(request, 1);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getValorString()).isEqualTo("Valor Actualizado");
        assertThat(resultado.getValorNum()).isEqualTo(123.45f);
        assertThat(resultado.getSujetoEstudio()).isEqualTo(sujetoPrueba);

        verify(antecedenteRepository, times(1)).findById(1);
        verify(antecedenteRepository, times(1)).save(antecedentePrueba);
    }

    @Test
    void updateById_CuandoNoExiste_DebeLanzarExcepcion() {
        Antecedente request = new Antecedente();
        request.setValorString("Valor Actualizado");

        when(antecedenteRepository.findById(99)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> antecedenteService.updateById(request, 99));

        assertThat(exception.getMessage()).isEqualTo("Antecedente no encontrado");
        verify(antecedenteRepository, times(1)).findById(99);
        verify(antecedenteRepository, never()).save(any(Antecedente.class)); // Nunca se debe guardar
    }

    @Test
    void deleteAntecedente_CuandoExitoso_DebeDevolverTrue() {

        Integer id = 1;
        doNothing().when(antecedenteRepository).deleteById(id);

        Boolean resultado = antecedenteService.deleteAntecedente(id);

        assertThat(resultado).isTrue();
        verify(antecedenteRepository, times(1)).deleteById(id);
    }

    @Test
    void deleteAntecedente_CuandoLanzaExcepcion_DebeDevolverFalse() {
        Integer id = 1;
        doThrow(new RuntimeException("Error de BD")).when(antecedenteRepository).deleteById(id);

        Boolean resultado = antecedenteService.deleteAntecedente(id);

        assertThat(resultado).isFalse();
        verify(antecedenteRepository, times(1)).deleteById(id);
    }
}