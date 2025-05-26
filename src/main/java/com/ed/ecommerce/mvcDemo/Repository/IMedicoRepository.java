package com.ed.ecommerce.mvcDemo.Repository;

import com.ed.ecommerce.mvcDemo.Model.Medico;

import java.util.List;

public interface IMedicoRepository {

    List<Medico> findByEspecialidadAndCentroMedico(int idEspecialidad, int idCentroMedico);
    Medico obtenerPorId(int idMedico);

}
