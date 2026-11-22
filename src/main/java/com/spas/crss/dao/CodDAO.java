package com.spas.crss.dao;

import com.spas.crss.config.DatabaseConnection;
import com.spas.crss.model.Cod;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CodDAO {

    // CREATE: Add a new CoD
    public boolean addCod(Cod cod) {
        String sql = "INSERT INTO cod (cod_id, name, email) VALUES (?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, cod.getCodId());
            statement.setString(2, cod.getName());
            statement.setString(3, cod.getEmail());

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new IllegalStateException("Failed to add CoD: " + e.getMessage(), e);
        }
    }

    // READ: Get a CoD by ID
    public Cod getCodById(String codId) {
        String sql = "SELECT * FROM cod WHERE cod_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, codId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return extractCod(resultSet);
                }
            }

        } catch (SQLException e) {
            throw new IllegalStateException("Failed to retrieve CoD: " + e.getMessage(), e);
        }

        return null;
    }

    // READ: Get all CoDs
    public List<Cod> getAllCods() {
        List<Cod> cods = new ArrayList<>();
        String sql = "SELECT * FROM cod ORDER BY name";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                cods.add(extractCod(resultSet));
            }

        } catch (SQLException e) {
            throw new IllegalStateException("Failed to retrieve CoDs: " + e.getMessage(), e);
        }

        return cods;
    }

    // UPDATE: Update CoD details
    public boolean updateCod(Cod cod) {
        String sql = "UPDATE cod SET name = ?, email = ? WHERE cod_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, cod.getName());
            statement.setString(2, cod.getEmail());
            statement.setString(3, cod.getCodId());

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new IllegalStateException("Failed to update CoD: " + e.getMessage(), e);
        }
    }

    // DELETE: Remove a CoD
    public boolean deleteCod(String codId) {
        String sql = "DELETE FROM cod WHERE cod_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, codId);

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new IllegalStateException("Failed to delete CoD: " + e.getMessage(), e);
        }
    }

    // Convert database result into a CoD object
    private Cod extractCod(ResultSet resultSet) throws SQLException {
        return new Cod(
                resultSet.getString("cod_id"),
                resultSet.getString("name"),
                resultSet.getString("email")
        );
    }
}
