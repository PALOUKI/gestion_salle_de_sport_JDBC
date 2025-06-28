package dao;

import entite.Abonnement;
import entite.MoyenDePaiement;
import entite.Paiement;
import util.Connexion;

import java.sql.*; // Inclut Connection, PreparedStatement, ResultSet, SQLException, Statement
import java.util.ArrayList;
import java.util.List;

public class PaiementDao {

    public void ajouter(Paiement paiement) throws SQLException { // Ajout de throws SQLException
        // La requête INSERT est correcte, car 'id' est AUTO_INCREMENT et est généré par la BDD.
        String sql = "INSERT INTO paiements (montant, date_de_paiement, moyen_de_paiement_code, abonnement_id) " +
                "VALUES (?, ?, ?, ?)";
        try (Connection session = Connexion.getSessionV2();
             // Indiquer à la PreparedStatement de retourner les clés générées
             // C'est essentiel pour récupérer l'ID auto-généré par la BDD.
             PreparedStatement statement = session.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setInt(1, paiement.getMontant());
            statement.setTimestamp(2, Timestamp.valueOf(paiement.getDateDePaiement()));
            // Assurez-vous que le code du moyen de paiement est correct
            statement.setString(3, paiement.getMoyenDePaiement().getCode());
            // Assurez-vous que l'objet Abonnement a bien son ID de BDD
            statement.setInt(4, paiement.getAbonnement().getId());

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                // Récupérer l'ID auto-généré par la base de données
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int id = generatedKeys.getInt(1); // Récupère le premier ID généré
                        paiement.setId(id); // Met à jour l'objet Paiement avec l'ID réel de la base de données
                        System.out.println("Paiement DAO: " + paiement.getId() + " ajouté avec succès.");
                    } else {
                        System.out.println("Paiement DAO: " + paiement.getId() + " ajouté, mais aucun ID généré n'a été trouvé.");
                    }
                }
            } else {
                System.out.println("Paiement DAO: Aucune ligne ajoutée pour le paiement.");
            }

        } catch (SQLException e) {
            System.err.println("Paiement DAO: Erreur lors de l'ajout du paiement : " + e.getMessage());
            e.printStackTrace(); // Affichez la stack trace pour un diagnostic complet
            throw e; // Relancer l'exception
        }
    }

    public void modifier(Paiement paiement) throws SQLException { // Ajout de throws SQLException
        // Correction de la faute de frappe : 'moyen_de_depaiement_code' -> 'moyen_de_paiement_code'
        String sql = "UPDATE paiements SET montant = ?, date_de_paiement = ?, " +
                "moyen_de_paiement_code = ?, abonnement_id = ? WHERE id = ?";
        try (Connection session = Connexion.getSessionV2();
             PreparedStatement statement = session.prepareStatement(sql)) {

            statement.setInt(1, paiement.getMontant());
            statement.setTimestamp(2, Timestamp.valueOf(paiement.getDateDePaiement()));
            statement.setString(3, paiement.getMoyenDePaiement().getCode());
            statement.setInt(4, paiement.getAbonnement().getId());
            statement.setInt(5, paiement.getId());

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Paiement DAO: " + paiement.getId() + " modifié avec succès.");
            } else {
                System.out.println("Paiement DAO: Aucune ligne affectée lors de la modification du paiement (ID: " + paiement.getId() + ").");
            }

        } catch (SQLException e) {
            System.err.println("Paiement DAO: Erreur lors de la modification du paiement : " + e.getMessage());
            e.printStackTrace();
            throw e; // Relancer l'exception
        }
    }

    public Paiement trouver(int id) throws SQLException { // Ajout de throws SQLException
        String sql = "SELECT * FROM paiements WHERE id = ?";
        Paiement paiement = null;

        try (Connection session = Connexion.getSessionV2();
             PreparedStatement statement = session.prepareStatement(sql)) {

            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) { // Utilisation de try-with-resources pour ResultSet
                if (resultSet.next()) {
                    paiement = new Paiement();
                    paiement.setId(resultSet.getInt("id"));
                    paiement.setMontant(resultSet.getInt("montant"));

                    Timestamp tsPaiement = resultSet.getTimestamp("date_de_paiement");
                    paiement.setDateDePaiement(tsPaiement != null ? tsPaiement.toLocalDateTime() : null);

                    MoyenDePaiement moyenDePaiement = new MoyenDePaiement();
                    moyenDePaiement.setCode(resultSet.getString("moyen_de_paiement_code"));
                    // Optionnel: charger les détails complets du moyen de paiement ici via MoyenDePaiementDao.
                    paiement.setMoyenDePaiement(moyenDePaiement);

                    Abonnement abonnement = new Abonnement();
                    abonnement.setId(resultSet.getInt("abonnement_id"));
                    // Optionnel: charger les détails complets de l'abonnement ici via AbonnementDao.
                    paiement.setAbonnement(abonnement);
                }
            }
            if (paiement != null) {
                System.out.println("Paiement DAO: Paiement trouvé avec succès (ID: " + paiement.getId() + ").");
            } else {
                System.out.println("Paiement DAO: Aucun paiement trouvé pour l'ID : " + id + ".");
            }
        } catch (SQLException e) {
            System.err.println("Paiement DAO: Erreur lors de la recherche du paiement (ID: " + id + ") : " + e.getMessage());
            e.printStackTrace();
            throw e; // Relancer l'exception
        }
        return paiement;
    }

    public void supprimer(int id) throws SQLException { // Modification pour prendre un int id et ajout de throws SQLException
        String sql = "DELETE FROM paiements WHERE id = ?";
        try (Connection session = Connexion.getSessionV2();
             PreparedStatement statement = session.prepareStatement(sql)) {

            statement.setInt(1, id);
            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Paiement DAO: Paiement avec ID " + id + " supprimé avec succès.");
            } else {
                System.out.println("Paiement DAO: Aucun paiement trouvé pour la suppression avec ID: " + id + ".");
            }

        } catch (SQLException e) {
            System.err.println("Paiement DAO: Erreur lors de la suppression du paiement (ID: " + id + ") : " + e.getMessage());
            e.printStackTrace();
            throw e; // Relancer l'exception
        }
    }

    public List<Paiement> listerTous() throws SQLException { // Ajout de throws SQLException
        String sql = "SELECT * FROM paiements";
        List<Paiement> paiements = new ArrayList<>();

        try (Connection session = Connexion.getSessionV2();
             PreparedStatement statement = session.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) { // Utilisation de try-with-resources pour ResultSet

            while (resultSet.next()) {
                Paiement paiement = new Paiement();
                paiement.setId(resultSet.getInt("id"));
                paiement.setMontant(resultSet.getInt("montant"));

                Timestamp tsPaiement = resultSet.getTimestamp("date_de_paiement");
                paiement.setDateDePaiement(tsPaiement != null ? tsPaiement.toLocalDateTime() : null);

                MoyenDePaiement moyenDePaiement = new MoyenDePaiement();
                // Correction de la faute de frappe : 'moyen_paiement_code' -> 'moyen_de_paiement_code'
                moyenDePaiement.setCode(resultSet.getString("moyen_de_paiement_code"));
                // Optionnel: charger les détails complets du moyen de paiement ici via MoyenDePaiementDao.
                paiement.setMoyenDePaiement(moyenDePaiement);

                Abonnement abonnement = new Abonnement();
                abonnement.setId(resultSet.getInt("abonnement_id"));
                // Optionnel: charger les détails complets de l'abonnement ici via AbonnementDao.
                paiement.setAbonnement(abonnement);

                paiements.add(paiement);
            }
            if (paiements.isEmpty()) {
                System.out.println("Paiement DAO: La liste des paiements est vide.");
            }
        } catch (SQLException e) {
            System.err.println("Paiement DAO: Erreur lors de la récupération des paiements : " + e.getMessage());
            e.printStackTrace();
            throw e; // Relancer l'exception
        }
        return paiements;
    }
}
