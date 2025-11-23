package ubb.codeandcoffee.proyectoSemestral.servicios;

import jakarta.transaction.Transactional;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ubb.codeandcoffee.proyectoSemestral.modelo.Antecedente;
import ubb.codeandcoffee.proyectoSemestral.modelo.Criterio;
import ubb.codeandcoffee.proyectoSemestral.modelo.DatoSolicitado;
import ubb.codeandcoffee.proyectoSemestral.modelo.SujetoEstudio;

import java.io.FileOutputStream;
import java.util.*;

@Service
public class ExportarService {
    @Autowired
    SujetoEstudioService sujEstService;
    @Autowired
    AntecedenteService antService;
    @Autowired
    DatoSolicitadoService datoService;
    @Autowired
    CriterioService critService;


    @Transactional
    public void makeWorkbook(String filePath) {
        if (!filePath.endsWith(".xls")) filePath += ".xls";
        try {
            FileOutputStream fos = new FileOutputStream(filePath);
            Workbook workbook = new XSSFWorkbook();
            /*
            Realizar toda accion para rellenar el excel
             */

            Sheet sheet = workbook.createSheet("Datos Recopilados");
            Row r;
            Cell c;


            ArrayList<SujetoEstudio> sujetos = sujEstService.getSujetoEstudio();
            ArrayList<DatoSolicitado> preguntas = datoService.getDatoSolicitados();
            ArrayList<Criterio> criterios = critService.getCriterios();

            //mapa para recordar las columnas en que va cada cosa
            ArrayList<String> map = new ArrayList<>();

            int rowNum = 0;
            int columnNum = 0;
            /*
            Añadir los titulos a la primera columna
             */
            r = sheet.createRow(rowNum);
            r.createCell(0).setCellValue("ID_sujeto");
            map.add(columnNum, "ID_sujeto");
            columnNum++;
            r.createCell(1).setCellValue("Tipo_sujeto");
            map.add(columnNum, "Tipo_sujeto");
            columnNum++;

            for (DatoSolicitado pregunta : preguntas) {
                if (pregunta.getEstudio()) {
                    //solo van las preguntas que van a STATA (ya dicotomizados)
                    r.createCell(columnNum).setCellValue(pregunta.getNombreStata());
                    map.add(pregunta.getNombreStata());
                    columnNum++;

                    Set<Criterio> criteriosEnPregunta = pregunta.getCriterios();
                    for (Criterio criterio : criteriosEnPregunta) {
                        r.createCell(columnNum).setCellValue(criterio.getNombreStata());
                        map.add(criterio.getNombreStata());
                        columnNum++;
                    }
                }
            }


            /*
            Obtener los datos y añadirlos por sujeto
             */
            rowNum++;
            for (SujetoEstudio sujeto : sujetos) {
                r = sheet.createRow(rowNum);

                // Datos báse de sujeto (anonimizados)
                r.createCell(0).setCellValue(sujeto.getId_sujeto());
                r.createCell(1).setCellValue(sujeto.getTipo());
                ArrayList<Antecedente> respuestasSujeto = antService.getAntecedentesBySujeto(sujeto);

                for (Antecedente res : respuestasSujeto) {
                    columnNum = map.indexOf(res.getDatoSolicitado().getNombreStata());
                    r.createCell(columnNum).setCellValue(res.getValorNum());
                }

                // como se podría añadir los criterios?? pueden haber duplicados en el mapa


                rowNum++;
            }

            workbook.write(fos);
            workbook.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
