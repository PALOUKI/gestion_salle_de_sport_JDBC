package dao;

import entite.Abonnement;
import entite.Membre;
import entite.TypeAbonnement;
import util.Connexion;

import java.sql.*; // Importation de java.sql.* inclut Connection, PreparedStatement, ResultSet, SQLException, Statement
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AbonnementDao {

    public void ajouter(Abonnement abonnement) throws SQLException { // Ajout de throws SQLException
        // La requête INSERT est correcte, car 'id' est AUTO_INCREMENT et est généré par la BDD.
        String sql = "INSERT INTO abonnements(date_debut, date_fin, membre_id, type_abonnement_code) VALUES (?, ?, ?, ?)";

        try (Connection session = Connexion.getSessionV2();
             // Indiquer à la PreparedStatement de retourner les clés générées
             // C'est essentiel pour récupérer l'ID auto-généré par la BDD.
             PreparedStatement statement = session.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setTimestamp(1, Timestamp.valueOf(abonnement.getDateDebut()));
            // Gérer le cas où date_fin peut être null si votre BDD le permet et que l'entité le reflète.
            statement.setTimestamp(2, abonnement.getDateFin() != null ? Timestamp.valueOf(abonnement.getDateFin()) : null);
            // Assurez-vous que l'objet Membre a bien son ID de BDD après son insertion.
            statement.setInt(3, abonnement.getMembre().getId());
            // Assurez-vous que le TypeAbonnement a un code valide (puisque c'est sa clé primaire).
            statement.setString(4, abonnement.getTypeAbonnement().getCode());

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                // Récupérer l'ID auto-généré par la base de données
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int id = generatedKeys.getInt(1); // Récupère le premier ID généré
                        abonnement.setId(id); // Met à jour l'objet Abonnement avec l'ID réel de la base de données
                        System.out.println("Abonnement DAO: " + abonnement.getId() + " ajouté avec succès.");
                    } else {
                        System.out.println("Abonnement DAO: " + abonnement.getId() + " ajouté, mais aucun ID généré n'a été trouvé.");
                    }
                }
            } else {
                System.out.println("Abonnement DAO: Aucune ligne ajoutée pour l'abonnement.");
            }

        } catch (SQLException e) {
            System.err.println("Abonnement DAO: Erreur lors de l'ajout de l'abonnement : " + e.getMessage());
            e.printStackTrace(); // Affichez la stack trace pour un diagnostic complet
            throw e; // Relancer l'exception
        }
    }

    public void modifier(Abonnement abonnement) throws SQLException { // Ajout de throws SQLException
        String sql = "UPDATE abonnements SET date_debut = ?, date_fin = ?, membre_id = ?, type_abonnement_code = ? WHERE id = ?";

        try (Connection session = Connexion.getSessionV2();
             PreparedStatement statement = session.prepareStatement(sql)) {

            statement.setTimestamp(1, Timestamp.valueOf(abonnement.getDateDebut()));
            statement.setTimestamp(2, abonnement.getDateFin() != null ? Timestamp.valueOf(abonnement.getDateFin()) : null);
            statement.setInt(3, abonnement.getMembre().getId());
            statement.setString(4, abonnement.getTypeAbonnement().getCode());
            statement.setInt(5, abonnement.getId());

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Abonnement DAO: " + abonnement.getId() + " modifié avec succès.");
            } else {
                System.out.println("Abonnement DAO: Aucune ligne affectée lors de la modification de l'abonnement (ID: " + abonnement.getId() + ").");
            }

        } catch (SQLException e) {
            System.err.println("Abonnement DAO: Erreur lors de la modification de l'abonnement : " + e.getMessage());
            e.printStackTrace();
            throw e; // Relancer l'exception
        }
    }

    public void supprimer(int id) throws SQLException { // Modification pour prendre un int id et ajout de throws SQLException
        String sql = "DELETE FROM abonnements WHERE id = ?";

        try (Connection session = Connexion.getSessionV2();
             PreparedStatement statement = session.prepareStatement(sql)) {

            statement.setInt(1, id);

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Abonnement DAO: Abonnement avec ID " + id + " supprimé avec succès.");
            } else {
                System.out.println("Abonnement DAO: Aucun abonnement trouvé pour la suppression avec ID: " + id + ".");
            }

        } catch (SQLException e) {
            System.err.println("Abonnement DAO: Erreur lors de la suppression de l'abonnement (ID: " + id + ") : " + e.getMessage());
            e.printStackTrace();
            throw e; // Relancer l'exception
        }
    }

    public Abonnement trouver(int id) throws SQLException { // Ajout de throws SQLException
        String sql = "SELECT * FROM abonnements WHERE id = ?";
        Abonnement abonnement = null;

        try (Connection session = Connexion.getSessionV2();
             PreparedStatement statement = session.prepareStatement(sql)) {

            statement.setInt(1, id);

            try (ResultSet resultSet = statement.executeQuery()) { // Utilisation de try-with-resources pour ResultSet
                if (resultSet.next()) {

                    LocalDateTime dateDebut = null;
                    Timestamp timestampDebut = resultSet.getTimestamp("date_debut");
                    if (timestampDebut != null) {
                        dateDebut = timestampDebut.toLocalDateTime();
                    }

                    LocalDateTime dateFin = null;
                    Timestamp timestampFin = resultSet.getTimestamp("date_fin");
                    if (timestampFin != null) {
                        dateFin = timestampFin.toLocalDateTime();
                    }

                    Membre membre = new Membre();
                    membre.setId(resultSet.getInt("membre_id")); // Récupérer l'ID du membre

                    TypeAbonnement typeAbonnement = new TypeAbonnement();
                    typeAbonnement.setCode(resultSet.getString("type_abonnement_code")); // Récupérer le code du type d'abonnement

                    abonnement = new Abonnement(); // Créer un nouvel objet Abonnement
                    abonnement.setId(resultSet.getInt("id")); // L'ID vient du ResultSet
                    abonnement.setDateDebut(dateDebut);
                    abonnement.setDateFin(dateFin);
                    abonnement.setMembre(membre);
                    abonnement.setTypeAbonnement(typeAbonnement);
                }
            }
            if (abonnement != null) {
                System.out.println("Abonnement DAO: Abonnement trouvé avec succès (ID: " + abonnement.getId() + ").");
            } else {
                System.out.println("Abonnement DAO: Aucun abonnement trouvé pour l'ID : " + id + ".");
            }
        } catch (SQLException e) {
            System.err.println("Abonnement DAO: Erreur lors de la recherche de l'abonnement (ID: " + id + ") : " + e.getMessage());
            e.printStackTrace();
            throw e; // Relancer l'exception
        }

        return abonnement;
    }

    public List<Abonnement> listerTous() throws SQLException { // Ajout de throws SQLException
        String sql = "SELECT * FROM abonnements";
        List<Abonnement> abonnements = new ArrayList<>();

        try (Connection session = Connexion.getSessionV2();
             PreparedStatement statement = session.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Abonnement abonnement = new Abonnement();

                abonnement.setId(resultSet.getInt("id"));

                Timestamp timestampDebut = resultSet.getTimestamp("date_debut");
                if (timestampDebut != null) {
                    abonnement.setDateDebut(timestampDebut.toLocalDateTime());
                }

                Timestamp timestampFin = resultSet.getTimestamp("date_fin");
                if (timestampFin != null) {
                    abonnement.setDateFin(timestampFin.toLocalDateTime());
                }

                Membre membre = new Membre();
                membre.setId(resultSet.getInt("membre_id"));
                abonnement.setMembre(membre);

                TypeAbonnement typeAbonnement = new TypeAbonnement();
                typeAbonnement.setCode(resultSet.getString("type_abonnement_code"));
                abonnement.setTypeAbonnement(typeAbonnement);

                abonnements.add(abonnement);
            }
            if (abonnements.isEmpty()) {
                System.out.println("Abonnement DAO: La liste des abonnements est vide.");
            }
        } catch (SQLException e) {
            System.err.println("Abonnement DAO: Erreur lors de la récupération des abonnements : " + e.getMessage());
            e.printStackTrace();
            throw e; // Relancer l'exception
        }

        return abonnements;
    }
}
