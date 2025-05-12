package com.ed.ecommerce.mvcDemo.Repository;

import com.ed.ecommerce.mvcDemo.Model.CentroMedico;

import java.util.List;

public interface ICentroMedicoRepository {
    List<CentroMedico> listarTodos();
    CentroMedico obtenerPorId(int idCentroMedico);


}
