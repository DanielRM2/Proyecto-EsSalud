package com.ed.ecommerce.mvcDemo.Services;

import com.ed.ecommerce.mvcDemo.Model.Especialidad;
import com.ed.ecommerce.mvcDemo.Model.HorarioDisponible;
import com.ed.ecommerce.mvcDemo.Repository.IHorariosDisponibles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServiceHorarioDisponible{

    @Autowired
    private IHorariosDisponibles horariosDisponiblesRepo;

    public List<HorarioDisponible> listarPorCentroMedicoYEspecialidad(int idCentroMedico, int idEspecialidad) {
        return horariosDisponiblesRepo.listarPorCentroMedicoYEspecialidad(idCentroMedico, idEspecialidad);
    }

    public HorarioDisponible obtenerPorId(int idHorario) {
        return horariosDisponiblesRepo.obtenerPorId(idHorario);
    }

    public boolean actualizarDisponibilidad(int idHorario, boolean disponible) {
        return horariosDisponiblesRepo.actualizarDisponibilidad(idHorario, disponible);
    }

}
