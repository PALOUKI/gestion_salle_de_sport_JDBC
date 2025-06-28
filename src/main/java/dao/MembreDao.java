package dao;

import entite.Client;
import entite.Membre;
import util.Connexion;

import java.sql.*; // Importation de java.sql.* inclut SQLException, Statement, etc.
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MembreDao {

    public void ajouter(Membre membre) throws SQLException { // Ajout de throws SQLException
        // Ne PAS inclure 'id' dans la clause INSERT car c'est AUTO_INCREMENT
        // La base de données générera automatiquement l'ID.
        String sql = "INSERT INTO membres (date_inscription, client_id) VALUES (?, ?)";
        try (Connection session = Connexion.getSessionV2();
             // Indiquer à la PreparedStatement de retourner les clés générées
             // C'est essentiel pour récupérer l'ID généré par la BDD.
             PreparedStatement statement = session.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setTimestamp(1, Timestamp.valueOf(membre.getDateInscription()));
            // Ceci est crucial : assurez-vous que l'objet Client a bien son ID de BDD
            // après son insertion via ClientService.ajouter().
            statement.setInt(2, membre.getClient().getId());

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                // Récupérer l'ID auto-généré par la base de données
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int id = generatedKeys.getInt(1); // Récupère le premier ID généré
                        membre.setId(id); // Met à jour l'objet Membre avec l'ID réel de la base de données
                        System.out.println("Membre DAO: " + (membre.getClient() != null ? membre.getClient().getNom() : "N/A") + " ajouté avec succès avec ID: " + membre.getId() + ".");
                    } else {
                        System.out.println("Membre DAO: " + (membre.getClient() != null ? membre.getClient().getNom() : "N/A") + " ajouté, mais aucun ID généré n'a été trouvé.");
                    }
                }
            } else {
                System.out.println("Membre DAO: Aucune ligne ajoutée pour le membre: " + (membre.getClient() != null ? membre.getClient().getNom() : "N/A") + ".");
            }

        } catch (SQLException e) {
            System.err.println("Membre DAO: Erreur lors de l'ajout du membre : " + e.getMessage());
            e.printStackTrace(); // Affichez la stack trace pour un diagnostic complet
            throw e; // Relancer l'exception
        }
    }

    public void modifier(Membre membre) throws SQLException { // Ajout de throws SQLException
        String sql = "UPDATE membres SET date_inscription = ?, client_id = ? WHERE id = ?";
        try (Connection session = Connexion.getSessionV2();
             PreparedStatement statement = session.prepareStatement(sql)) {

            statement.setTimestamp(1, Timestamp.valueOf(membre.getDateInscription()));
            statement.setInt(2, membre.getClient().getId());
            statement.setInt(3, membre.getId());

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Membre DAO: " + (membre.getClient() != null ? membre.getClient().getNom() : "N/A") + " modifié avec succès (ID: " + membre.getId() + ").");
            } else {
                System.out.println("Membre DAO: Aucune ligne affectée lors de la modification du membre (ID: " + membre.getId() + ").");
            }

        } catch (SQLException e) {
            System.err.println("Membre DAO: Erreur lors de la modification du membre (ID: " + membre.getId() + ") : " + e.getMessage());
            e.printStackTrace();
            throw e; // Relancer l'exception
        }
    }

    public Membre trouver(int id) throws SQLException { // Ajout de throws SQLException
        String sql = "SELECT * FROM membres WHERE id = ?";
        Membre membre = null;

        try (Connection session = Connexion.getSessionV2();
             PreparedStatement statement = session.prepareStatement(sql)) {

            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    membre = new Membre();
                    membre.setId(resultSet.getInt("id"));
                    // Vérifiez si le timestamp est null avant de le convertir
                    Timestamp timestampInscription = resultSet.getTimestamp("date_inscription");
                    membre.setDateInscription(timestampInscription != null ? timestampInscription.toLocalDateTime() : null);

                    Client client = new Client();
                    client.setId(resultSet.getInt("client_id"));
                    // Optionnel: charger les détails complets du client ici via ClientDao si nécessaire.
                    membre.setClient(client);
                }
            }
            if (membre != null) {
                System.out.println("Membre DAO: Membre trouvé avec succès (ID: " + membre.getId() + ").");
            } else {
                System.out.println("Membre DAO: Aucun membre trouvé pour l'ID : " + id + ".");
            }
        } catch (SQLException e) {
            System.err.println("Membre DAO: Erreur lors de la recherche du membre (ID: " + id + ") : " + e.getMessage());
            e.printStackTrace();
            throw e; // Relancer l'exception
        }

        return membre;
    }

    public void supprimer(int id) throws SQLException { // Modification pour prendre un int id et ajout de throws SQLException
        String sql = "DELETE FROM membres WHERE id = ?"; // Correction du nom de la table de 'membre' à 'membres'
        try (Connection session = Connexion.getSessionV2();
             PreparedStatement statement = session.prepareStatement(sql)) {

            statement.setInt(1, id);
            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Membre DAO: Membre avec ID " + id + " supprimé avec succès.");
            } else {
                System.out.println("Membre DAO: Aucun membre trouvé pour la suppression avec ID: " + id + ".");
            }

        } catch (SQLException e) {
            System.err.println("Membre DAO: Erreur lors de la suppression du membre (ID: " + id + ") : " + e.getMessage());
            e.printStackTrace();
            throw e; // Relancer l'exception
        }
    }

    public List<Membre> listerTous() throws SQLException { // Ajout de throws SQLException
        String sql = "SELECT * FROM membres";
        List<Membre> membres = new ArrayList<>();

        try (Connection session = Connexion.getSessionV2();
             PreparedStatement statement = session.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Membre membre = new Membre();
                membre.setId(resultSet.getInt("id"));
                // Vérifiez si le timestamp est null avant de le convertir
                Timestamp timestampInscription = resultSet.getTimestamp("date_inscription");
                membre.setDateInscription(timestampInscription != null ? timestampInscription.toLocalDateTime() : null);

                Client client = new Client();
                client.setId(resultSet.getInt("client_id"));
                // Optionnel: charger les détails complets du client ici via ClientDao si nécessaire.
                membre.setClient(client);

                membres.add(membre);
            }
            if (membres.isEmpty()) {
                System.out.println("Membre DAO: La liste des membres est vide.");
            }
        } catch (SQLException e) {
            System.err.println("Membre DAO: Erreur lors de la récupération des membres : " + e.getMessage());
            e.printStackTrace();
            throw e; // Relancer l'exception
        }

        return membres;
    }

    /**
     * Récupère le nombre total de membres dans la base de données.
     * @return Le nombre de membres.
     * @throws SQLException Si une erreur SQL se produit lors de la récupération du nombre.
     */
    public int countMembers() throws SQLException {
        String sql = "SELECT COUNT(*) FROM membres";
        int count = 0;
        try (Connection session = Connexion.getSessionV2();
             PreparedStatement statement = session.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            if (resultSet.next()) {
                count = resultSet.getInt(1); // Récupère le premier (et unique) résultat de la fonction COUNT
            }
            System.out.println("Membre DAO: Nombre total de membres : " + count);
        } catch (SQLException e) {
            System.err.println("Membre DAO: Erreur lors de la récupération du nombre de membres : " + e.getMessage());
            e.printStackTrace();
            throw e; // Relancer l'exception
        }
        return count;
    }
}
