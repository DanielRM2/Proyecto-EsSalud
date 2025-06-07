package com.ed.ecommerce.mvcDemo.Services;

import com.ed.ecommerce.mvcDemo.Model.Cita;
import com.ed.ecommerce.mvcDemo.Repository.ICitaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServiceCita {
    private final ICitaRepository citaRepository;

    @Autowired
    public ServiceCita(ICitaRepository citaRepository) {
        this.citaRepository = citaRepository;
    }

    public boolean agendarCita(Cita cita) {
        if (cita.getIdHorario() <= 0) {
            throw new IllegalArgumentException("ID de horario no válido");
        }

        if (citaRepository.existeCitaParaHorario(cita.getIdHorario())) {
            throw new IllegalStateException("Ese horario ya está ocupado");
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

    public boolean reprogramarCita(Integer idCita, Integer nuevoIdHorario) {
        if (idCita == null || idCita <= 0) {
            throw new IllegalArgumentException("ID de cita no válido");
        }

        if (nuevoIdHorario == null || nuevoIdHorario <= 0) {
            throw new IllegalArgumentException("ID de nuevo horario no válido");
        }

        if (citaRepository.existeCitaParaHorario(nuevoIdHorario)) {
            throw new IllegalStateException("El nuevo horario ya está ocupado");
        }

        return citaRepository.reprogramarCita(idCita, nuevoIdHorario);
    }

    public boolean haSidoReprogramada(Integer idCita) {
        if (idCita == null || idCita <= 0) {
            throw new IllegalArgumentException("ID de cita no válido");
        }
        return citaRepository.haSidoReprogramada(idCita);
    }


    public Cita obtenerPorId(int idCita) {
        return citaRepository.obtenerPorId(idCita);
    }
}
