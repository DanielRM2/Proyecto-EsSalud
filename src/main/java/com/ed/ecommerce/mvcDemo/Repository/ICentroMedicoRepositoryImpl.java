package com.ed.ecommerce.mvcDemo.Repository;


import com.ed.ecommerce.mvcDemo.Model.CentroMedico;
import com.ed.ecommerce.mvcDemo.Pattern.ConexionBD;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ICentroMedicoRepositoryImpl implements ICentroMedicoRepository{

    @Override
    public List<CentroMedico> listarTodos() {
        List<CentroMedico> centros = new ArrayList<>();
        String sql = "SELECT * FROM CentroMedico ORDER BY nombre";

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                CentroMedico centro = new CentroMedico(
                        rs.getInt("idCentroMedico"),
                        rs.getString("nombre"),
                        rs.getString("direccion")
                );
                centros.add(centro);
            }

        } catch (SQLException e) {
            System.err.println("Error al listar centros médicos: " + e.getMessage());
            e.printStackTrace();
        }

        return centros;
    }

    @Override
    public CentroMedico obtenerPorId(int idCentroMedico) {
        String sql = "SELECT * FROM CentroMedico WHERE idCentroMedico = ?";

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idCentroMedico);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new CentroMedico(
                            rs.getInt("idCentroMedico"),
                            rs.getString("nombre"),
                            rs.getString("direccion")
                    );
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener centro médico: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }
}
