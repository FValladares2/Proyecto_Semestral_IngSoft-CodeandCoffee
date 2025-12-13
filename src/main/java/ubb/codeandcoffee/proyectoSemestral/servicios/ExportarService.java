package ubb.codeandcoffee.proyectoSemestral.servicios;

import jakarta.transaction.Transactional;
import org.apache.poi.ss.usermodel.*;
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

    //shoutouts stackoverflow: https://stackoverflow.com/questions/7153254/how-to-add-cell-comments-to-excel-sheet-using-poi
    private void addComment(Workbook workbook, Sheet sheet, Cell cell, int rowIdx, String commentText) {
        CreationHelper factory = workbook.getCreationHelper();

        ClientAnchor anchor = factory.createClientAnchor();
        //i found it useful to show the comment box at the bottom right corner
        anchor.setCol1(cell.getColumnIndex() + 1); //the box of the comment starts at this given column...
        anchor.setCol2(cell.getColumnIndex() + 3); //...and ends at that given column
        anchor.setRow1(rowIdx + 1); //one row below the cell...
        anchor.setRow2(rowIdx + 5); //...and 4 rows high

        Drawing drawing = sheet.createDrawingPatriarch();
        Comment comment = drawing.createCellComment(anchor);
        //set the comment text and author
        comment.setString(factory.createRichTextString(commentText));
        comment.setAuthor("Leyenda");

        cell.setCellComment(comment);
    }


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
            ArrayList<Criterio> criteriosObj = critService.getCriterios();

            //mapa para recordar las columnas en que va cada cosa
            ArrayList<String> map = new ArrayList<>();
            //guarda un k/v pair, del indice del criterio en el mapa y los indices de las columnas asociadas
            HashMap<Integer, ArrayList<Integer>> criterios = new HashMap<>();

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
                    columnNum = setupPregunta(workbook, sheet, r, map, criterios, columnNum, pregunta);
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

                for (Criterio criterio: criteriosObj){
                    //buscar la columna en la que está el criterio
                    int indexCriterio = map.indexOf(criterio.getNombreStata());
                    int valor = 0;
                    String[] argumentos = criterio.getExpresion().split(" ");
                    String resultado = lidiarConCriterio(argumentos, criterios.get(indexCriterio), r);

                    //guardar el dato en la columna del criterio
                    r.createCell(indexCriterio).setCellValue(resultado);
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
            ArrayList<Criterio> criteriosObj = critService.getCriterios();

            //mapa para recordar las columnas en que va cada cosa
            ArrayList<String> map = new ArrayList<>();
            //guarda un k/v pair, del indice del criterio en el mapa y los indices de las columnas asociadas
            HashMap<Integer, ArrayList<Integer>> criterios = new HashMap<>();

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
                columnNum = setupPregunta(workbook, sheet, r, map, criterios, columnNum, pregunta);
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

                for (Criterio criterio: criteriosObj){
                    //buscar la columna en la que está el criterio
                    int indexCriterio = map.indexOf(criterio.getNombreStata());
                    int valor = 0;
                    String[] argumentos = criterio.getExpresion().split(" ");
                    String resultado = lidiarConCriterio(argumentos, criterios.get(indexCriterio), r);

                    //guardar el dato en la columna del criterio
                    r.createCell(indexCriterio).setCellValue(resultado);
                }

                rowNum++;
            }

            workbook.write(fos);
            workbook.close();
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private int setupPregunta(Workbook w, Sheet s, Row r, ArrayList<String> map, HashMap<Integer, ArrayList<Integer>> criterios, int columnNum, DatoSolicitado pregunta) {
        Cell c = r.createCell(columnNum);
        c.setCellValue(pregunta.getNombreStata());
        addComment(w, s, c, 0, pregunta.getLeyenda());

        map.add(pregunta.getNombreStata());
        int columnaPregunta = columnNum;
        columnNum++;

        Set<Criterio> criteriosEnPregunta = pregunta.getCriterios();
        for (Criterio criterio : criteriosEnPregunta) {
            //cada criterio es único (sin repetir nombre stata), pero puede estar asociado a múltiples preguntas.
            //  segun entiendo, eso implica las columnas por las que obtiene valores sus
            int indexCriterio = map.indexOf(criterio.getNombreStata());
            columnNum = setupCriterios(w, s, r, map, criterios, columnNum, columnaPregunta, criterio, indexCriterio);
        }
        return columnNum;
    }

    private int setupCriterios(Workbook w, Sheet s, Row r, ArrayList<String> map, HashMap<Integer, ArrayList<Integer>> criterios, int columnNum, int columnaPregunta, Criterio criterio, int indexCriterio) {
        if (indexCriterio == -1) {
            //si no está el criterio en el mapa, se añade en ambos mapa y criterios
            Cell c = r.createCell(columnNum);
            c.setCellValue(criterio.getNombreStata());
            addComment(w, s, c, 0, criterio.getLeyenda());

            map.add(criterio.getNombreStata());
            int guardadoEn = map.indexOf(criterio.getNombreStata());
            ArrayList<Integer> arr = new ArrayList<>();
            arr.add(columnaPregunta);
            criterios.put(guardadoEn, arr);
            columnNum++;
        }else{
            //si está en el mapa, se añade al criterio ya existente
            ArrayList<Integer> arr = criterios.get(indexCriterio);
            arr.add(columnaPregunta);
            criterios.replace(indexCriterio, arr); //alternativamente put() ?
        }
        return columnNum;
    }

    private String lidiarConCriterio(String[] argumento, ArrayList<Integer> columnasDatos, Row r) {
        if (argumento.length <= 3) {
            //si es un argumento estilo VALOR1 ACCION VALOR2
            //realizar accion acuerdo a lo indicado
            //todo: obtener el valor de la celda dependiendo del tipo de criterio (promedio, media, algo específico, etc)

            switch (argumento[1]) {
                case "":

            }

        }else{
            //si es un argumento que tiene más variables estilo (arg1) AND/OR (arg2)
            String[] argumento1 = new String[3];
            String[] argumento2 = new String[argumento.length - 4];
            String separador = argumento[3];

            argumento1[0] = argumento[0];
            argumento1[1] = argumento[1];
            argumento1[2] = argumento[2];
            System.arraycopy(argumento, 4, argumento2, 0, argumento.length - 4);

            String resultado1 = lidiarConCriterio(argumento1, columnasDatos, r);
            String resultado2 = lidiarConCriterio(argumento2, columnasDatos, r);
            if (separador.equals("Y")){
                if (resultado1 == null || resultado2 == null) return null;

                if (resultado1.equals("1") && resultado2.equals("1")) return "1";
                else return "0";
            }else if (separador.equals("O")){
                if (resultado1 == null || resultado2 == null) return null;

                if (resultado1.equals("1") || resultado2.equals("1")) return "1";
                else return "0";
            }else throw new RuntimeException("Expresión inesperada, separador no es \"Y\" ni \"O\"");
        }
        return null;
    }
}
