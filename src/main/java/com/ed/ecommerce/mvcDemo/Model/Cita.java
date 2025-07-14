package com.ed.ecommerce.mvcDemo.Model;

import java.time.LocalDateTime;
import java.util.List;

public class Cita {
    private Integer idCita;
    private Integer idUsuario;
    private Integer idMedico;
    private Integer idCentroMedico;
    private Integer idHorario;
    private LocalDateTime fechaCita;
    private String estado;
    private String nombreMedico;
    private String nombreCentro;
    private String nombreEspecialidad;

    public static final List<String> ESTADOS_VALIDOS = List.of("Pendiente", "Confirmado", "Cancelado", "Asistido", "Reprogramado");

    public Cita() {}

    public Cita(Integer idCita, Integer idUsuario, Integer idMedico, Integer idCentroMedico,
                Integer idHorario, LocalDateTime fechaCita, String estado,
                String nombreMedico, String nombreCentro, String nombreEspecialidad) {
        this.idCita = idCita;
        this.idUsuario = idUsuario;
        this.idMedico = idMedico;
        this.idCentroMedico = idCentroMedico;
        this.idHorario = idHorario;
        this.setFechaCita(fechaCita);
        this.setEstado(estado);
        this.nombreMedico = nombreMedico;
        this.nombreCentro = nombreCentro;
        this.nombreEspecialidad = nombreEspecialidad;
    }

    // Getters y Setters

    public Integer getIdCita() { return idCita; }
    public void setIdCita(Integer idCita) { this.idCita = idCita; }

    public Integer getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Integer idUsuario) { this.idUsuario = idUsuario; }

    public Integer getIdMedico() { return idMedico; }
    public void setIdMedico(Integer idMedico) { this.idMedico = idMedico; }

    public Integer getIdCentroMedico() { return idCentroMedico; }
    public void setIdCentroMedico(Integer idCentroMedico) { this.idCentroMedico = idCentroMedico; }

    public Integer getIdHorario() { return idHorario; }
    public void setIdHorario(Integer idHorario) { this.idHorario = idHorario; }

    public LocalDateTime getFechaCita() { return fechaCita; }
    public void setFechaCita(LocalDateTime fechaCita) { this.fechaCita = fechaCita; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) {
        if (estado != null) {
            estado = estado.trim(); // Trim the input string
            if (!ESTADOS_VALIDOS.contains(estado)) {
                throw new IllegalArgumentException("Estado no válido. Use: " + ESTADOS_VALIDOS);
            }
            this.estado = estado;
        }
    }

    public String getNombreMedico() { return nombreMedico; }
    public void setNombreMedico(String nombreMedico) { this.nombreMedico = nombreMedico; }

    public String getNombreCentro() { return nombreCentro; }
    public void setNombreCentro(String nombreCentro) { this.nombreCentro = nombreCentro; }

    public String getNombreEspecialidad() { return nombreEspecialidad; }
    public void setNombreEspecialidad(String nombreEspecialidad) { this.nombreEspecialidad = nombreEspecialidad; }

    @Override
    public String toString() {
        return "Cita [id=" + idCita + ", fecha=" + fechaCita + ", médico=" + nombreMedico + ", estado=" + estado + "]";
    }
}
