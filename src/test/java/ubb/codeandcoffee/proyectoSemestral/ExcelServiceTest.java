package ubb.codeandcoffee.proyectoSemestral;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ubb.codeandcoffee.proyectoSemestral.repositorios.AntecedenteRepository;
import ubb.codeandcoffee.proyectoSemestral.servicios.ExportarService;

@SpringBootTest
public class ExcelServiceTest {

    @Autowired
    ExportarService exportarService;

    @Test
    void getExcel(){
        exportarService.makeWorkbook("bdd/test.xls");
    }

}
