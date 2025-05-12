package com.ed.ecommerce.mvcDemo.Services;

import com.ed.ecommerce.mvcDemo.Model.Cita;
import com.ed.ecommerce.mvcDemo.Repository.ICitaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ServiceCita {
    private final ICitaRepository citaRepository;

    @Autowired
    public ServiceCita(ICitaRepository citaRepository) {
        this.citaRepository = citaRepository;
    }

    public boolean agendarCita(Cita cita) {
        if (cita.getFechaCita().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("No se pueden agendar citas en fechas pasadas");
        }

        if (citaRepository.existeCitaParaMedico(cita.getIdMedico(), cita.getFechaCita())) {
            throw new IllegalStateException("El médico ya tiene una cita programada en ese horario");
        }

        cita.setEstado("Pendiente");
        return citaRepository.agendarCita(cita);
    }

    public List<Cita> obtenerCitasPorUsuario(Integer idUsuario) {
        if (idUsuario == null || idUsuario <= 0) {
            throw new IllegalArgumentException("ID de usuario no válido");
        }
        return citaRepository.listarCitasPorUsuario(idUsuario);
    }

    public boolean cancelarCita(Integer idCita) {
        if (idCita == null || idCita <= 0) {
            throw new IllegalArgumentException("ID de cita no válido");
        }
        return citaRepository.cancelarCita(idCita);
    }

    public boolean reprogramarCita(Integer idCita, LocalDateTime nuevaFecha) {
        if (idCita == null || idCita <= 0) {
            throw new IllegalArgumentException("ID de cita no válido");
        }
        if (nuevaFecha == null || nuevaFecha.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("La nueva fecha no puede ser en el pasado");
        }
        return citaRepository.reprogramarCita(idCita, nuevaFecha);
    }
}
