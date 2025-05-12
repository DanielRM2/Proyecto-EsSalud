package com.ed.ecommerce.mvcDemo.Repository;

import com.ed.ecommerce.mvcDemo.Model.Cita;

import java.time.LocalDateTime;
import java.util.List;

public interface ICitaRepository {
    boolean agendarCita(Cita cita);
    List<Cita> listarCitasPorUsuario(int idUsuario);
    boolean cancelarCita(int idCita);
    boolean reprogramarCita(int idCita, LocalDateTime nuevaFecha);
    boolean existeCitaParaMedico(int idMedico, LocalDateTime fecha);

}
