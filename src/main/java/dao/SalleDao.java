package dao;

import entite.Salle;
import util.Connexion;

import java.sql.*; // Inclut Connection, PreparedStatement, ResultSet, SQLException, Statement
import java.util.ArrayList;
import java.util.List;

public class SalleDao {

    public void ajouter(Salle salle) throws SQLException { // Ajout de throws SQLException
        // Ne PAS inclure 'id' dans la clause INSERT car c'est AUTO_INCREMENT
        String sql = "INSERT INTO salles (libelle, description) VALUES (?, ?)";
        try (Connection session = Connexion.getSessionV2();
             // Indiquer à la PreparedStatement de retourner les clés générées
             PreparedStatement preparedStatement = session.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, salle.getLibelle());
            preparedStatement.setString(2, salle.getDescription());

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                // Récupérer l'ID auto-généré
                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int id = generatedKeys.getInt(1); // Récupère le premier ID généré
                        salle.setId(id); // Met à jour l'objet Salle avec l'ID réel de la base de données
                        System.out.println("Salle DAO: " + salle.getLibelle() + " ajoutée avec succès avec ID: " + salle.getId() + ".");
                    } else {
                        System.out.println("Salle DAO: " + salle.getLibelle() + " ajoutée, mais aucun ID généré n'a été trouvé.");
                    }
                }
            } else {
                System.out.println("Salle DAO: Aucune ligne ajoutée pour la salle: " + salle.getLibelle() + ".");
            }

        } catch (SQLException e) {
            System.err.println("Salle DAO: Erreur lors de l'ajout de la salle : " + e.getMessage());
            e.printStackTrace(); // Affichez la stack trace pour un diagnostic complet
            throw e; // Relancer l'exception
        }
    }

    public void modifier(Salle salle) throws SQLException { // Ajout de throws SQLException
        String sql = "UPDATE salles SET libelle = ?, description = ? WHERE id = ?";
        try (Connection session = Connexion.getSessionV2();
             PreparedStatement preparedStatement = session.prepareStatement(sql)) {

            preparedStatement.setString(1, salle.getLibelle());
            preparedStatement.setString(2, salle.getDescription());
            preparedStatement.setInt(3, salle.getId());

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Salle DAO: " + salle.getLibelle() + " modifiée avec succès (ID: " + salle.getId() + ").");
            } else {
                System.out.println("Salle DAO: Aucune ligne affectée lors de la modification de la salle (ID: " + salle.getId() + ").");
            }

        } catch (SQLException e) {
            System.err.println("Salle DAO: Erreur lors de la modification de la salle : " + e.getMessage());
            e.printStackTrace();
            throw e; // Relancer l'exception
        }
    }

    public void supprimer(int id) throws SQLException { // Modification pour prendre un int id et ajout de throws SQLException
        String sql = "DELETE FROM salles WHERE id = ?"; // Modification de la clause WHERE pour utiliser l'ID
        try (Connection session = Connexion.getSessionV2();
             PreparedStatement preparedStatement = session.prepareStatement(sql)) {

            preparedStatement.setInt(1, id);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Salle DAO: Salle avec ID " + id + " supprimée avec succès.");
            } else {
                System.out.println("Salle DAO: Aucune salle trouvée pour la suppression avec ID: " + id + ".");
            }

        } catch (SQLException e) {
            System.err.println("Salle DAO: Erreur lors de la suppression de la salle (ID: " + id + ") : " + e.getMessage());
            e.printStackTrace();
            throw e; // Relancer l'exception
        }
    }

    public Salle trouver(int id) throws SQLException { // Ajout de throws SQLException
        String sql = "SELECT * FROM salles WHERE id = ?"; // Correction de la clause WHERE pour utiliser l'ID
        Salle salle = null;

        try (Connection session = Connexion.getSessionV2();
             PreparedStatement preparedStatement = session.prepareStatement(sql)) {

            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) { // Utilisation de try-with-resources pour ResultSet
                if (resultSet.next()) {
                    salle = new Salle();
                    salle.setId(resultSet.getInt("id"));
                    salle.setLibelle(resultSet.getString("libelle"));
                    salle.setDescription(resultSet.getString("description"));
                }
            }
            if (salle != null) {
                System.out.println("Salle DAO: Salle trouvée avec succès (ID: " + salle.getId() + ", Libellé: " + salle.getLibelle() + ").");
            } else {
                System.out.println("Salle DAO: Aucune salle trouvée pour l'ID : " + id + ".");
            }
        } catch (SQLException e) {
            System.err.println("Salle DAO: Erreur lors de la recherche de la salle (ID: " + id + ") : " + e.getMessage());
            e.printStackTrace();
            throw e; // Relancer l'exception
        }

        return salle;
    }

    public List<Salle> listerTous() throws SQLException { // Ajout de throws SQLException
        String sql = "SELECT * FROM salles";
        List<Salle> salles = new ArrayList<>();

        try (Connection session = Connexion.getSessionV2();
             PreparedStatement preparedStatement = session.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                Salle salle = new Salle();
                salle.setId(resultSet.getInt("id"));
                salle.setLibelle(resultSet.getString("libelle"));
                salle.setDescription(resultSet.getString("description"));
                salles.add(salle);
            }
            if (salles.isEmpty()) {
                System.out.println("Salle DAO: La liste des salles est vide.");
            }
        } catch (SQLException e) {
            System.err.println("Salle DAO: Erreur lors de la récupération des salles : " + e.getMessage());
            e.printStackTrace();
            throw e; // Relancer l'exception
        }

        return salles;
    }
}
