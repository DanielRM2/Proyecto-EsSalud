package com.ed.ecommerce.mvcDemo.Repository;


import com.ed.ecommerce.mvcDemo.Model.Especialidad;
import com.ed.ecommerce.mvcDemo.Pattern.ConexionBD;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class IEspecialidadRepositoryImpl implements IEspecialidadRepository {

    @Override
    public List<Especialidad> listarTodas() {
        List<Especialidad> especialidades = new ArrayList<>();
        String sql = "SELECT * FROM Especialidad ORDER BY nombre";

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Especialidad especialidad = new Especialidad(
                        rs.getInt("idEspecialidad"),
                        rs.getString("nombre")
                );
                especialidades.add(especialidad);
            }

        } catch (SQLException e) {
            System.err.println("Error al listar especialidades: " + e.getMessage());
            e.printStackTrace();
        }

        return especialidades;
    }

    @Override
    public Especialidad obtenerPorId(int idEspecialidad) {
        String sql = "SELECT * FROM Especialidad WHERE idEspecialidad = ?";

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idEspecialidad);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Especialidad(
                            rs.getInt("idEspecialidad"),
                            rs.getString("nombre")
                    );
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener especialidad: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }
}
