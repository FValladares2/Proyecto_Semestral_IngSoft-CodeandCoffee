package ubb.codeandcoffee.proyectoSemestral.controladores;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ubb.codeandcoffee.proyectoSemestral.DTO.CriterioDTO;
import ubb.codeandcoffee.proyectoSemestral.modelo.Criterio;
import ubb.codeandcoffee.proyectoSemestral.modelo.DatoSolicitado;
import ubb.codeandcoffee.proyectoSemestral.modelo.Tipo_Calculo;
import ubb.codeandcoffee.proyectoSemestral.servicios.CriterioService;
import ubb.codeandcoffee.proyectoSemestral.servicios.DatoSolicitadoService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/* controlador para manejar la creación de criterios */
@Controller
@RequestMapping("/criterios")
public class CrearCriterioController {

    @Autowired
    private CriterioService criterioService;
    @Autowired
    private DatoSolicitadoService datoSolicitadoService;

    // muestra el formulario para crear un nuevo criterio
    @GetMapping("/nuevo")
    public String mostrarFormulario(Model model) {
        model.addAttribute("listaVariables", datoSolicitadoService.getDatoSolicitados());
        model.addAttribute("criterioDTO", new CriterioDTO());
        return "crear_criterio";
    }
    // procesa el formulario de creación de criterio
    @PostMapping("/guardar")
    public String procesarCriterio(@ModelAttribute("criterioDTO") CriterioDTO dto,
                                   BindingResult result,
                                   RedirectAttributes redirectAttrs) {

        // verificar errores de datos
        if (result.hasErrors()) {
            System.out.println("--- ERROR DE VALIDACIÓN ---");
            result.getAllErrors().forEach(e -> System.out.println(e.toString()));
            return "crear_criterio"; // vuelve al form si hay error
        }

        try {
            Criterio nuevo = new Criterio();
            nuevo.setNombre(dto.getNombre());
            nuevo.setNombreStata(dto.getNombreStata());
            nuevo.setLeyenda(dto.getLeyenda());
            nuevo.setTipoCalculo(Tipo_Calculo.valueOf(dto.getTipoCalculo()));

            Set<DatoSolicitado> variablesInvolucradas = new HashSet<>();
            StringBuilder expresionBuilder = new StringBuilder();

            // CASO PARTICULAR (Múltiples reglas)
            if ("PARTICULAR".equals(dto.getTipoCalculo())) {
                List<CriterioDTO.ReglaFila> filas = dto.getReglas();
                if (filas != null) {
                    for (int i = 0; i < filas.size(); i++) {
                        CriterioDTO.ReglaFila fila = filas.get(i);
                        // ignorar filas vacías si el usuario agregó de más
                        if (fila.getIdVariable() != null) {
                            DatoSolicitado var = datoSolicitadoService.getById(fila.getIdVariable()).orElse(null);
                            if (var != null) {
                                variablesInvolucradas.add(var);
                                // construye: "variable > valor AND..."
                                expresionBuilder.append(var.getNombreStata())
                                        .append(" ")
                                        .append(fila.getOperador())
                                        .append(" ")
                                        .append(fila.getValor());

                                if (i < filas.size() - 1 && fila.getConector() != null) {
                                    expresionBuilder.append(" ").append(fila.getConector()).append(" ");
                                }
                            }
                        }
                    }
                }
            }
            // CASO ESTADÍSTICO (Promedio/Mediana)
            else {
                DatoSolicitado var = datoSolicitadoService.getById(dto.getIdVariableSimple())
                        .orElseThrow(() -> new RuntimeException("Debe seleccionar una variable"));
                variablesInvolucradas.add(var);

                String func = "PROMEDIO".equals(dto.getTipoCalculo()) ? "AVG" : "MEDIAN";
                // construye: "variable >= AVG"
                expresionBuilder.append(var.getNombreStata())
                        .append(" ")
                        .append(dto.getOperadorSimple()) // el operador que eligió el usuario
                        .append(" ")
                        .append(func);
            }
            
            // asigna los datos al nuevo criterio
            nuevo.setDatosSolicitados(variablesInvolucradas);
            nuevo.setExpresion(expresionBuilder.toString());

            criterioService.guardarCriterio(nuevo);

            redirectAttrs.addFlashAttribute("mensaje", "¡Criterio guardado exitosamente!");
            return "redirect:/criterios/nuevo";

        } catch (Exception e) {
            //manejamos cualquier error inesperado
            e.printStackTrace();
            redirectAttrs.addFlashAttribute("error", "Error interno: " + e.getMessage());
            return "redirect:/criterios/nuevo";
        }
    }
}
