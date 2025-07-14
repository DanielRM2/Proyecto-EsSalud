package com.ed.ecommerce.mvcDemo.Repository;

import com.ed.ecommerce.mvcDemo.Model.Cita;

import java.time.LocalDateTime;
import java.util.List;

public interface ICitaRepository {

    boolean agendarCita(Cita cita);
    List<Cita> listarCitasPorUsuario(int idUsuario);
    boolean cancelarCita(int idCita);
    boolean reprogramarCita(int idCita, int nuevoIdHorario);
    boolean existeCitaParaHorario(int idHorario);
    Cita obtenerPorId(int idCita);
    boolean haSidoReprogramada(int idCita);

    // Métodos para el historial de citas
    List<Cita> findCitasActivasPorUsuarioYEstados(int idUsuario, List<String> estados);
    List<Cita> findCitasPorUsuarioYEstado(int idUsuario, String estado);
    int countCitasPorUsuario(int idUsuario);
    int countCitasPorUsuarioYEstado(int idUsuario, String estado);
    boolean actualizarEstadoCita(int idCita, String estado);
}