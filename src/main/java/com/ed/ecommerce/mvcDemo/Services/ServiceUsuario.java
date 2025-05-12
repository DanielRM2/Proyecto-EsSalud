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
        // Aquí debes verificar si el DNI o el correo ya existen
        if (verificarDniExistente(usuario.getDni()) || verificarCorreoExistente(usuario.getCorreo())) {
            return false; // Si alguno existe, no registrar
        }
        // Registrar el usuario si no existe el DNI o el correo
        return repo.registrar(usuario);
    }

    public boolean verificarDniExistente(String dni) {
        return repo.obtenerPorDni(dni) != null;
    }

    public boolean verificarCorreoExistente(String correo) {
        return repo.obtenerPorCorreo(correo) != null;
    }

    public Usuario obtenerPorId(int idUsuario) {
        return repo.obtenerPorId(idUsuario);
    }

    public boolean actualizarDatos(Usuario usuario) {
        return repo.actualizarDatos(usuario);
    }
}
