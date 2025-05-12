package com.ed.ecommerce.mvcDemo.Repository;

import com.ed.ecommerce.mvcDemo.Model.Especialidad;

import java.util.List;

public interface IEspecialidadRepository {
    List<Especialidad> listarTodas();
    Especialidad obtenerPorId(int idEspecialidad);
}
