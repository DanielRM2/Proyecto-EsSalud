package com.ed.ecommerce.mvcDemo.Repository;

import com.ed.ecommerce.mvcDemo.Model.Cita;
import com.ed.ecommerce.mvcDemo.Pattern.ConexionBD;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner; // <-- ¡IMPORTACIÓN AÑADIDA/VERIFICADA!

@Repository
public class ICitaRepositoryImpl implements ICitaRepository {

    @Override
    public boolean agendarCita(Cita cita) {
        String sql = "INSERT INTO Cita (idUsuario, idMedico, idCentroMedico, idHorario, estado, fechaCita) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, cita.getIdUsuario());
            ps.setInt(2, cita.getIdMedico());
            ps.setInt(3, cita.getIdCentroMedico());
            ps.setInt(4, cita.getIdHorario());
            ps.setString(5, cita.getEstado());
            ps.setTimestamp(6, Timestamp.valueOf(cita.getFechaCita()));

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
            e.printStackTrace(); // Para depuración
        }
        return false;
    }

    @Override
    public List<Cita> listarCitasPorUsuario(int idUsuario) {
        List<Cita> citas = new ArrayList<>();
        String sql = "SELECT c.idCita, c.idUsuario, c.idMedico, c.idCentroMedico, c.idHorario, c.estado, " +
                "h.fecha, h.hora, " +
                "CONCAT(m.nombre, ' ', m.apellido) AS nombreCompletoMedico, " +
                "e.nombre AS nombreEspecialidad, cm.nombre AS nombreCentroMedico " +
                "FROM Cita c " +
                "JOIN HorarioDisponible h ON c.idHorario = h.idHorario " +
                "JOIN Medico m ON c.idMedico = m.idMedico " +
                "JOIN Especialidad e ON m.idEspecialidad = e.idEspecialidad " +
                "JOIN CentroMedico cm ON c.idCentroMedico = cm.idCentroMedico " +
                "WHERE c.idUsuario = ? " +
                "ORDER BY h.fecha DESC, h.hora DESC";

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
                    cita.setIdHorario(rs.getInt("idHorario"));
                    cita.setEstado(rs.getString("estado"));

                    LocalDateTime fechaCita = rs.getDate("fecha").toLocalDate()
                            .atTime(rs.getTime("hora").toLocalTime());
                    cita.setFechaCita(fechaCita);

                    // Nuevos campos (asegúrate que tu clase Cita tenga estos setters)
                    cita.setNombreMedico(rs.getString("nombreCompletoMedico"));
                    cita.setNombreEspecialidad(rs.getString("nombreEspecialidad"));
                    cita.setNombreCentro(rs.getString("nombreCentroMedico"));

                    citas.add(cita);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al listar citas: " + e.getMessage());
            e.printStackTrace(); // Para depuración
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
            e.printStackTrace(); // Para depuración
        }
        return false;
    }

    @Override
    public boolean reprogramarCita(int idCita, int nuevoIdHorario) {
        String sql = "UPDATE Cita SET idHorario = ?, estado = 'Reprogramado' WHERE idCita = ?";

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, nuevoIdHorario);
            ps.setInt(2, idCita);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al reprogramar cita: " + e.getMessage());
            e.printStackTrace(); // Para depuración
        }
        return false;
    }

    @Override
    public boolean haSidoReprogramada(int idCita) {
        String sql = "SELECT estado FROM Cita WHERE idCita = ?";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idCita);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getString("estado").equals("Reprogramado");
            }
        } catch (SQLException e) {
            System.err.println("Error al verificar estado de cita: " + e.getMessage());
            e.printStackTrace(); // Para depuración
        }
        return false;
    }

    @Override
    public boolean existeCitaParaHorario(int idHorario) {
        String sql = "SELECT COUNT(*) FROM Cita WHERE idHorario = ? AND estado NOT IN ('Cancelado', 'Completado')";

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idHorario);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al verificar existencia de cita para horario: " + e.getMessage());
            e.printStackTrace(); // Para depuración
        }
        return false;
    }

    @Override
    public Cita obtenerPorId(int idCita) {
        String sql = "SELECT * FROM Cita WHERE idCita = ?";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idCita);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Cita cita = new Cita();
                cita.setIdCita(rs.getInt("idCita"));
                cita.setIdUsuario(rs.getInt("idUsuario"));
                cita.setIdMedico(rs.getInt("idMedico"));
                cita.setIdCentroMedico(rs.getInt("idCentroMedico"));
                cita.setIdHorario(rs.getInt("idHorario"));
                cita.setEstado(rs.getString("estado"));
                Timestamp fechaCitaTimestamp = rs.getTimestamp("fechaCita");
                if (fechaCitaTimestamp != null) {
                    cita.setFechaCita(fechaCitaTimestamp.toLocalDateTime());
                }
                return cita;
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener cita por ID: " + e.getMessage()); // Añadido System.err.println
            e.printStackTrace(); // Para depuración
        }
        return null;
    }

    // <-- ¡MÉTODO AÑADIDO/VERIFICADO!
    @Override
    public List<Cita> findCitasActivasPorUsuarioYEstados(int idUsuario, List<String> estados) {
        List<Cita> citas = new ArrayList<>();
        // Construye dinámicamente la cláusula IN para los estados
        StringJoiner sj = new StringJoiner(",", "(", ")");
        for (int i = 0; i < estados.size(); i++) {
            sj.add("?");
        }
        String sql = "SELECT * FROM Cita WHERE idUsuario = ? AND estado IN " + sj.toString();

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);
            for (int i = 0; i < estados.size(); i++) {
                ps.setString(i + 2, estados.get(i)); // +2 porque el primer parámetro es idUsuario
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Cita cita = new Cita();
                    cita.setIdCita(rs.getInt("idCita"));
                    cita.setIdUsuario(rs.getInt("idUsuario"));
                    cita.setIdMedico(rs.getInt("idMedico"));
                    cita.setIdCentroMedico(rs.getInt("idCentroMedico"));
                    cita.setIdHorario(rs.getInt("idHorario"));
                    cita.setEstado(rs.getString("estado"));
                    Timestamp fechaCitaTimestamp = rs.getTimestamp("fechaCita");
                    if (fechaCitaTimestamp != null) {
                        cita.setFechaCita(fechaCitaTimestamp.toLocalDateTime());
                    }
                    citas.add(cita);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar citas activas por usuario y estados: " + e.getMessage());
            e.printStackTrace(); // Para depuración
        }
        return citas;
    }
}
