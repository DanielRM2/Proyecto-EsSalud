package com.ed.ecommerce.mvcDemo.Model;

import java.time.LocalDate;
import java.time.LocalTime;

public class HorarioDisponible {
    private Integer idHorario;
    private Integer idMedico;
    private Integer idCentroMedico; // <-- ¡IMPORTANTE: Añadido este campo de nuevo!
    private LocalDate fecha;
    private LocalTime hora;
    private Boolean disponible;
    private String nombreMedico;

    public HorarioDisponible() {}

    // Constructor actualizado para incluir idCentroMedico y nombreMedico (opcional)
    public HorarioDisponible(Integer idHorario, Integer idMedico, Integer idCentroMedico,
                             LocalDate fecha, LocalTime hora, Boolean disponible, String nombreMedico) {
        this.idHorario = idHorario;
        this.idMedico = idMedico;
        this.idCentroMedico = idCentroMedico; // <-- Asegúrate de inicializarlo
        this.fecha = fecha;
        this.hora = hora;
        this.disponible = disponible;
        this.nombreMedico = nombreMedico; // <-- Inicializa el nuevo campo
    }

    // Constructor si no siempre necesitas nombreMedico
    public HorarioDisponible(Integer idHorario, Integer idMedico, Integer idCentroMedico,
                             LocalDate fecha, LocalTime hora, Boolean disponible) {
        this.idHorario = idHorario;
        this.idMedico = idMedico;
        this.idCentroMedico = idCentroMedico;
        this.fecha = fecha;
        this.hora = hora;
        this.disponible = disponible;
    }


    public String getNombreMedico() {
        return nombreMedico;
    }

    public void setNombreMedico(String nombreMedico) {
        this.nombreMedico = nombreMedico;
    }

    public Integer getIdHorario() { return idHorario; }
    public void setIdHorario(Integer idHorario) { this.idHorario = idHorario; }

    public Integer getIdMedico() { return idMedico; }
    public void setIdMedico(Integer idMedico) { this.idMedico = idMedico; }

    // Getter y Setter para idCentroMedico
    public Integer getIdCentroMedico() { return idCentroMedico; }
    public void setIdCentroMedico(Integer idCentroMedico) { this.idCentroMedico = idCentroMedico; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public LocalTime getHora() { return hora; }
    public void setHora(LocalTime hora) { this.hora = hora; }

    // Cambiado de getDisponible() a isDisponible() para seguir la convención de Java para Booleans
    public Boolean isDisponible() { return disponible; } // <-- ¡IMPORTANTE: Renombrado a isDisponible()!
    public void setDisponible(Boolean disponible) { this.disponible = disponible; }
}