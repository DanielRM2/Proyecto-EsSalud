package com.ed.ecommerce.mvcDemo.Model;

import java.time.LocalDate;
import java.time.LocalTime;

public class HorarioDisponible {
    private Integer idHorario;
    private Integer idMedico;
    private LocalDate fecha;
    private LocalTime hora;
    private Boolean disponible;
    private String nombreMedico;

    public String getNombreMedico() {
        return nombreMedico;
    }

    public void setNombreMedico(String nombreMedico) {
        this.nombreMedico = nombreMedico;
    }

    public HorarioDisponible() {}

    public HorarioDisponible(Integer idHorario, Integer idMedico,
                             LocalDate fecha, LocalTime hora, Boolean disponible) {
        this.idHorario = idHorario;
        this.idMedico = idMedico;
        this.fecha = fecha;
        this.hora = hora;
        this.disponible = disponible;
    }

    public Integer getIdHorario() { return idHorario; }
    public void setIdHorario(Integer idHorario) { this.idHorario = idHorario; }

    public Integer getIdMedico() { return idMedico; }
    public void setIdMedico(Integer idMedico) { this.idMedico = idMedico; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public LocalTime getHora() { return hora; }
    public void setHora(LocalTime hora) { this.hora = hora; }

    public Boolean getDisponible() { return disponible; }
    public void setDisponible(Boolean disponible) { this.disponible = disponible; }
}
