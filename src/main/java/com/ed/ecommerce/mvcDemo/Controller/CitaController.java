package com.ed.ecommerce.mvcDemo.Controller;

import com.ed.ecommerce.mvcDemo.Model.*;
import com.ed.ecommerce.mvcDemo.Services.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

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
    @Autowired
    private ServiceHorarioDisponible serviceHorarioDisponible;

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
    public String mostrarFormularioCita(@RequestParam(required = false) Integer idCentroMedico,
                                        @RequestParam(required = false) Integer idEspecialidad,
                                        HttpSession session,
                                        Model model) {

        Usuario usuario = validarSesion(session);
        if (usuario == null) return "redirect:/essalud/login";

        List<CentroMedico> centros = serviceCentroMedico.listarTodos();
        List<Especialidad> especialidades = serviceEspecialidad.listarTodas();

        model.addAttribute("centros", centros);
        model.addAttribute("especialidades", especialidades);

        return "agendarCita";
    }

    // Procesar formulario y mostrar horarios disponibles
    @PostMapping("/citas/agendar/listarHorarios")
    public String listarHorarios(
                                 @RequestParam("idCentroMedico") int idCentroMedico,
                                 @RequestParam("idEspecialidad") int idEspecialidad,
                                 Model model) {

        // Obtener listas para selects y horarios
        List<CentroMedico> centros = serviceCentroMedico.listarTodos();
        List<Especialidad> especialidades = serviceEspecialidad.listarTodas();
        List<HorarioDisponible> horarios = serviceHorarioDisponible.listarPorCentroMedicoYEspecialidad(idCentroMedico, idEspecialidad);

        model.addAttribute("centros", centros);
        model.addAttribute("especialidades", especialidades);
        model.addAttribute("horarios", horarios);

        // Mantener selección en los selects
        model.addAttribute("selectedCentro", idCentroMedico);
        model.addAttribute("selectedEspecialidad", idEspecialidad);

        return "agendarCita";
    }



    // Procesar agendamiento
    @PostMapping("/citas/agendar")
    public String procesarAgendarCita(HttpSession session,
                                      @RequestParam("idHorario") Integer idHorario,
                                      RedirectAttributes redirectAttributes) {
        Usuario usuario = validarSesion(session);
        if (usuario == null) {
            return "redirect:/essalud/login";
        }

        try {
            HorarioDisponible horario = serviceHorarioDisponible.obtenerPorId(idHorario);
            if (horario == null) {
                redirectAttributes.addFlashAttribute("error", "Horario no encontrado.");
                return "redirect:/essalud/citas/agendar";
            }

            // Obtener el médico
            Medico medico = serviceMedico.obtenerPorId(horario.getIdMedico());
            if (medico == null) {
                redirectAttributes.addFlashAttribute("error", "Médico no encontrado.");
                return "redirect:/essalud/citas/agendar";
            }

            // Obtener el centro médico desde el médico
            Integer idCentroMedico = medico.getIdCentroMedico();

            Cita nuevaCita = new Cita();
            nuevaCita.setIdUsuario(usuario.getIdUsuario());
            nuevaCita.setIdMedico(medico.getIdMedico());
            nuevaCita.setIdCentroMedico(idCentroMedico);
            nuevaCita.setIdHorario(horario.getIdHorario());
            nuevaCita.setEstado("Pendiente");
            nuevaCita.setFechaCita(LocalDateTime.of(horario.getFecha(), horario.getHora()));

            boolean exito = serviceCita.agendarCita(nuevaCita);

            if (exito) {
                serviceHorarioDisponible.actualizarDisponibilidad(idHorario, false);
                redirectAttributes.addFlashAttribute("mensaje", "Cita agendada correctamente.");
            } else {
                redirectAttributes.addFlashAttribute("error", "No se pudo agendar la cita.");
            }

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        }

        return "redirect:/essalud/index";
    }





    @PostMapping("/citas/cancelar/{id}")
    public String cancelarCita(@PathVariable int id,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {

        Usuario usuario = validarSesion(session);
        if (usuario == null) return "redirect:/essalud/login";

        try {
            // Obtener la cita antes de cancelarla
            Cita cita = serviceCita.obtenerPorId(id);

            if (cita == null) {
                redirectAttributes.addFlashAttribute("error", "Cita no encontrada.");
                return "redirect:/essalud/index";
            }

            boolean exito = serviceCita.cancelarCita(id);

            if (exito) {
                // Cambiar disponibilidad del horario a true
                serviceHorarioDisponible.actualizarDisponibilidad(cita.getIdHorario(), true);
                redirectAttributes.addFlashAttribute("mensaje", "Cita cancelada correctamente.");
            } else {
                redirectAttributes.addFlashAttribute("error", "No se pudo cancelar la cita.");
            }

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        }

        return "redirect:/essalud/index";
    }




    @GetMapping("/citas/reprogramar/{id}")
    public String mostrarFormularioReprogramacion(@PathVariable int id,
                                                  HttpSession session,
                                                  Model model) {
        Usuario usuario = validarSesion(session);
        if (usuario == null) return "redirect:/essalud/login";

        Cita cita = serviceCita.obtenerPorId(id);
        if (cita == null || !cita.getIdUsuario().equals(usuario.getIdUsuario())) {
            return "redirect:/essalud/index";
        }

        // Obtener nombres completos
        Medico medico = serviceMedico.obtenerPorId(cita.getIdMedico());
        Especialidad especialidad = serviceEspecialidad.obtenerPorId(medico.getIdEspecialidad());

        // Agregar nombres al objeto cita o al modelo
        model.addAttribute("nombreMedico", medico.getNombreCompleto());
        model.addAttribute("nombreEspecialidad", especialidad.getNombre());

        if (cita.getEstado().equals("Reprogramado")) {
            model.addAttribute("error", "Esta cita ya ha sido reprogramada una vez.");
        }

        List<HorarioDisponible> horarios = serviceHorarioDisponible.listarPorCentroMedicoYEspecialidad(
                        cita.getIdCentroMedico(),
                        medico.getIdEspecialidad()
                ).stream()
                .filter(h -> h.getIdHorario() != cita.getIdHorario())
                .collect(Collectors.toList());

        model.addAttribute("cita", cita);
        model.addAttribute("horarios", horarios);
        return "reprogramarCita";
    }

    @PostMapping("/citas/reprogramar/{id}")
    public String procesarReprogramacion(@PathVariable int id,
                                         @RequestParam("nuevoIdHorario") int nuevoIdHorario,
                                         HttpSession session,
                                         RedirectAttributes redirectAttributes) {
        Usuario usuario = validarSesion(session);
        if (usuario == null) return "redirect:/essalud/login";

        try {
            // Obtener la cita actual antes de modificarla
            Cita citaActual = serviceCita.obtenerPorId(id);
            if (citaActual == null) {
                redirectAttributes.addFlashAttribute("error", "Cita no encontrada.");
                return "redirect:/essalud/index";
            }

            int idHorarioAnterior = citaActual.getIdHorario();

            // Reprogramar la cita con el nuevo horario
            serviceCita.reprogramarCita(id, nuevoIdHorario);

            // Marcar el nuevo horario como no disponible
            serviceHorarioDisponible.actualizarDisponibilidad(nuevoIdHorario, false);

            // Marcar el horario anterior como disponible
            serviceHorarioDisponible.actualizarDisponibilidad(idHorarioAnterior, true);

            redirectAttributes.addFlashAttribute("mensaje", "Cita reprogramada exitosamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al reprogramar: " + e.getMessage());
            return "redirect:/essalud/citas/reprogramar/" + id;
        }

        return "redirect:/essalud/index";
    }

    // Métodos auxiliares privados
    private Usuario validarSesion(HttpSession session) {
        return (Usuario) session.getAttribute("usuario");
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
