package com.ed.ecommerce.mvcDemo.Repository;

import com.ed.ecommerce.mvcDemo.Model.Usuario;

public interface IUsuario {

    //Metodo para iniciar sesión con el correo y la contraseña del usuario
    Usuario iniciarSesion(String correo, String contrasena);

    //Metodo para registrar un nuevo usuario en la base de datos
    boolean registrar(Usuario usuario);

    //Metodo para obtener un usuario desde la base de datos usando su ID
    Usuario obtenerPorId(int idUsuario);

    //Metodo para actualizar los datos de un usuario en la base de datos
    boolean actualizarDatos(Usuario usuario);

}
