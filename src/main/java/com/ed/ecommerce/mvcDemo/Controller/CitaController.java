package com.ed.ecommerce.mvcDemo.Controller;

import com.ed.ecommerce.mvcDemo.Model.*;
import com.ed.ecommerce.mvcDemo.Services.*;
import com.ed.ecommerce.mvcDemo.Exceptions.RecursoNoEncontradoException; // NECESARIO: Asegúrate de que esta clase exista
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
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
            e.printStackTrace(); // Para depuración
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

    @PostMapping("/citas/agendar")
    public String procesarAgendarCita(HttpSession session,
                                      @RequestParam("idHorario") Integer idHorario,
                                      RedirectAttributes redirectAttributes) {
        Usuario usuario = validarSesion(session);
        if (usuario == null) {
            return "redirect:/essalud/login";
        }

        try {
            // Crea un objeto Cita con los datos mínimos. ServiceCita se encargará del resto.
            Cita nuevaCita = new Cita();
            nuevaCita.setIdUsuario(usuario.getIdUsuario());
            nuevaCita.setIdHorario(idHorario);

            // Delega la lógica de agendamiento y validación al ServiceCita
            boolean exito = serviceCita.agendarCita(nuevaCita);

            if (exito) {
                redirectAttributes.addFlashAttribute("mensaje", "Cita agendada correctamente.");
            } else {
                redirectAttributes.addFlashAttribute("error", "No se pudo agendar la cita.");
            }

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (RecursoNoEncontradoException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) { // Captura cualquier otra excepción inesperada
            redirectAttributes.addFlashAttribute("error", "Error inesperado al agendar cita: " + e.getMessage());
            e.printStackTrace(); // Para depuración
        }

        return "redirect:/essalud/index";
    }

    @PostMapping("/citas/cancelar/{id}")
    public String cancelarCita(@PathVariable("id") int idCita,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {

        Usuario usuario = validarSesion(session);
        if (usuario == null) return "redirect:/essalud/login";

        try {
            // Obtener la cita antes de cancelarla para obtener el idHorario
            Cita cita = serviceCita.obtenerPorId(idCita);

            if (cita == null) {
                redirectAttributes.addFlashAttribute("error", "Cita no encontrada.");
                return "redirect:/essalud/index";
            }

            boolean exito = serviceCita.cancelarCita(idCita);

            if (exito) {
                // Marcar el horario como disponible nuevamente
                serviceHorarioDisponible.actualizarDisponibilidad(cita.getIdHorario(), true);
                redirectAttributes.addFlashAttribute("mensaje", "Cita cancelada correctamente.");
            } else {
                redirectAttributes.addFlashAttribute("error", "No se pudo cancelar la cita.");
            }

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (RecursoNoEncontradoException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) { // Captura cualquier otra excepción inesperada
            redirectAttributes.addFlashAttribute("error", "Error al cancelar cita: " + e.getMessage());
            e.printStackTrace(); // Para depuración
        }

        return "redirect:/essalud/index";
    }

    @GetMapping("/citas/reprogramar/{id}")
    public String mostrarFormularioReprogramacion(@PathVariable("id") int idCita,
                                                  HttpSession session,
                                                  Model model) {
        Usuario usuario = validarSesion(session);
        if (usuario == null) return "redirect:/essalud/login";

        Cita cita = serviceCita.obtenerPorId(idCita);
        if (cita == null || !cita.getIdUsuario().equals(usuario.getIdUsuario())) {
            return "redirect:/essalud/index";
        }

        Medico medico = serviceMedico.obtenerPorId(cita.getIdMedico());
        Especialidad especialidad = null;

        // **LÍNEA 206:** Aquí se eliminó la comprobación 'medico.getIdEspecialidad() != null'
        // porque getIdEspecialidad() devuelve un 'int' primitivo, que nunca puede ser null.
        if (medico != null && medico.getIdEspecialidad() > 0) {
            especialidad = serviceEspecialidad.obtenerPorId(medico.getIdEspecialidad());
        }

        if (medico != null) {
            model.addAttribute("nombreMedico", medico.getNombreCompleto());
        }
        if (especialidad != null) {
            model.addAttribute("nombreEspecialidad", especialidad.getNombre());
        }

        // Verificar si la cita ya ha sido reprogramada
        try {
            if (serviceCita.haSidoReprogramada(idCita)) {
                model.addAttribute("error", "Esta cita ya ha sido reprogramada una vez.");
            }
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
        }

        List<HorarioDisponible> horarios = serviceHorarioDisponible.listarPorCentroMedicoYEspecialidad(
                        cita.getIdCentroMedico(),
                        // **LÍNEA 229:** Aquí se eliminó la comprobación 'medico.getIdEspecialidad() != null'
                        // porque getIdEspecialidad() devuelve un 'int' primitivo, que nunca puede ser null.
                        (medico != null ? medico.getIdEspecialidad() : 0) // Pasar 0 si no hay ID de especialidad válido
                ).stream()
                .filter(h -> h.getIdHorario() != cita.getIdHorario()) // Excluir el horario actual de la cita
                .collect(Collectors.toList());

        model.addAttribute("cita", cita);
        model.addAttribute("horarios", horarios);
        return "reprogramarCita";
    }

    @PostMapping("/citas/reprogramar/{id}")
    public String procesarReprogramacion(@PathVariable("id") int idCita,
                                         @RequestParam("nuevoIdHorario") int nuevoIdHorario,
                                         HttpSession session,
                                         RedirectAttributes redirectAttributes) {
        Usuario usuario = validarSesion(session);
        if (usuario == null) return "redirect:/essalud/login";

        try {
            // Obtener la cita actual antes de modificarla
            Cita citaActual = serviceCita.obtenerPorId(idCita);
            if (citaActual == null) {
                throw new RecursoNoEncontradoException("Cita no encontrada.");
            }

            int idHorarioAnterior = citaActual.getIdHorario();

            // Reprogramar la cita con el nuevo horario a través del servicio
            boolean exito = serviceCita.reprogramarCita(idCita, nuevoIdHorario);

            if (exito) {
                // Marcar el nuevo horario como no disponible
                serviceHorarioDisponible.actualizarDisponibilidad(nuevoIdHorario, false);
                // Marcar el horario anterior como disponible
                serviceHorarioDisponible.actualizarDisponibilidad(idHorarioAnterior, true);

                redirectAttributes.addFlashAttribute("mensaje", "Cita reprogramada exitosamente.");
            } else {
                redirectAttributes.addFlashAttribute("error", "No se pudo reprogramar la cita.");
            }

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/essalud/citas/reprogramar/" + idCita;
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/essalud/citas/reprogramar/" + idCita;
        } catch (RecursoNoEncontradoException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/essalud/citas/reprogramar/" + idCita;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error inesperado al reprogramar: " + e.getMessage());
            e.printStackTrace(); // Para depuración
            return "redirect:/essalud/citas/reprogramar/" + idCita;
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