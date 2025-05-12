package com.ed.ecommerce.mvcDemo.Repository;

import com.ed.ecommerce.mvcDemo.Model.Medico;
import com.ed.ecommerce.mvcDemo.Pattern.ConexionBD;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class IMedicoRepositoryImpl implements IMedicoRepository {

    @Override
    public List<Medico> findByEspecialidadAndCentroMedico(int idEspecialidad, int idCentroMedico) {
        List<Medico> medicos = new ArrayList<>();
        String sql = "SELECT m.idMedico, m.nombre, m.apellido " +
                "FROM Medico m " +
                "WHERE m.idEspecialidad = ? AND m.idCentroMedico = ? AND m.estado = 1";

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idEspecialidad);
            ps.setInt(2, idCentroMedico);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Medico medico = new Medico();
                    medico.setIdMedico(rs.getInt("idMedico"));
                    medico.setNombre(rs.getString("nombre"));
                    medico.setApellido(rs.getString("apellido"));
                    medicos.add(medico);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar médicos: " + e.getMessage());
            e.printStackTrace();
        }
        return medicos;
    }

    @Override
    public Medico obtenerPorId(int idMedico) {
        String sql = "SELECT m.idMedico, m.nombre, m.apellido, e.nombre as especialidad, " +
                "cm.nombre as centroMedico, m.idEspecialidad, m.idCentroMedico " +
                "FROM Medico m " +
                "JOIN Especialidad e ON m.idEspecialidad = e.idEspecialidad " +
                "JOIN CentroMedico cm ON m.idCentroMedico = cm.idCentroMedico " +
                "WHERE m.idMedico = ? AND m.estado = 1";

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idMedico);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Medico medico = new Medico();
                    medico.setIdMedico(rs.getInt("idMedico"));
                    medico.setNombre(rs.getString("nombre"));
                    medico.setApellido(rs.getString("apellido"));
                    medico.setEspecialidad(rs.getString("especialidad"));
                    medico.setCentroMedico(rs.getString("centroMedico"));
                    medico.setIdEspecialidad(rs.getInt("idEspecialidad"));
                    medico.setIdCentroMedico(rs.getInt("idCentroMedico"));
                    return medico;
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener médico por ID: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }
}
