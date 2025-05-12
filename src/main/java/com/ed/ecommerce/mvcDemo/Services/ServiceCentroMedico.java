package com.ed.ecommerce.mvcDemo.Services;

import com.ed.ecommerce.mvcDemo.Model.CentroMedico;
import com.ed.ecommerce.mvcDemo.Repository.ICentroMedicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServiceCentroMedico {

    @Autowired
    private ICentroMedicoRepository centroMedicoRepository;

    public List<CentroMedico> listarTodos() {
        return centroMedicoRepository.listarTodos();
    }

    public CentroMedico obtenerPorId(int idCentroMedico) {
        return centroMedicoRepository.obtenerPorId(idCentroMedico);
    }
}
