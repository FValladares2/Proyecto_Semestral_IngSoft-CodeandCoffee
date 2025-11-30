package ubb.codeandcoffee.proyectoSemestral;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ubb.codeandcoffee.proyectoSemestral.servicios.ExportarService;

@SpringBootTest
public class ExcelServiceTest {

    @Autowired
    ExportarService exportarService;

    @Test
    void getExcelA(){
        exportarService.makeFile("bdd/test.xlsx");
        exportarService.makeWorkbookDicotom("bdd/test.xlsx");
    }

    @Test
    void getExcelB(){
        exportarService.makeFile("bdd/test2.xlsx");
        exportarService.makeWorkbookFull("bdd/test2.xlsx");
    }

}
