package dao;

import entite.Client;
import util.Connexion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException; // Assurez-vous que cette importation est présente
import java.sql.Statement;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ClientDao {

    public void ajouter(Client client) throws SQLException { // Ajoutez throws SQLException
        String sql = "INSERT INTO clients (nom, prenom, date_naissance, email) VALUES (?, ?, ?, ?)";
        try (Connection session = Connexion.getSessionV2();
             PreparedStatement preparedStatement = session.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, client.getNom());
            preparedStatement.setString(2, client.getPrenom());
            preparedStatement.setDate(3, client.getDateNaissance() != null ? Date.valueOf(client.getDateNaissance().toLocalDate()) : null);
            preparedStatement.setString(4, client.getEmail());

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int id = generatedKeys.getInt(1);
                        client.setId(id);
                        System.out.println("Client DAO: " + client.getNom() + " ajouté avec succès avec ID: " + client.getId());
                    }
                }
            } else {
                System.out.println("Client DAO: Aucune ligne affectée lors de l'ajout du client.");
            }

        } catch (SQLException e) {
            System.err.println("Client DAO: Erreur lors de l'ajout du client : " + e.getMessage());
            e.printStackTrace();
            throw e; // Relancer l'exception
        }
    }

    public void modifier(Client client) throws SQLException { // Ajoutez throws SQLException
        String sql = "UPDATE clients SET nom = ?, prenom = ?, date_naissance = ?, email = ? WHERE id = ?";
        try (Connection session = Connexion.getSessionV2();
             PreparedStatement preparedStatement = session.prepareStatement(sql)) {

            preparedStatement.setString(1, client.getNom());
            preparedStatement.setString(2, client.getPrenom());
            preparedStatement.setDate(3, client.getDateNaissance() != null ? Date.valueOf(client.getDateNaissance().toLocalDate()) : null);
            preparedStatement.setString(4, client.getEmail());
            preparedStatement.setInt(5, client.getId());

            preparedStatement.executeUpdate();

            System.out.println("Client DAO: " + client.getNom() + " modifié avec succès.");

        } catch (SQLException e) {
            System.err.println("Client DAO: Erreur lors de la modification du client : " + e.getMessage());
            e.printStackTrace();
            throw e; // Relancer l'exception
        }
    }

    public void supprimer(int id) throws SQLException { // Ajoutez throws SQLException
        String sql = "DELETE FROM clients WHERE id = ?";
        try (Connection session = Connexion.getSessionV2();
             PreparedStatement preparedStatement = session.prepareStatement(sql)) {

            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
            System.out.println("Client DAO: Client avec ID " + id + " supprimé avec succès.");

        } catch (SQLException e) {
            System.err.println("Client DAO: Erreur lors de la suppression du client : " + e.getMessage());
            e.printStackTrace();
            throw e; // Relancer l'exception
        }
    }

    public Client trouver(int id) throws SQLException { // Ajoutez throws SQLException
        String sql = "SELECT * FROM clients WHERE id = ?";
        Client client = null;

        try (Connection session = Connexion.getSessionV2();
             PreparedStatement preparedStatement = session.prepareStatement(sql)) {

            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    client = new Client();
                    client.setId(resultSet.getInt("id"));
                    client.setNom(resultSet.getString("nom"));
                    client.setPrenom(resultSet.getString("prenom"));
                    Date sqlDate = resultSet.getDate("date_naissance");
                    if (sqlDate != null) {
                        client.setDateNaissance(sqlDate.toLocalDate().atStartOfDay());
                    } else {
                        client.setDateNaissance(null);
                    }
                    client.setEmail(resultSet.getString("email"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Client DAO: Erreur lors de la recherche du client : " + e.getMessage());
            e.printStackTrace();
            throw e; // Relancer l'exception
        }
        return client;
    }

    public List<Client> listerTous() throws SQLException { // Ajoutez throws SQLException
        String sql = "SELECT * FROM clients";
        List<Client> clients = new ArrayList<>();

        try (Connection session = Connexion.getSessionV2();
             PreparedStatement preparedStatement = session.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                Client client = new Client();
                client.setId(resultSet.getInt("id"));
                client.setNom(resultSet.getString("nom"));
                client.setPrenom(resultSet.getString("prenom"));
                Date sqlDate = resultSet.getDate("date_naissance");
                if (sqlDate != null) {
                    client.setDateNaissance(sqlDate.toLocalDate().atStartOfDay());
                } else {
                    client.setDateNaissance(null);
                }
                client.setEmail(resultSet.getString("email"));
                clients.add(client);
            }
        } catch (SQLException e) {
            System.err.println("Client DAO: Erreur lors de la récupération des clients : " + e.getMessage());
            e.printStackTrace();
            throw e; // Relancer l'exception
        }
        return clients;
    }
}
