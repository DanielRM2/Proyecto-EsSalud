package com.ed.ecommerce.mvcDemo.Services;

import com.ed.ecommerce.mvcDemo.Model.Cita;
import com.ed.ecommerce.mvcDemo.Model.HorarioDisponible;
import com.ed.ecommerce.mvcDemo.Model.Medico;
import com.ed.ecommerce.mvcDemo.Exceptions.RecursoNoEncontradoException; // Asegúrate de que esta clase exista
import com.ed.ecommerce.mvcDemo.Repository.ICitaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
public class ServiceCita {

    private final ICitaRepository citaRepository;
    private final ServiceHorarioDisponible serviceHorarioDisponible;
    private final ServiceMedico serviceMedico;

    @Autowired
    public ServiceCita(ICitaRepository citaRepository,
                      ServiceHorarioDisponible serviceHorarioDisponible,
                      ServiceMedico serviceMedico) {
        this.citaRepository = citaRepository;
        this.serviceHorarioDisponible = serviceHorarioDisponible;
        this.serviceMedico = serviceMedico;
    }

    public boolean agendarCita(Cita cita) {
        if (cita.getIdUsuario() == null || cita.getIdUsuario() <= 0) {
            throw new IllegalArgumentException("ID de usuario no válido.");
        }
        if (cita.getIdHorario() == null || cita.getIdHorario() <= 0) {
            throw new IllegalArgumentException("ID de horario no válido.");
        }

        // 1. Validar que el usuario no tenga citas pendientes, reprogramadas, confirmados
        List<String> estadosActivos = Arrays.asList("Pendiente", "Reprogramado", "Confirmado");
        List<Cita> citasActivasUsuario = citaRepository.findCitasActivasPorUsuarioYEstados(cita.getIdUsuario(), estadosActivos);
        if (!citasActivasUsuario.isEmpty()) {
            throw new IllegalStateException("Usted ya tiene una cita activa. Debe asistir a su cita o cancelarla para poder agendar una nueva.");
        }

        // 2. Obtener detalles del horario y médico
        HorarioDisponible horario = serviceHorarioDisponible.obtenerPorId(cita.getIdHorario());
        if (horario == null) {
            throw new RecursoNoEncontradoException("Horario disponible no encontrado.");
        }
        if (!horario.isDisponible()) {
            throw new IllegalStateException("El horario seleccionado ya no está disponible.");
        }

        Medico medico = serviceMedico.obtenerPorId(horario.getIdMedico());
        if (medico == null) {
            throw new RecursoNoEncontradoException("Médico asociado al horario no encontrado.");
        }

        // 3. Rellenar los campos de la cita que provienen de HorarioDisponible y Medico
        cita.setIdMedico(medico.getIdMedico());
        cita.setIdCentroMedico(medico.getIdCentroMedico()); // Asumiendo que Medico tiene idCentroMedico
        cita.setEstado("Pendiente");
        cita.setFechaCita(LocalDateTime.of(horario.getFecha(), horario.getHora()));

        // 4. Agendar la cita en el repositorio
        boolean agendado = citaRepository.agendarCita(cita);

        // 5. Actualizar la disponibilidad del horario si la cita fue agendada con éxito
        if (agendado) {
            serviceHorarioDisponible.actualizarDisponibilidad(horario.getIdHorario(), false); // Marcar como no disponible
        }
        return agendado;
    }

    public List<Cita> obtenerCitasPorUsuario(Integer idUsuario) {
        if (idUsuario == null || idUsuario <= 0) {
            throw new IllegalArgumentException("ID de usuario no válido.");
        }
        return citaRepository.listarCitasPorUsuario(idUsuario);
    }

