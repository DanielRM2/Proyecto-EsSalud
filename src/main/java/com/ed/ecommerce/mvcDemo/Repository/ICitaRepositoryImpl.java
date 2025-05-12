package com.ed.ecommerce.mvcDemo.Repository;


import com.ed.ecommerce.mvcDemo.Model.Cita;
import com.ed.ecommerce.mvcDemo.Pattern.ConexionBD;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ICitaRepositoryImpl implements ICitaRepository {

    @Override
    public boolean agendarCita(Cita cita) {
        String sql = "INSERT INTO Cita (idUsuario, idMedico, idCentroMedico, fechaCita, estado) VALUES (?, ?, ?, ?, ?)";

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, cita.getIdUsuario());
            ps.setInt(2, cita.getIdMedico());
            ps.setInt(3, cita.getIdCentroMedico());
            ps.setTimestamp(4, Timestamp.valueOf(cita.getFechaCita()));
            ps.setString(5, cita.getEstado());

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        cita.setIdCita(rs.getInt(1));
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al agendar cita: " + e.getMessage());
        }
        return false;
    }

    @Override
    public List<Cita> listarCitasPorUsuario(int idUsuario) {
        List<Cita> citas = new ArrayList<>();
        String sql = "SELECT c.*, m.nombre AS nombreMedico, m.apellido, " +
                "cm.nombre AS nombreCentro, e.nombre AS nombreEspecialidad " +
                "FROM Cita c " +
                "JOIN Medico m ON c.idMedico = m.idMedico " +
                "JOIN CentroMedico cm ON c.idCentroMedico = cm.idCentroMedico " +
                "JOIN Especialidad e ON m.idEspecialidad = e.idEspecialidad " +
                "WHERE c.idUsuario = ? ORDER BY c.fechaCita DESC";

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Cita cita = new Cita();
                    cita.setIdCita(rs.getInt("idCita"));
                    cita.setIdUsuario(rs.getInt("idUsuario"));
                    cita.setIdMedico(rs.getInt("idMedico"));
                    cita.setIdCentroMedico(rs.getInt("idCentroMedico"));
                    cita.setFechaCita(rs.getTimestamp("fechaCita").toLocalDateTime());
                    cita.setEstado(rs.getString("estado"));
                    cita.setNombreMedico(rs.getString("nombreMedico") + " " + rs.getString("apellido"));
                    cita.setNombreCentro(rs.getString("nombreCentro"));
                    cita.setNombreEspecialidad(rs.getString("nombreEspecialidad"));

                    citas.add(cita);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al listar citas: " + e.getMessage());
        }
        return citas;
    }

    @Override
    public boolean cancelarCita(int idCita) {
        String sql = "UPDATE Cita SET estado = 'Cancelado' WHERE idCita = ?";

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idCita);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al cancelar cita: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean reprogramarCita(int idCita, LocalDateTime nuevaFecha) {
        String sql = "UPDATE Cita SET fechaCita = ?, estado = 'Reprogramado' WHERE idCita = ?";

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setTimestamp(1, Timestamp.valueOf(nuevaFecha));
            ps.setInt(2, idCita);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al reprogramar cita: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean existeCitaParaMedico(int idMedico, LocalDateTime fecha) {
        String sql = "SELECT COUNT(*) FROM Cita WHERE idMedico = ? AND fechaCita = ? AND estado NOT IN ('Cancelado', 'Completado')";

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idMedico);
            ps.setTimestamp(2, Timestamp.valueOf(fecha));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al verificar cita: " + e.getMessage());
        }
        return false;
    }

}
