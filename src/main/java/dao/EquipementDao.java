package dao;

import entite.Equipement;
import entite.Salle; // Importez la classe Salle
import util.Connexion;

import java.sql.*; // Importation de java.sql.* inclut Connection, PreparedStatement, ResultSet, SQLException, Statement
import java.util.ArrayList;
import java.util.List;

public class EquipementDao {

    public void ajouter(Equipement equipement) throws SQLException { // Ajout de throws SQLException
        // Ne PAS inclure 'id' dans la clause INSERT car c'est AUTO_INCREMENT
        String sql = "INSERT INTO equipements (libelle, description, salle_id) VALUES (?, ?, ?)";

        try (Connection session = Connexion.getSessionV2();
             // Indiquer à la PreparedStatement de retourner les clés générées
             PreparedStatement statement = session.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) { // Ajout de Statement.RETURN_GENERATED_KEYS

            statement.setString(1, equipement.getLibelle());
            statement.setString(2, equipement.getDescription());
            // Ceci est crucial : assurez-vous que l'objet Salle a bien son ID de BDD
            // après son insertion via SalleService.ajouter().
            statement.setInt(3, equipement.getSalle().getId());

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                // Récupérer l'ID auto-généré par la base de données
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int id = generatedKeys.getInt(1); // Récupère le premier ID généré
                        equipement.setId(id); // Met à jour l'objet Equipement avec l'ID réel de la base de données
                        System.out.println("Equipement DAO: " + equipement.getLibelle() + " ajouté avec succès avec ID: " + equipement.getId() + ".");
                    } else {
                        System.out.println("Equipement DAO: " + equipement.getLibelle() + " ajouté, mais aucun ID généré n'a été trouvé.");
                    }
                }
            } else {
                System.out.println("Equipement DAO: Aucune ligne ajoutée pour l'équipement: " + equipement.getLibelle() + ".");
            }

        } catch (SQLException e) {
            System.err.println("Equipement DAO: Erreur lors de l'ajout de l'équipement : " + e.getMessage());
            e.printStackTrace();
            throw e; // Relancer l'exception
        }
    }

    public void modifier(Equipement equipement) throws SQLException { // Ajout de throws SQLException
        String sql = "UPDATE equipements SET libelle = ?, description = ?, salle_id = ? WHERE id = ?";

        try (Connection session = Connexion.getSessionV2();
             PreparedStatement statement = session.prepareStatement(sql)) {

            statement.setString(1, equipement.getLibelle());
            statement.setString(2, equipement.getDescription());
            statement.setInt(3, equipement.getSalle().getId());
            statement.setInt(4, equipement.getId());

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Equipement DAO: " + equipement.getLibelle() + " modifié avec succès (ID: " + equipement.getId() + ").");
            } else {
                System.out.println("Equipement DAO: Aucune ligne affectée lors de la modification de l'équipement (ID: " + equipement.getId() + ").");
            }

        } catch (SQLException e) {
            System.err.println("Equipement DAO: Erreur lors de la modification de l'équipement : " + e.getMessage());
            e.printStackTrace();
            throw e; // Relancer l'exception
        }
    }

    public void supprimer(int id) throws SQLException { // Modification pour prendre un int id et ajout de throws SQLException
        String sql = "DELETE FROM equipements WHERE id = ?";

        try (Connection session = Connexion.getSessionV2();
             PreparedStatement statement = session.prepareStatement(sql)) {

            statement.setInt(1, id);
            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Equipement DAO: Equipement avec ID " + id + " supprimé avec succès.");
            } else {
                System.out.println("Equipement DAO: Aucun équipement trouvé pour la suppression avec ID: " + id + ".");
            }

        } catch (SQLException e) {
            System.err.println("Equipement DAO: Erreur lors de la suppression de l'équipement (ID: " + id + ") : " + e.getMessage());
            e.printStackTrace();
            throw e; // Relancer l'exception
        }
    }

    public Equipement trouver(int id) throws SQLException { // Ajout de throws SQLException
        String sql = "SELECT * FROM equipements WHERE id = ?";
        Equipement equipement = null;

        try (Connection session = Connexion.getSessionV2();
             PreparedStatement statement = session.prepareStatement(sql)) {

            statement.setInt(1, id);
            try (ResultSet rs = statement.executeQuery()) { // Utilisation de try-with-resources pour ResultSet
                if (rs.next()) {
                    equipement = new Equipement();
                    equipement.setId(rs.getInt("id"));
                    equipement.setLibelle(rs.getString("libelle"));
                    equipement.setDescription(rs.getString("description"));

                    Salle salle = new Salle();
                    salle.setId(rs.getInt("salle_id"));
                    // Optionnel: charger les détails complets de la salle ici via SalleDao si nécessaire.
                    equipement.setSalle(salle);
                }
            }
            if (equipement != null) {
                System.out.println("Equipement DAO: Equipement trouvé avec succès (ID: " + equipement.getId() + ", Libellé: " + equipement.getLibelle() + ").");
            } else {
                System.out.println("Equipement DAO: Aucun équipement trouvé pour l'ID : " + id + ".");
            }
        } catch (SQLException e) {
            System.err.println("Equipement DAO: Erreur lors de la recherche de l'équipement (ID: " + id + ") : " + e.getMessage());
            e.printStackTrace();
            throw e; // Relancer l'exception
        }

        return equipement;
    }

    public List<Equipement> listerTous() throws SQLException { // Ajout de throws SQLException
        String sql = "SELECT * FROM equipements";
        List<Equipement> equipements = new ArrayList<>();

        try (Connection session = Connexion.getSessionV2();
             PreparedStatement statement = session.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Equipement equipement = new Equipement();
                equipement.setId(resultSet.getInt("id"));
                equipement.setLibelle(resultSet.getString("libelle"));
                equipement.setDescription(resultSet.getString("description"));

                Salle salle = new Salle();
                salle.setId(resultSet.getInt("salle_id"));
                // Optionnel: charger les détails complets de la salle ici via SalleDao si nécessaire.
                equipement.setSalle(salle);

                equipements.add(equipement);
            }
            if (equipements.isEmpty()) {
                System.out.println("Equipement DAO: La liste des équipements est vide.");
            }
        } catch (SQLException e) {
            System.err.println("Equipement DAO: Erreur lors de la récupération des équipements : " + e.getMessage());
            e.printStackTrace();
            throw e; // Relancer l'exception
        }

        return equipements;
    }
}