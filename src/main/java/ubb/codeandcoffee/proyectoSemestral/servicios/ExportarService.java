package ubb.codeandcoffee.proyectoSemestral.servicios;

import jakarta.transaction.Transactional;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ubb.codeandcoffee.proyectoSemestral.modelo.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
    @Autowired
    UsuarioSujetoService usujService;

    public boolean makeFile(String fileName) {
        try {
            File file = new File(fileName);
            return file.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public void makeWorkbookDicotom(String filePath) {
        if (filePath.endsWith(".xls")) filePath = filePath.replace(".xls", ".xlsx");
        if (!filePath.endsWith(".xlsx")) filePath += ".xlsx";
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
                    if (res.getValorNum() != null) r.createCell(columnNum).setCellValue(res.getValorNum());
                }



                rowNum++;
            }

            workbook.write(fos);
            workbook.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public void makeWorkbookFull(String filePath) {
        if (filePath.endsWith(".xls")) filePath = filePath.replace(".xls", ".xlsx");
        if (!filePath.endsWith(".xlsx")) filePath += ".xlsx";
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

            /*Datos del paciente:
            *   ID - Código - Nombre - dirección - email - telefono - nacionalidad - ocupacion
            * */

            r.createCell(0).setCellValue("ID_sujeto");
            map.add(columnNum, "ID_sujeto");
            columnNum++;
            r.createCell(1).setCellValue("Tipo_sujeto");
            map.add(columnNum, "Tipo_sujeto");
            columnNum++;
            r.createCell(2).setCellValue("Nombre_sujeto");
            map.add(columnNum, "Nombre_sujeto");
            columnNum++;
            r.createCell(3).setCellValue("Direccion_sujeto");
            map.add(columnNum, "Direccion_sujeto");
            columnNum++;
            r.createCell(4).setCellValue("Email_sujeto");
            map.add(columnNum, "Email_sujeto");
            columnNum++;
            r.createCell(5).setCellValue("Telefono_sujeto");
            map.add(columnNum, "Telefono_sujeto");
            columnNum++;
            r.createCell(6).setCellValue("Nacionalidad_sujeto");
            map.add(columnNum, "Nacionalidad_sujeto");
            columnNum++;
            r.createCell(7).setCellValue("Ocupacion_sujeto");
            map.add(columnNum, "Ocupacion_sujeto");
            columnNum++;

            /*TODO: Datos del usuario que lo añade:
             *  correo - nombre - estado - rol ?
             *
            r.createCell(8).setCellValue("correo_usuario");
            map.add(columnNum, "correo_usuario");
            columnNum++;
            r.createCell(9).setCellValue("nombre_usuario");
            map.add(columnNum, "nombre_usuario");
            columnNum++;
            r.createCell(10).setCellValue("estado_usuario");
            map.add(columnNum, "estado_usuario");
            columnNum++;
            r.createCell(11).setCellValue("rol_usuario");
            map.add(columnNum, "rol_usuario");
            columnNum++;
             */

            for (DatoSolicitado pregunta : preguntas) {
                //añade toda pregunta, dicotomizada o no
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


            /*
            Obtener los datos y añadirlos por sujeto
             */
            rowNum++;
            for (SujetoEstudio sujeto : sujetos) {
                r = sheet.createRow(rowNum);

                // Datos báse de sujeto
                r.createCell(0).setCellValue(sujeto.getId_sujeto());
                r.createCell(1).setCellValue(sujeto.getTipo());
                r.createCell(2).setCellValue(sujeto.getNombre());
                r.createCell(3).setCellValue(sujeto.getDireccion());
                r.createCell(4).setCellValue(sujeto.getEmail());
                r.createCell(5).setCellValue(sujeto.getTelefono());
                r.createCell(6).setCellValue(sujeto.getNacionalidad());
                r.createCell(7).setCellValue(sujeto.getOcupacion());

                /*TODO: Datos del usuario que lo añade:
                 *  correo - nombre - estado - rol ?
                Usuario u = usujService.getCreadorSujeto(sujeto);
                r.createCell(8).setCellValue(u.getCorreo());
                r.createCell(9).setCellValue(u.getNombre());
                r.createCell(10).setCellValue(u.getEstado().toString());
                r.createCell(11).setCellValue(u.getRol().toString());
                 */

                ArrayList<Antecedente> respuestasSujeto = antService.getAntecedentesBySujeto(sujeto);

                for (Antecedente res : respuestasSujeto) {
                    columnNum = map.indexOf(res.getDatoSolicitado().getNombreStata());
                    if (res.getValorString() != null) r.createCell(columnNum).setCellValue(res.getValorString());
                    else if (res.getValorNum() != null) r.createCell(columnNum).setCellValue(res.getValorNum());
                }

                rowNum++;
            }

            workbook.write(fos);
            workbook.close();
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
