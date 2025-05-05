package com.ed.ecommerce.mvcDemo.Repository;

import com.ed.ecommerce.mvcDemo.Model.Usuario;
import com.ed.ecommerce.mvcDemo.Pattern.ConexionBD;
import org.springframework.stereotype.Repository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@Repository
public class IUsuarioImpl implements IUsuario {

    // Instancia del codificador de contraseñas BCrypt
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public Usuario iniciarSesion(String correo, String contrasena) {
        Usuario usuario = null;
        try {
            // Obtenemos la conexión de la base de datos
            Connection con = ConexionBD.getConexion();

            // Consultamos al usuario por su correo
            String sql = "SELECT * FROM Usuario WHERE correo = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, correo);
            ResultSet rs = ps.executeQuery();

            // Si se encuentra el usuario, verificamos la contraseña
            if (rs.next()) {
                String hashedPassword = rs.getString("contrasena");
                if (passwordEncoder.matches(contrasena, hashedPassword)) {
                    // Si las contraseñas coinciden, creamos el objeto Usuario con los datos del registro
                    usuario = new Usuario();
                    usuario.setIdUsuario(rs.getInt("idUsuario"));
                    usuario.setNombre(rs.getString("nombre"));
                    usuario.setApellido(rs.getString("apellido"));
                    usuario.setDni(rs.getString("dni"));
                    usuario.setCorreo(rs.getString("correo"));
                    usuario.setContrasena(hashedPassword);  // guardamos el hash de la contraseña
                }
            }
            // Cerramos la conexión con la base de datos
            con.close();
        } catch (Exception e) {
            e.printStackTrace();  // En caso de error, mostramos la traza del error
        }
        return usuario;  // Retornamos el usuario encontrado o null si no se encontró
    }

    @Override
    public boolean registrar(Usuario usuario) {
        try {
            // Obtenemos la conexión de la base de datos
            Connection con = ConexionBD.getConexion();

            // Insertamos los datos del nuevo usuario en la base de datos
            String sql = "INSERT INTO Usuario (nombre, apellido, dni, correo, contrasena) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, usuario.getNombre());
            ps.setString(2, usuario.getApellido());
            ps.setString(3, usuario.getDni());
            ps.setString(4, usuario.getCorreo());

            // Encriptamos la contraseña antes de guardarla
            ps.setString(5, passwordEncoder.encode(usuario.getContrasena())); // ENCRIPTAMOS AQUÍ

            // Ejecutamos la consulta para insertar el usuario
            ps.executeUpdate();
            con.close();  // Cerramos la conexión

            return true;  // Si la inserción fue exitosa, retornamos true
        } catch (Exception e) {
            e.printStackTrace();  // Si hay un error, mostramos la traza
        }
        return false;  // Si hubo algún error, retornamos false
    }

    @Override
    public Usuario obtenerPorId(int idUsuario) {
        Usuario usuario = null;
        try {
            // Obtenemos la conexión de la base de datos
            Connection con = ConexionBD.getConexion();

            // Consultamos al usuario por su ID
            String sql = "SELECT * FROM Usuario WHERE idUsuario = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, idUsuario);
            ResultSet rs = ps.executeQuery();

            // Si se encuentra el usuario, lo mapeamos a un objeto Usuario
            if (rs.next()) {
                usuario = new Usuario();
                usuario.setIdUsuario(rs.getInt("idUsuario"));
                usuario.setNombre(rs.getString("nombre"));
                usuario.setApellido(rs.getString("apellido"));
                usuario.setDni(rs.getString("dni"));
                usuario.setCorreo(rs.getString("correo"));
                usuario.setContrasena(rs.getString("contrasena"));
            }
            con.close();  // Cerramos la conexión
        } catch (Exception e) {
            e.printStackTrace();  // Mostramos la traza del error si ocurre uno
        }
        return usuario;  // Retornamos el usuario encontrado o null si no se encuentra
    }

    @Override
    public boolean actualizarDatos(Usuario usuario) {
        try {
            // Obtenemos la conexión de la base de datos
            Connection con = ConexionBD.getConexion();

            // Actualizamos los datos del usuario en la base de datos
            String sql = "UPDATE Usuario SET nombre = ?, apellido = ?, dni = ?, correo = ?, contrasena = ? WHERE idUsuario = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, usuario.getNombre());
            ps.setString(2, usuario.getApellido());
            ps.setString(3, usuario.getDni());
            ps.setString(4, usuario.getCorreo());

            // Encriptamos la nueva contraseña antes de actualizarla
            ps.setString(5, passwordEncoder.encode(usuario.getContrasena())); // ENCRIPTAMOS AL ACTUALIZAR
            ps.setInt(6, usuario.getIdUsuario());

            // Ejecutamos la consulta para actualizar los datos
            ps.executeUpdate();
            con.close();  // Cerramos la conexión

            return true;  // Si la actualización fue exitosa, retornamos true
        } catch (Exception e) {
            e.printStackTrace();  // Mostramos la traza del error si ocurre uno
        }
        return false;  // Si hubo un error, retornamos false
    }
}
