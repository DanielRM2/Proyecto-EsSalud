package com.ed.ecommerce.mvcDemo.Controller;

import com.ed.ecommerce.mvcDemo.Model.Cita;
import com.ed.ecommerce.mvcDemo.Model.Usuario;
import com.ed.ecommerce.mvcDemo.Services.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/essalud")
public class CitaController {

    @Autowired
    private ServiceCita serviceCita;
    @Autowired
    private ServiceMedico serviceMedico;
    @Autowired
    private ServiceEspecialidad serviceEspecialidad;
    @Autowired
    private ServiceCentroMedico serviceCentroMedico;

    // Mostrar el index
    @GetMapping("/index")
    public String mostrarIndex(HttpSession session, Model model,
                               @RequestParam(required = false) String mensaje,
                               @RequestParam(required = false) String error) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            return "redirect:/essalud/login";
        }

        model.addAttribute("usuario", usuario);

        // Manejar mensajes de redirección
        if (mensaje != null) {
            model.addAttribute("mensaje", mensaje);
        }
        if (error != null) {
            model.addAttribute("error", error);
        }

        try {
            List<Cita> citas = serviceCita.obtenerCitasPorUsuario(usuario.getIdUsuario());

            if (citas != null && !citas.isEmpty()) {
                // Ordenar citas por fecha (más recientes primero)
                citas.sort((c1, c2) -> c2.getFechaCita().compareTo(c1.getFechaCita()));
                model.addAttribute("citas", citas);
            } else {
                model.addAttribute("mensaje", "No tienes citas agendadas actualmente.");
            }
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar citas: " + e.getMessage());
        }

        return "index";
    }

    // Mostrar formulario para agendar cita
    @GetMapping("/citas/agendar")
    public String mostrarFormularioCita(HttpSession session,
                                        Model model,
                                        @RequestParam(required = false) Integer idEspecialidad,
                                        @RequestParam(required = false) Integer idCentroMedico) {

        Usuario usuario = validarSesion(session);
        if (usuario == null) return "redirect:/essalud/login";

        cargarDatosFormulario(model, idEspecialidad, idCentroMedico);
        return "agendarCita";
    }

    // Procesar agendamiento
    @PostMapping("/citas/agendar")
    public String procesarAgendarCita(@ModelAttribute("nuevaCita") Cita cita,
                                      @RequestParam Integer idMedico,
                                      @RequestParam Integer idCentroMedico,
                                      HttpSession session,
                                      RedirectAttributes redirectAttributes) {

        Usuario usuario = validarSesion(session);
        if (usuario == null) return "redirect:/essalud/login";

        try {
            prepararCita(cita, usuario, idMedico, idCentroMedico);
            boolean exito = serviceCita.agendarCita(cita);

            manejarResultado(redirectAttributes, exito,
                    "Cita agendada correctamente",
                    "No se pudo agendar la cita");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
            return "redirect:/essalud/citas/agendar";
        }

        return "redirect:/essalud/index";
    }


    // Cancelar cita
    @PostMapping("/citas/cancelar/{id}")
    public String cancelarCita(@PathVariable int id,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {

        Usuario usuario = validarSesion(session);
        if (usuario == null) return "redirect:/essalud/login";

        try {
            boolean exito = serviceCita.cancelarCita(id);
            manejarResultado(redirectAttributes, exito,
                    "Cita cancelada correctamente",
                    "No se pudo cancelar la cita");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        }

        return "redirect:/essalud/index";
    }
    @PostMapping("/citas/reprogramar/{id}")
    public String reprogramarCita(@PathVariable int id,
                                  @RequestParam("nuevaFecha") String nuevaFechaStr,
                                  RedirectAttributes redirectAttributes) {
        try {
            LocalDateTime nuevaFecha = LocalDateTime.parse(nuevaFechaStr);
            boolean reprogramado = serviceCita.reprogramarCita(id, nuevaFecha);

            redirectAttributes.addFlashAttribute(
                    reprogramado ? "mensaje" : "error",
                    reprogramado ? "Cita reprogramada correctamente." : "No se pudo reprogramar la cita."
            );
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al reprogramar cita: " + e.getMessage());
        }

        return "redirect:/essalud/index";
    }

    // Métodos auxiliares privados
    private Usuario validarSesion(HttpSession session) {
        return (Usuario) session.getAttribute("usuario");
    }

    private void cargarDatosFormulario(Model model, Integer idEspecialidad, Integer idCentroMedico) {
        model.addAttribute("especialidades", serviceEspecialidad.listarTodas());
        model.addAttribute("centrosMedicos", serviceCentroMedico.listarTodos());
        model.addAttribute("nuevaCita", new Cita());

        if (idEspecialidad != null && idCentroMedico != null) {
            model.addAttribute("medicos", serviceMedico.listarPorEspecialidadYCentro(idEspecialidad, idCentroMedico));
            model.addAttribute("idEspecialidadSeleccionada", idEspecialidad);
            model.addAttribute("idCentroMedicoSeleccionado", idCentroMedico);
        }
    }

    private void prepararCita(Cita cita, Usuario usuario, Integer idMedico, Integer idCentroMedico) {
        cita.setIdUsuario(usuario.getIdUsuario());
        cita.setIdMedico(idMedico);
        cita.setIdCentroMedico(idCentroMedico);
        cita.setEstado("Pendiente");
    }

    private void manejarResultado(RedirectAttributes redirectAttributes,
                                  boolean exito,
                                  String mensajeExito,
                                  String mensajeError) {
        redirectAttributes.addFlashAttribute(
                exito ? "mensaje" : "error",
                exito ? mensajeExito : mensajeError
        );
    }

}
