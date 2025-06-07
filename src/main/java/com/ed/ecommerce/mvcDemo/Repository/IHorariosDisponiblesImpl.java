package com.ed.ecommerce.mvcDemo.Repository;

import com.ed.ecommerce.mvcDemo.Model.HorarioDisponible;
import com.ed.ecommerce.mvcDemo.Pattern.ConexionBD;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class IHorariosDisponiblesImpl implements IHorariosDisponibles {

    @Override
    public List<HorarioDisponible> listarPorCentroMedicoYEspecialidad(int idCentroMedico, int idEspecialidad) {
        List<HorarioDisponible> lista = new ArrayList<>();

        // La consulta ya unía HorarioDisponible con Medico, ahora nos aseguramos de seleccionar m.idCentroMedico
        String sql = "SELECT h.idHorario, h.idMedico, h.fecha, h.hora, h.disponible, " +
                "m.nombre, m.apellido, m.idCentroMedico " + // <-- ¡CLAVE! Seleccionamos idCentroMedico del Medico
                "FROM HorarioDisponible h " +
                "INNER JOIN Medico m ON h.idMedico = m.idMedico " +
                "WHERE m.idCentroMedico = ? AND m.idEspecialidad = ? AND h.disponible = 1 " +
                "ORDER BY h.fecha, h.hora";

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idCentroMedico);
            ps.setInt(2, idEspecialidad);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    HorarioDisponible horario = new HorarioDisponible();
                    horario.setIdHorario(rs.getInt("idHorario"));
                    horario.setIdMedico(rs.getInt("idMedico"));
                    // Asignamos el idCentroMedico que obtuvimos de la tabla Medico
                    horario.setIdCentroMedico(rs.getInt("idCentroMedico")); // <-- ¡CLAVE! Lo asignamos al objeto
                    horario.setFecha(rs.getDate("fecha").toLocalDate());
                    horario.setHora(rs.getTime("hora").toLocalTime());
                    horario.setDisponible(rs.getBoolean("disponible"));

                    horario.setNombreMedico(rs.getString("nombre") + " " + rs.getString("apellido"));

                    lista.add(horario);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al listar horarios disponibles: " + e.getMessage());
            e.printStackTrace();
        }

        return lista;
    }


    @Override
    public HorarioDisponible obtenerPorId(int idHorario) {
        // Para obtener un HorarioDisponible por ID, también necesitamos el idCentroMedico del Médico asociado.
        String sql = "SELECT h.idHorario, h.idMedico, h.fecha, h.hora, h.disponible, " +
                "m.idCentroMedico " + // <-- ¡CLAVE! Seleccionamos idCentroMedico de Medico
                "FROM HorarioDisponible h " +
                "INNER JOIN Medico m ON h.idMedico = m.idMedico " + // <-- Hacemos un JOIN con Medico
                "WHERE h.idHorario = ?";

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idHorario);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Usamos el constructor de 6 argumentos, obteniendo idCentroMedico del Medico
                    return new HorarioDisponible(
                            rs.getInt("idHorario"),
                            rs.getInt("idMedico"),
                            rs.getInt("idCentroMedico"), // <-- ¡CLAVE! Pasamos idCentroMedico del Medico
                            rs.getDate("fecha").toLocalDate(),
                            rs.getTime("hora").toLocalTime(),
                            rs.getBoolean("disponible")
                    );
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener horario disponible: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public boolean actualizarDisponibilidad(int idHorario, boolean disponible) {
        String sql = "UPDATE HorarioDisponible SET disponible = ? WHERE idHorario = ?";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setBoolean(1, disponible);
            ps.setInt(2, idHorario);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar disponibilidad: " + e.getMessage());
            return false;
        }
    }
}