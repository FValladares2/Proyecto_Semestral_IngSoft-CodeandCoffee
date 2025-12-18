package ubb.codeandcoffee.proyectoSemestral.servicios;

import jakarta.transaction.Transactional;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.ss.util.CellUtil;
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

            //Separar las preguntas por su grupo (id_seccion)
            SortedMap<Integer, List<DatoSolicitado>> datosAgrupados = new TreeMap<>();
            for (DatoSolicitado pregunta : preguntas){
                datosAgrupados.computeIfAbsent(pregunta.getSeccion().getNumero(),
                        k -> new ArrayList<>()).add(pregunta);
            }

            for (var entry : datosAgrupados.entrySet()) {
                int seccion = entry.getKey();
                for (DatoSolicitado pregunta : entry.getValue()) {
                    if (pregunta.getEstudio()) {
                        //solo van las preguntas que van a STATA (ya dicotomizados)
                        columnNum = setupPregunta(workbook, sheet, r, map, columnNum, pregunta);
                    }else{
                        //si no va la pregunta, solo añadir el criterio
                        Set<Criterio> criteriosEnPregunta = critService.getByDatoSolicitado(pregunta);
                        for (Criterio criterio : criteriosEnPregunta) {
                            int indexCriterio = map.indexOf(criterio.getNombreStata());
                            columnNum = setupCriterios(workbook, sheet, r, map, columnNum, criterio, indexCriterio);
                        }
                    }
                }
            }

            int totalColumns = columnNum;
            //obtener promedios/medianas/modas con datos validos
            HashMap<DatoSolicitado, Float> promedios = new HashMap<>();
            HashMap<DatoSolicitado, Float> medianas = new HashMap<>();
            HashMap<DatoSolicitado, Float> modas = new HashMap<>();
            getPMM(promedios, medianas, modas, preguntas);

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
                HashMap<DatoSolicitado, Antecedente> respuestasMapa = new HashMap<>();

                for (Antecedente res : respuestasSujeto) {
                    respuestasMapa.put(res.getDatoSolicitado(), res);
                    columnNum = map.indexOf(res.getDatoSolicitado().getNombreStata());
                    System.out.println("antecedente: "+res.getDatoSolicitado().getNombreStata());
                    if (res.getValorNum() != null && columnNum != -1) r.createCell(columnNum).setCellValue(res.getValorNum());
                }

                for (Criterio criterio: criteriosObj){
                    //buscar la columna en la que está el criterio
                    int indexCriterio = map.indexOf(criterio.getNombreStata());
                    if (indexCriterio == -1){
                        System.out.println("Error? criterio no está en la tabla - puede ser por no tener preguntas validas en excel");
                        continue;
                    }
                    Set<DatoSolicitado> datos = criterio.getDatosSolicitados();
                    String[] argumentos = criterio.getExpresion().split(" ");
                    String resultado = lidiarConCriterio(argumentos, respuestasMapa, datos, r, promedios, medianas, modas);
                    System.out.println("resultado: " + resultado);
                    //guardar el dato en la columna del criterio
                    if (resultado != null) r.createCell(indexCriterio).setCellValue(resultado);
                }


                rowNum++;
            }
            rowNum++;
            calcSumasAlFinal(rowNum, totalColumns, sheet);

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

            //Separar las preguntas por su grupo (id_seccion)
            SortedMap<Integer, List<DatoSolicitado>> datosAgrupados = new TreeMap<>();
            for (DatoSolicitado pregunta : preguntas){
                datosAgrupados.computeIfAbsent(pregunta.getSeccion().getNumero(),
                        k -> new ArrayList<>()).add(pregunta);
            }
            for (var entry : datosAgrupados.entrySet()) {
                int seccion = entry.getKey();
                for (DatoSolicitado pregunta : entry.getValue()) {
                    //añade toda pregunta, dicotomizada o no
                    columnNum = setupPregunta(workbook, sheet, r, map, columnNum, pregunta);
                }
            }

            int totalColumns = columnNum;
            //obtener promedios/medianas/modas con datos validos
            HashMap<DatoSolicitado, Float> promedios = new HashMap<>();
            HashMap<DatoSolicitado, Float> medianas = new HashMap<>();
            HashMap<DatoSolicitado, Float> modas = new HashMap<>();
            getPMM(promedios, medianas, modas, preguntas);

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
                HashMap<DatoSolicitado, Antecedente> respuestasMapa = new HashMap<>();

                for (Antecedente res : respuestasSujeto) {
                    respuestasMapa.put(res.getDatoSolicitado(), res);
                    columnNum = map.indexOf(res.getDatoSolicitado().getNombreStata());
                    if (res.getValorString() != null) r.createCell(columnNum).setCellValue(res.getValorString());
                    else if (res.getValorNum() != null) r.createCell(columnNum).setCellValue(res.getValorNum());
                }

                for (Criterio criterio: criteriosObj){
                    //buscar la columna en la que está el criterio
                    int indexCriterio = map.indexOf(criterio.getNombreStata());
                    if (indexCriterio == -1){
                        System.out.println("Error? criterio no está en la tabla - puede ser por no tener preguntas validas en excel");
                        continue;
                    }
                    Set<DatoSolicitado> datos = criterio.getDatosSolicitados();
                    String[] argumentos = criterio.getExpresion().split(" ");
                    String resultado = lidiarConCriterio(argumentos, respuestasMapa, datos, r, promedios, medianas, modas);
                    System.out.println("resultado: " + resultado);
                    //guardar el dato en la columna del criterio
                    r.createCell(indexCriterio).setCellValue(resultado);
                }

                rowNum++;
            }

            rowNum++;
            calcSumasAlFinal(rowNum, totalColumns, sheet);

            workbook.write(fos);
            workbook.close();
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private int setupPregunta(Workbook w, Sheet s, Row r, ArrayList<String> map, int columnNum, DatoSolicitado pregunta) {
        Cell c = r.createCell(columnNum);
        c.setCellValue(pregunta.getNombreStata());
        addComment(w, s, c, 0, pregunta.getLeyenda());

        map.add(pregunta.getNombreStata());
        columnNum++;

        System.out.println("Columnnum pre-criterios: " + columnNum);
        Set<Criterio> criteriosEnPregunta = critService.getByDatoSolicitado(pregunta);
        for (Criterio criterio : criteriosEnPregunta) {
            //cada criterio es único (sin repetir nombre stata), pero puede estar asociado a múltiples preguntas.
            //  segun entiendo, eso implica las columnas por las que obtiene valores sus
            int indexCriterio = map.indexOf(criterio.getNombreStata());
            System.out.println("Is here 2");
            columnNum = setupCriterios(w, s, r, map, columnNum, criterio, indexCriterio);
        }
        return columnNum;
    }

    private int setupCriterios(Workbook w, Sheet s, Row r, ArrayList<String> map, int columnNum, Criterio criterio, int indexCriterio) {
        System.out.println("criterios ~~~");
        if (indexCriterio == -1) {
            System.out.println("Nuevo Criterio: "+ criterio.getNombreStata());
            //si no está el criterio en el mapa, se añade su columna en mapa
            Cell c = r.createCell(columnNum);
            c.setCellValue(criterio.getNombreStata());
            addComment(w, s, c, 0, criterio.getLeyenda());

            map.add(criterio.getNombreStata());
            columnNum++;
        }//si está en el mapa, continúa
        return columnNum;
    }

    private String lidiarConCriterio(String[] argumento, HashMap<DatoSolicitado, Antecedente> columnasDatos,
                                       Set<DatoSolicitado> datos, Row r, HashMap<DatoSolicitado, Float> avg,
                                       HashMap<DatoSolicitado, Float> med, HashMap<DatoSolicitado, Float> mod) {
        if (argumento.length <= 3) {
            //si es un argumento estilo VALOR1 ACCION VALOR2
            //realizar accion acuerdo a lo indicado

            System.out.print("argumentos: ");
            for (String arg : argumento) System.out.print("[] = ["+ arg + "], ");
            System.out.println();

            DatoSolicitado datoValor1 = null;
            for (DatoSolicitado d : datos) if (d.getNombreStata().equals(argumento[0])) datoValor1 = d;
            if (datoValor1 == null) {
                System.out.println("Argumento[0] " + argumento[0] + " no esta relacionado con un criterio procesado");
                return null;
            }
            System.out.println("Found: "+argumento[0]+" = "+datoValor1.getNombreStata());
            Antecedente valor1 = columnasDatos.get(datoValor1);
            if (valor1 == null) return null; //si no hay respuesta del usuario, retorna null

            float valor2;
            if (!argumento[2].equals("AVG") &&
                    !argumento[2].equals("MOD") &&
                    !argumento[2].equals("MED"))
                valor2 = Float.parseFloat(argumento[2]);
            else if (avg.get(datoValor1) != null &&
                    //mod.get(datoValor1) != null &&
                    med.get(datoValor1) != null){
                System.out.println("Realizando Accion "+argumento[2]);
                switch (argumento[2]) {
                    case "AVG" -> valor2 = avg.get(datoValor1);
                    case "MOD" -> valor2 = mod.get(datoValor1);
                    case "MED" -> valor2 = med.get(datoValor1);
                    default -> {
                        System.out.println("Error?; unreachable statement?");
                        return null;
                    }
                }
                System.out.println("valor2: "+valor2 + " valor1: " + valor1);
            }else return null;

            switch (argumento[1]) {
                case "<=" -> {
                    if (valor1.getValorNum() <= valor2) return "1";
                    else return "0";
                }
                case ">=" -> {
                    if (valor1.getValorNum() >= valor2) return "1";
                    else return "0";
                }
                case ">" -> {
                    if (valor1.getValorNum() > valor2) return "1";
                    else return "0";
                }
                case "<" -> {
                    if (valor1.getValorNum() < valor2) return "1";
                    else return "0";
                }
                case "==" -> {
                    if (valor1.getValorNum() == valor2) return "1";
                    else if (valor1.getValorString().equals(argumento[2])) return "1";
                    else return "0";
                }
                case "!=" -> {
                    if (valor1.getValorNum() != valor2) return "1";
                    else if (!valor1.getValorString().equals(argumento[2])) return "1";
                    else return "0";
                }
                default -> {
                    //print en consola spring para debug
                    System.out.println("Argumento[1] (acción) ("+argumento[1]+") no reconocido?");
                }
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

            String resultado1 = lidiarConCriterio(argumento1, columnasDatos, datos, r, avg, med, mod);
            String resultado2 = lidiarConCriterio(argumento2, columnasDatos, datos, r, avg, med, mod);
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

    private void getPMM(HashMap<DatoSolicitado, Float> avg, HashMap<DatoSolicitado, Float> med,
                        HashMap<DatoSolicitado, Float> mod, ArrayList<DatoSolicitado> preguntas){
        for (DatoSolicitado d : preguntas) {
            if (d.getTipoRespuesta().equals(TipoRespuesta.NUMERO)) {
                List<Antecedente> respuestas = antService.getAllByDatoSolicitado(d);
                ArrayList<Float> valores = new ArrayList<>();
                float suma = 0;
                int cantidad = 0;
                for (Antecedente a : respuestas) {
                    if (a.getValorNum() != null) {
                        float v = a.getValorNum();
                        suma += v;
                        valores.add(v);
                        cantidad++;
                    }
                }
                if (cantidad > 0) {
                    avg.put(d, (suma / cantidad));
                    float media;
                    int len = valores.size();
                    if (len % 2 == 0) media = ((valores.get(len / 2) + valores.get(len / 2 - 1)) / 2);
                    else media = valores.get(len / 2);
                    med.put(d, media);
                    //todo: calcular moda
                }
            }
        }
    }


    private void calcSumasAlFinal(int rownum, int columns, Sheet sheet){
        System.out.println("\nCalculando sumas de datos dicotomizados");
        System.out.println("columns: " + columns);
        HashMap<String, Integer> datosAEsquivar = new HashMap<>();
        datosAEsquivar.put("Nombre_sujeto", 1);
        datosAEsquivar.put("Direccion_sujeto", 1);
        datosAEsquivar.put("Email_sujeto", 1);
        datosAEsquivar.put("Telefono_sujeto", 1);
        datosAEsquivar.put("Nacionalidad_sujeto", 1);
        datosAEsquivar.put("Ocupacion_sujeto", 1);

        Row r;
        Cell c;


        r = CellUtil.getRow(rownum, sheet);
        CellUtil.getCell(r, 0).setCellValue("Cantidad de 0s =");
        r = CellUtil.getRow(rownum+1, sheet);
        CellUtil.getCell(r, 0).setCellValue("Cantidad de 1s =");

        for (int i = 2; i < columns; i++) {
            System.out.println("i="+i);
            r = CellUtil.getRow(0, sheet);
            c = CellUtil.getCell(r, i);
            String nombreColumna = c.getStringCellValue();

            if (datosAEsquivar.get(nombreColumna) != null) continue;

            String colLetter = CellReference.convertNumToColString(i);
            String range = colLetter + "1:" + colLetter + (rownum-1); // e.g., A2:A100

            r = CellUtil.getRow(rownum, sheet);
            CellUtil.getCell(r, i).setCellFormula("COUNTIF(" + range + ",1)");

            r = CellUtil.getRow(rownum+1, sheet);
            CellUtil.getCell(r, i).setCellFormula("COUNTIF(" + range + ",0)");
        }
    }
}