    public boolean cancelarCita(Integer idCita, HttpSession session) {
        if (idCita == null || idCita <= 0) {
            throw new IllegalArgumentException("ID de cita no válido.");
        }

        // Obtener o inicializar el contador de cancelaciones de la sesión
        Integer contadorCancelaciones = (Integer) session.getAttribute("contadorCancelaciones");
        LocalDateTime bloqueoHasta = (LocalDateTime) session.getAttribute("bloqueoHasta");

        // Verificar si el usuario está bloqueado
        if (bloqueoHasta != null && LocalDateTime.now().isBefore(bloqueoHasta)) {
            throw new IllegalStateException("No puede cancelar más citas. Está bloqueado hasta: " + bloqueoHasta);
        }

        // Obtener la cita para validar que existe
        Cita cita = obtenerPorId(idCita);
        if (cita == null) {
            throw new RecursoNoEncontradoException("Cita no encontrada.");
        }

        // Actualizar el contador de cancelaciones
        if (contadorCancelaciones == null) {
            contadorCancelaciones = 1;
        } else {
            contadorCancelaciones++;
        }

        // Aplicar bloqueo progresivo después de 2 cancelaciones
        if (contadorCancelaciones > 2) {
            int minutosBloqueo = (int) Math.pow(5, (contadorCancelaciones - 2)); // 5, 25, 125, 625 minutos...
            bloqueoHasta = LocalDateTime.now().plusMinutes(minutosBloqueo);
            session.setAttribute("bloqueoHasta", bloqueoHasta);
            
            // Mostrar mensaje con el tiempo de bloqueo
            String mensajeBloqueo = String.format("Has alcanzado el límite de cancelaciones. " +
                "Podrás agendar una nueva cita en %d minutos.", minutosBloqueo);
            session.setAttribute("mensajeBloqueo", mensajeBloqueo);
        }

        // Actualizar el contador en la sesión
        session.setAttribute("contadorCancelaciones", contadorCancelaciones);

        // Cancelar la cita
        return citaRepository.cancelarCita(idCita);
    }

    public boolean reprogramarCita(Integer idCita, Integer nuevoIdHorario) {
        if (idCita == null || idCita <= 0) {
            throw new IllegalArgumentException("ID de cita no válido.");
        }
        if (nuevoIdHorario == null || nuevoIdHorario <= 0) {
            throw new IllegalArgumentException("ID de nuevo horario no válido.");
        }

        // Validar que el nuevo horario no esté ocupado por OTRA cita (no la que estamos reprogramando)
        if (citaRepository.existeCitaParaHorario(nuevoIdHorario)) {
            // Se podría añadir una validación más específica aquí para no marcar la misma cita si el ID de horario no cambió.
            throw new IllegalStateException("El nuevo horario ya está ocupado por otra cita.");
        }

        // Nota: La lógica para liberar el horario viejo y ocupar el nuevo NO está implementada aquí
        // en esta versión simplificada del ServiceCita. Se asumiría que el controlador, o un
        // método más completo, se encargaría de ello.
        return citaRepository.reprogramarCita(idCita, nuevoIdHorario);
    }

    public boolean haSidoReprogramada(Integer idCita) {
        if (idCita == null || idCita <= 0) {
            throw new IllegalArgumentException("ID de cita no válido.");
        }
        return citaRepository.haSidoReprogramada(idCita);
    }

    public Cita obtenerPorId(int idCita) {
        if (idCita <= 0) {
            throw new IllegalArgumentException("ID de cita no válido.");
        }
        return citaRepository.obtenerPorId(idCita);
    }

    // Obtener citas por usuario y estado
    public List<Cita> obtenerCitasPorUsuarioYEstado(Integer idUsuario, String estado) {
        if (idUsuario == null || idUsuario <= 0) {
            throw new IllegalArgumentException("ID de usuario no válido.");
        }
        if (estado == null || estado.trim().isEmpty()) {
            throw new IllegalArgumentException("El estado no puede estar vacío.");
        }
        return citaRepository.findCitasPorUsuarioYEstado(idUsuario, estado);
    }

    // Contar todas las citas de un usuario
    public int contarCitasPorUsuario(Integer idUsuario) {
        if (idUsuario == null || idUsuario <= 0) {
            throw new IllegalArgumentException("ID de usuario no válido.");
        }
        return citaRepository.countCitasPorUsuario(idUsuario);
    }

    // Contar citas de un usuario por estado
    public int contarCitasPorUsuarioYEstado(Integer idUsuario, String estado) {
        if (idUsuario == null || idUsuario <= 0) {
            throw new IllegalArgumentException("ID de usuario no válido.");
        }
        if (estado == null || estado.trim().isEmpty()) {
            throw new IllegalArgumentException("El estado no puede estar vacío.");
        }
        return citaRepository.countCitasPorUsuarioYEstado(idUsuario, estado);
    }
}