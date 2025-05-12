package com.ed.ecommerce.mvcDemo.Services;

import com.ed.ecommerce.mvcDemo.Model.Especialidad;
import com.ed.ecommerce.mvcDemo.Repository.IEspecialidadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServiceEspecialidad {
    @Autowired
    private IEspecialidadRepository especialidadRepository;

    public List<Especialidad> listarTodas() {
        return especialidadRepository.listarTodas();
    }

    public Especialidad obtenerPorId(int idEspecialidad) {
        return especialidadRepository.obtenerPorId(idEspecialidad);
    }
}
