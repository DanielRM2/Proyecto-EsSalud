package com.ed.ecommerce.mvcDemo.Repository;

import java.util.List;
import com.ed.ecommerce.mvcDemo.Model.HorarioDisponible;

public interface IHorariosDisponibles {

    List<HorarioDisponible> listarPorCentroMedicoYEspecialidad(int idCentroMedico, int idEspecialidad);

    HorarioDisponible obtenerPorId(int idHorario);

    boolean actualizarDisponibilidad(int idHorario, boolean disponible);

}
