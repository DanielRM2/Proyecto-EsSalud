package com.ed.ecommerce.mvcDemo.Model;

public class CentroMedico {
    private int idCentroMedico;
    private String nombre;
    private String direccion;

    // Constructores
    public CentroMedico() {}

    public CentroMedico(int idCentroMedico, String nombre, String direccion) {
        this.idCentroMedico = idCentroMedico;
        this.nombre = nombre;
        this.direccion = direccion;
    }

    // Getters y Setters
    public int getIdCentroMedico() {
        return idCentroMedico;
    }

    public void setIdCentroMedico(int idCentroMedico) {
        this.idCentroMedico = idCentroMedico;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }
}
