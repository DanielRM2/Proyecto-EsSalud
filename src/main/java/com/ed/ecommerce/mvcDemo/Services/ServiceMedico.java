package com.ed.ecommerce.mvcDemo.Services;


import com.ed.ecommerce.mvcDemo.Model.Medico;
import com.ed.ecommerce.mvcDemo.Repository.IMedicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServiceMedico {
    @Autowired
    private IMedicoRepository medicoRepository;
    public List<Medico> listarPorEspecialidadYCentro(int idEspecialidad, int idCentroMedico) {
        return medicoRepository.findByEspecialidadAndCentroMedico(idEspecialidad, idCentroMedico);
        }
    public Medico obtenerPorId(int idMedico) {
        return medicoRepository.obtenerPorId(idMedico);
    }
    }

