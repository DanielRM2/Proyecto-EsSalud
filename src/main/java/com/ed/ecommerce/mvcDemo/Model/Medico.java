package com.ed.ecommerce.mvcDemo.Model;

public class Medico {

    private int idMedico;
    private String nombre;
    private String apellido;
    private int idEspecialidad;
    private int idCentroMedico;
    private boolean estado; //true = activo, false = inactivo)
    private String especialidad; // Para mostrar en vistas
    private String centroMedico; // Para mostrar en vistas

    // Constructores
    public Medico() {
    }

    public Medico(int idMedico, String nombre, String apellido,
                  int idEspecialidad, int idCentroMedico,
                  boolean estado) {
        this.idMedico = idMedico;
        this.nombre = nombre;
        this.apellido = apellido;
        this.idEspecialidad = idEspecialidad;
        this.idCentroMedico = idCentroMedico;
        this.estado = estado;
    }

    // Constructor completo
    public Medico(int idMedico, String nombre, String apellido,
                  int idEspecialidad, int idCentroMedico,
                  boolean estado, String especialidad,
                  String centroMedico) {
        this.idMedico = idMedico;
        this.nombre = nombre;
        this.apellido = apellido;
        this.idEspecialidad = idEspecialidad;
        this.idCentroMedico = idCentroMedico;
        this.estado = estado;
        this.especialidad = especialidad;
        this.centroMedico = centroMedico;
    }

    // Getters y Setters
    public int getIdMedico() { return idMedico; }
    public void setIdMedico(int idMedico) { this.idMedico = idMedico; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public int getIdEspecialidad() { return idEspecialidad; }
    public void setIdEspecialidad(int idEspecialidad) { this.idEspecialidad = idEspecialidad; }

    public int getIdCentroMedico() { return idCentroMedico; }
    public void setIdCentroMedico(int idCentroMedico) { this.idCentroMedico = idCentroMedico; }

    public boolean isEstado() { return estado; }
    public void setEstado(boolean estado) { this.estado = estado; }

    public String getEspecialidad() { return especialidad; }
    public void setEspecialidad(String especialidad) { this.especialidad = especialidad; }

    public String getCentroMedico() { return centroMedico; }
    public void setCentroMedico(String centroMedico) { this.centroMedico = centroMedico; }

    // Métodos auxiliares
    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }

    public String getEstadoTexto() {
        return estado ? "Activo" : "Inactivo";
    }
}

