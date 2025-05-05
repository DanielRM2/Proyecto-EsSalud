package com.ed.ecommerce.mvcDemo.Services;

import com.ed.ecommerce.mvcDemo.Model.Usuario;
import com.ed.ecommerce.mvcDemo.Repository.IUsuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ServiceUsuario {

    @Autowired
    private IUsuario repo;

    public Usuario iniciarSesion(String correo, String contrasena) {
        return repo.iniciarSesion(correo, contrasena);
    }

    public boolean registrar(Usuario usuario) {
        return repo.registrar(usuario);
    }

    public Usuario obtenerPorId(int idUsuario) {
        return repo.obtenerPorId(idUsuario);
    }

    public boolean actualizarDatos(Usuario usuario) {
        return repo.actualizarDatos(usuario);
    }
}
