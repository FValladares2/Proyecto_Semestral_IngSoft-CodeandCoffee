package ubb.codeandcoffee.proyectoSemestral.controladores;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ubb.codeandcoffee.proyectoSemestral.modelo.SujetoEstudio;
import ubb.codeandcoffee.proyectoSemestral.modelo.codigo_sujeto;
import ubb.codeandcoffee.proyectoSemestral.repositorios.SujetoEstudioRepository;
import ubb.codeandcoffee.proyectoSemestral.servicios.DatoSolicitadoService;
import ubb.codeandcoffee.proyectoSemestral.servicios.SeccionService;
import ubb.codeandcoffee.proyectoSemestral.servicios.SujetoEstudioService;

import java.util.ArrayList;

@Controller
@RequestMapping("/edicion")
public class EdicionController {

    @Autowired
    private SeccionService seccionService;

    @Autowired
    private SujetoEstudioRepository repo;

    @Autowired
    private SujetoEstudioService sujetoEstudioService;

    @Autowired
    private DatoSolicitadoService datoSolicitadoService;

    //entremedio voy a poner lo relativo a la actualizacion de los datos de un sujeto
    @GetMapping("/sujetos")//aqui se accede a la lista de sujetos, para elegir a cual queremos actualizar
    public String mostrarSujetos( Model modelo) {

        ArrayList<SujetoEstudio> sujetosEstudio = sujetoEstudioService.getSujetoEstudio();


        modelo.addAttribute("sujetos", sujetosEstudio);

        return "form/lista_sujetos";
    }

    @GetMapping("/sujetos/editar/{tipo}/{id}")//eligiendo el sujeto a actualizar, tenemos que elegir que actualizaremos
    public String mostrarOpcionesActualizar(@PathVariable("id") String id, @PathVariable("tipo") String tipo, Model modelo) {

        codigo_sujeto codigo = new codigo_sujeto(id, tipo);
        SujetoEstudio sujeto = sujetoEstudioService.getById(codigo)
                .orElseThrow(() -> new IllegalArgumentException("Sujeto no encontrado"));

        modelo.addAttribute("sujeto", sujeto);

        return "form/menu_actualizar_sujeto";
    }

    @GetMapping("sujetos/editar/ingreso/{tipo}/{id}")//si elegimos actualizar ingreso (sus datos personales)
    public String mostrarEdicionDatos(@PathVariable("id") String id,@PathVariable("tipo") String tipo, Model model) {

        codigo_sujeto codigo = new codigo_sujeto(id, tipo);
        SujetoEstudio sujeto = repo.findById(codigo)
                .orElseThrow(() -> new IllegalArgumentException("Sujeto no encontrado"));


        model.addAttribute("sujeto", sujeto);
        model.addAttribute("editar", true);

        return "form/ingreso_sujeto_edicion";
    }

    //envio de actualizacion
    //necesita el springsecurity funcionando para funcionar bien
    @PostMapping("/sujetos/editar/ingreso/{tipo}/{id}")
    public String procesarActualizacionIngreso(
            @PathVariable("id") String id,
            @RequestParam String nombre,
            @PathVariable("tipo") String tipo,
            @RequestParam(required = false) String direccion,
            @RequestParam(required = false) String ocupacion,
            @RequestParam(required = false) String telefono,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String nacionalidad,
            HttpSession session,
            RedirectAttributes redirectAttributes,
            HttpServletRequest request) {

        codigo_sujeto codigo = new codigo_sujeto(id, tipo);
        SujetoEstudio sujeto = sujetoEstudioService.findByCompuesta(id, tipo);

        if (request.isUserInRole("ROLE_ADMINISTRADOR") && sujeto == null) {
            redirectAttributes.addFlashAttribute("error",
                    "El sujeto con ID " + id + " no existe.");
            return "redirect:/menu/admin";
        }else{
            if(request.isUserInRole("ROLE_RECOLECTOR_DE_DATOS") && sujeto == null){
                redirectAttributes.addFlashAttribute("error",
                        "El sujeto con ID " + id + " no existe.");
                return "redirect:/menu/recolector";
            }

        }


        try {
            sujeto.setNombre(nombre);
            sujeto.setDireccion(direccion);
            sujeto.setOcupacion(ocupacion);
            sujeto.setTelefono(telefono);
            sujeto.setEmail(email);
            sujeto.setNacionalidad(nacionalidad);

            sujetoEstudioService.updateById(sujeto, codigo);

            redirectAttributes.addFlashAttribute("exito",
                    "Sujeto actualizado correctamente.");

            if (request.isUserInRole("ROLE_ADMINISTRADOR")) {
                return "redirect:/menu/admin";
            }else{
                return "redirect:/menu/recolector";
            }


        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute("error",
                    "Error al actualizar el sujeto. Revise los datos.");
            return "redirect:/sujetos/editar/ingreso/{tipo}/{id}";
        }
    }
}
