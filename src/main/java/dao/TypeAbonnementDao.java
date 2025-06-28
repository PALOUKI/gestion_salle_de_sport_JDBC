package dao;

import entite.TypeAbonnement;
import util.Connexion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TypeAbonnementDao {

    public void ajouter(TypeAbonnement typeAbonnement) {
        String sql = "INSERT INTO type_abonnements(code, libelle, montant) VALUES (?, ?, ?)";

        try (Connection session = Connexion.getSessionV2();
             PreparedStatement ps = session.prepareStatement(sql)) {

            ps.setString(1, typeAbonnement.getCode());
            ps.setString(2, typeAbonnement.getLibelle());
            ps.setInt(3, typeAbonnement.getMontant());

            int rowsAffected = ps.executeUpdate();

            System.out.println("DAO: Resultat de ps.executeUpdate() : " + rowsAffected + " lignes affectées.");

            if (rowsAffected > 0) {
                System.out.println("DAO: Abonnement de type " + typeAbonnement.getLibelle() + " ajouté avec succès.");
            } else {
                System.out.println("DAO: Avertissement: L'ajout de l'abonnement " + typeAbonnement.getLibelle() + " n'a affecté aucune ligne. (Peut-être un code existant ?)");
            }

        } catch (SQLException e) {
            System.err.println("DAO: Erreur SQL lors de l'ajout du type d'abonnement : " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void modifier(TypeAbonnement typeAbonnement) {
        String sql = "UPDATE type_abonnements SET libelle = ?, montant = ? WHERE code = ?";

        try (Connection session = Connexion.getSessionV2();
             PreparedStatement ps = session.prepareStatement(sql)) {

            ps.setString(1, typeAbonnement.getLibelle());
            ps.setInt(2, typeAbonnement.getMontant());
            ps.setString(3, typeAbonnement.getCode());

            ps.executeUpdate();

            System.out.println("Abonnement de type " + typeAbonnement.getLibelle() + " modifié avec succès.");

        } catch (SQLException e) {
            System.out.println("Erreur lors de la modification du type d'abonnement : " + e.getMessage());
        }
    }

    public void supprimer(TypeAbonnement typeAbonnement) {
        String sql = "DELETE FROM type_abonnements WHERE code = ?";

        try (Connection session = Connexion.getSessionV2();
             PreparedStatement ps = session.prepareStatement(sql)) {

            ps.setString(1, typeAbonnement.getCode());

            ps.executeUpdate();

            System.out.println("Abonnement de type " + typeAbonnement.getLibelle() + " supprimé avec succès.");

        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression du type d'abonnement : " + e.getMessage());
        }
    }

    public TypeAbonnement trouver(String code) {
        String sql = "SELECT * FROM type_abonnements WHERE code = ?";
        TypeAbonnement typeAbonnement = null;

        try (Connection session = Connexion.getSessionV2();
             PreparedStatement ps = session.prepareStatement(sql)) {

            ps.setString(1, code);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String libelle = rs.getString("libelle");
                    int montant = rs.getInt("montant");
                    typeAbonnement = new TypeAbonnement(code, libelle, montant);
                }
            }
            // --- CORRECTION ICI : Vérifiez si typeAbonnement n'est pas null avant d'y accéder ---
            if (typeAbonnement != null) {
                System.out.println("Le type d'abonnement a été trouvé avec succès: " + typeAbonnement.getCode() + "   " + typeAbonnement.getLibelle() + ".");
            } else {
                System.out.println("Aucun type d'abonnement trouvé pour le code : " + code + ".");
            }
            // --- FIN CORRECTION ---

        } catch (SQLException e) {
            System.out.println("Erreur lors de la recherche du type d'abonnement : " + e.getMessage());
        }

        return typeAbonnement;
    }

    public List<TypeAbonnement> listerTous() {
        String sql = "SELECT * FROM type_abonnements";
        List<TypeAbonnement> liste = new ArrayList<>();

        try (Connection session = Connexion.getSessionV2();
             PreparedStatement ps = session.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String code = rs.getString("code");
                String libelle = rs.getString("libelle");
                int montant = rs.getInt("montant");
                liste.add(new TypeAbonnement(code, libelle, montant));
            }

        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des types d'abonnement : " + e.getMessage());
        }

        return liste;
    }

}
