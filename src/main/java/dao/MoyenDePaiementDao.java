package dao;

import entite.MoyenDePaiement;
import util.Connexion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException; // Assurez-vous que cette importation est présente
import java.util.ArrayList;
import java.util.List;

public class MoyenDePaiementDao {

    public void ajouter(MoyenDePaiement moyenDePaiement) throws SQLException { // Ajout de throws SQLException
        String sql = "INSERT INTO moyen_de_paiements (code, libelle) VALUES (?, ?)";
        try (Connection session = Connexion.getSessionV2();
             PreparedStatement statement = session.prepareStatement(sql)) {

            statement.setString(1, moyenDePaiement.getCode());
            statement.setString(2, moyenDePaiement.getLibelle());

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("MoyenDePaiement DAO: " + moyenDePaiement.getLibelle() + " ajouté avec succès.");
            } else {
                System.out.println("MoyenDePaiement DAO: Aucune ligne affectée lors de l'ajout du moyen de paiement.");
            }

        } catch (SQLException e) {
            System.err.println("MoyenDePaiement DAO: Erreur lors de l'ajout du moyen de paiement : " + e.getMessage());
            e.printStackTrace();
            throw e; // Relancer l'exception
        }
    }

    public void modifier(MoyenDePaiement moyenDePaiement) throws SQLException { // Ajout de throws SQLException
        String sql = "UPDATE moyen_de_paiements SET libelle = ? WHERE code = ?";
        try (Connection session = Connexion.getSessionV2();
             PreparedStatement statement = session.prepareStatement(sql)) {

            statement.setString(1, moyenDePaiement.getLibelle());
            statement.setString(2, moyenDePaiement.getCode());

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("MoyenDePaiement DAO: " + moyenDePaiement.getLibelle() + " modifié avec succès.");
            } else {
                System.out.println("MoyenDePaiement DAO: Aucune ligne affectée lors de la modification du moyen de paiement.");
            }

        } catch (SQLException e) {
            System.err.println("MoyenDePaiement DAO: Erreur lors de la modification du moyen de paiement : " + e.getMessage());
            e.printStackTrace();
            throw e; // Relancer l'exception
        }
    }

    public void supprimer(String code) throws SQLException { // Ajout de throws SQLException
        String sql = "DELETE FROM moyen_de_paiements WHERE code = ?";
        try (Connection session = Connexion.getSessionV2();
             PreparedStatement statement = session.prepareStatement(sql)) {

            statement.setString(1, code);
            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("MoyenDePaiement DAO: Moyen de paiement avec code " + code + " supprimé avec succès.");
            } else {
                System.out.println("MoyenDePaiement DAO: Aucun moyen de paiement trouvé pour la suppression avec le code " + code + ".");
            }


        } catch (SQLException e) {
            System.err.println("MoyenDePaiement DAO: Erreur lors de la suppression du moyen de paiement : " + e.getMessage());
            e.printStackTrace();
            throw e; // Relancer l'exception
        }
    }

    public MoyenDePaiement trouver(String code) throws SQLException { // Ajout de throws SQLException
        String sql = "SELECT * FROM moyen_de_paiements WHERE code = ?";
        MoyenDePaiement moyenDePaiement = null;

        try (Connection session = Connexion.getSessionV2();
             PreparedStatement statement = session.prepareStatement(sql)) {

            statement.setString(1, code);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    moyenDePaiement = new MoyenDePaiement();
                    moyenDePaiement.setCode(resultSet.getString("code"));
                    moyenDePaiement.setLibelle(resultSet.getString("libelle"));
                }
            }
            // Retiré le message "Aucun moyen de paiement trouvé" car le service ou contrôleur peut le gérer.

        } catch (SQLException e) {
            System.err.println("MoyenDePaiement DAO: Erreur lors de la recherche du moyen de paiement : " + e.getMessage());
            e.printStackTrace();
            throw e; // Relancer l'exception
        }
        return moyenDePaiement;
    }

    public List<MoyenDePaiement> listerTous() throws SQLException { // Ajout de throws SQLException
        String sql = "SELECT * FROM moyen_de_paiements";
        List<MoyenDePaiement> moyensDePaiement = new ArrayList<>();

        try (Connection session = Connexion.getSessionV2();
             PreparedStatement statement = session.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                MoyenDePaiement moyenDePaiement = new MoyenDePaiement();
                moyenDePaiement.setCode(resultSet.getString("code"));
                moyenDePaiement.setLibelle(resultSet.getString("libelle"));

                moyensDePaiement.add(moyenDePaiement);
            }
            // Retiré le message "La liste est vide" car le service ou contrôleur peut le gérer.
        } catch (SQLException e) {
            System.err.println("MoyenDePaiement DAO: Erreur lors de la récupération des moyens de paiement : " + e.getMessage());
            e.printStackTrace();
            throw e; // Relancer l'exception
        }
        return moyensDePaiement;
    }
}
