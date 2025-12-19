package ubb.codeandcoffee.proyectoSemestral.controladores;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ubb.codeandcoffee.proyectoSemestral.servicios.ExportarService;

import java.io.File;

// Controlador para manejar la exportación de reportes

@Controller
@RequestMapping("/exportar")
public class ExportarController {

    @Autowired
    private ExportarService exportarService;

    @GetMapping
    public String verMenuExportar() {
        return "exportar";
    }


    /// Endpoint POST para generar reportes
    @PostMapping("/generar")
    public String generarReporte(@RequestParam("tipo") String tipo, RedirectAttributes redirectAttributes) {
        String fileName = "";
        try {
            if ("dicotomico".equals(tipo)) {
                fileName = "reporte_dicotomico.xlsx";
                exportarService.makeWorkbookDicotom(fileName);
            } else if ("completo".equals(tipo)) {
                fileName = "reporte_completo.xlsx";
                exportarService.makeWorkbookFull(fileName);
            } else {
                throw new IllegalArgumentException("Tipo de reporte no reconocido");
            }

            redirectAttributes.addFlashAttribute("exito", "¡Reporte " + tipo + " generado correctamente!");
            redirectAttributes.addFlashAttribute("archivoGenerado", fileName);

        } catch (Exception e) {
            // manejamos todos los errores posibles
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error al generar el reporte: " + e.getMessage());
        }

        return "redirect:/exportar";
    }


    //se encarga de descargar el archivo generado
    @GetMapping("/descargar/{fileName}")
    public ResponseEntity<Resource> descargarReporte(@PathVariable String fileName) {
        if (!fileName.equals("reporte_dicotomico.xlsx") && !fileName.equals("reporte_completo.xlsx")) {
            return ResponseEntity.badRequest().build();
        }

        File file = new File(fileName);
        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = new FileSystemResource(file);


        //configuramos la respuesta HTTP para descargar el archivo
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .body(resource);
    }
}
