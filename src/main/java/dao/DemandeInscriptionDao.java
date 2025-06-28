package dao;

import entite.Client;
import entite.DemandeInscription;
import util.Connexion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException; // Assurez-vous que cette importation est présente
import java.sql.Statement; // Pour Statement.RETURN_GENERATED_KEYS
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class DemandeInscriptionDao {

    public void ajouter(DemandeInscription demandeInscription) throws SQLException { // Ajout de throws SQLException
        // SQL query: DO NOT include 'id' in the column list for AUTO_INCREMENT
        String sql = "INSERT INTO demande_inscriptions (date_de_demande, date_de_traitement, client_id) VALUES (?, ?, ?)";

        try (Connection session = Connexion.getSessionV2();
             // Use Statement.RETURN_GENERATED_KEYS to get the auto-generated ID
             PreparedStatement statement = session.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setTimestamp(1, Timestamp.valueOf(demandeInscription.getDateDeDemande()));
            statement.setTimestamp(2, demandeInscription.getDateDeTraitement() != null
                    ? Timestamp.valueOf(demandeInscription.getDateDeTraitement()) : null);
            // This is crucial: Ensure demandeInscription.getClient().getId() holds the actual database ID
            // This means clientService.ajouter() MUST update the Client object's ID after insertion.
            statement.setInt(3, demandeInscription.getClient().getId());

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                // Retrieve the auto-generated ID
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int id = generatedKeys.getInt(1); // Get the first (and only) generated key
                        demandeInscription.setId(id); // Set the generated ID back to your object
                        System.out.println("Demande Inscription DAO: " + demandeInscription.getId() + " ajoutée avec succès.");
                    } else {
                        System.out.println("Demande Inscription DAO: ajoutée, mais aucun ID généré n'a été trouvé.");
                    }
                }
            } else {
                System.out.println("Demande Inscription DAO: Aucune ligne ajoutée pour la demande d'inscription.");
            }

        } catch (SQLException e) {
            System.err.println("Demande Inscription DAO: Erreur lors de l'ajout de la demande d'inscription : " + e.getMessage());
            e.printStackTrace(); // Print stack trace for detailed error info
            throw e; // Relancer l'exception
        }
    }

    public void modifier(DemandeInscription demandeInscription) throws SQLException { // Ajout de throws SQLException
        String sql = "UPDATE demande_inscriptions SET date_de_demande = ?, date_de_traitement = ?, client_id = ? WHERE id = ?";

        try (Connection session = Connexion.getSessionV2();
             PreparedStatement statement = session.prepareStatement(sql)) {

            statement.setTimestamp(1, Timestamp.valueOf(demandeInscription.getDateDeDemande()));
            statement.setTimestamp(2, demandeInscription.getDateDeTraitement() != null
                    ? Timestamp.valueOf(demandeInscription.getDateDeTraitement()) : null);
            statement.setInt(3, demandeInscription.getClient().getId());
            statement.setInt(4, demandeInscription.getId());

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Demande Inscription DAO: " + demandeInscription.getId() + " modifiée avec succès.");
            } else {
                System.out.println("Demande Inscription DAO: Aucune ligne affectée lors de la modification de la demande d'inscription.");
            }

        } catch (SQLException e) {
            System.err.println("Demande Inscription DAO: Erreur lors de la modification de la demande d'inscription : " + e.getMessage());
            e.printStackTrace();
            throw e; // Relancer l'exception
        }
    }

    public void supprimer(int id) throws SQLException { // Modification pour prendre un int id et ajout de throws SQLException
        String sql = "DELETE FROM demande_inscriptions WHERE id = ?";

        try (Connection session = Connexion.getSessionV2();
             PreparedStatement statement = session.prepareStatement(sql)) {

            statement.setInt(1, id);
            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Demande Inscription DAO: " + id + " supprimée avec succès.");
            } else {
                System.out.println("Demande Inscription DAO: Aucune demande d'inscription trouvée pour la suppression avec ID: " + id + ".");
            }

        } catch (SQLException e) {
            System.err.println("Demande Inscription DAO: Erreur lors de la suppression de la demande d'inscription : " + e.getMessage());
            e.printStackTrace();
            throw e; // Relancer l'exception
        }
    }

    public DemandeInscription trouver(int id) throws SQLException { // Ajout de throws SQLException
        String sql = "SELECT * FROM demande_inscriptions WHERE id = ?";
        DemandeInscription demandeInscription = null;

        try (Connection session = Connexion.getSessionV2();
             PreparedStatement statement = session.prepareStatement(sql)) {

            statement.setInt(1, id);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    demandeInscription = new DemandeInscription();
                    demandeInscription.setId(rs.getInt("id"));

                    Timestamp timestampDemande = rs.getTimestamp("date_de_demande");
                    demandeInscription.setDateDeDemande(timestampDemande != null ? timestampDemande.toLocalDateTime() : null);

                    Timestamp timestampTraitement = rs.getTimestamp("date_de_traitement");
                    demandeInscription.setDateDeTraitement(timestampTraitement != null ? timestampTraitement.toLocalDateTime() : null);

                    Client client = new Client();
                    client.setId(rs.getInt("client_id"));
                    // Vous pourriez vouloir récupérer les détails complets du client ici
                    // en utilisant ClientDao, mais pour l'instant, juste l'ID suffit.
                    demandeInscription.setClient(client);
                }
            }
            // Retiré le message "Aucune demande trouvée" car le service ou contrôleur peut le gérer.
        } catch (SQLException e) {
            System.err.println("Demande Inscription DAO: Erreur lors de la recherche de la demande d'inscription : " + e.getMessage());
            e.printStackTrace();
            throw e; // Relancer l'exception
        }

        return demandeInscription;
    }

    public List<DemandeInscription> listerTous() throws SQLException { // Ajout de throws SQLException
        String sql = "SELECT * FROM demande_inscriptions";
        List<DemandeInscription> demandeInscriptions = new ArrayList<>();

        try (Connection session = Connexion.getSessionV2();
             PreparedStatement statement = session.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                DemandeInscription demandeInscription = new DemandeInscription();
                demandeInscription.setId(resultSet.getInt("id"));

                Timestamp timestampDemande = resultSet.getTimestamp("date_de_demande");
                demandeInscription.setDateDeDemande(timestampDemande != null ? timestampDemande.toLocalDateTime() : null);

                Timestamp timestampTraitement = resultSet.getTimestamp("date_de_traitement");
                demandeInscription.setDateDeTraitement(timestampTraitement != null ? timestampTraitement.toLocalDateTime() : null);

                Client client = new Client();
                client.setId(resultSet.getInt("client_id"));
                // Vous pourriez vouloir récupérer les détails complets du client ici
                // en utilisant ClientDao, mais pour l'instant, juste l'ID suffit.
                demandeInscription.setClient(client);

                demandeInscriptions.add(demandeInscription);
            }
            // Retiré le message "La liste est vide" car le service ou contrôleur peut le gérer.
        } catch (SQLException e) {
            System.err.println("Demande Inscription DAO: Erreur lors de la récupération des demandes d'inscription : " + e.getMessage());
            e.printStackTrace();
            throw e; // Relancer l'exception
        }

        return demandeInscriptions;
    }
}