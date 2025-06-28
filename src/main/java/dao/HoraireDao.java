package dao;

import entite.Horaire;
import entite.Salle;
import util.Connexion;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class HoraireDao {
/*
    public void ajouter(Horaire horaire) {

        String sql = "INSERT INTO horaires(debut, fin, salle_id) VALUES (?, ?, ?)";

        try (Connection session = Connexion.getSessionV2();
             PreparedStatement ps = session.prepareStatement(sql)) {

            ps.setTimestamp(1, Timestamp.valueOf(horaire.getDebut()));
            ps.setTimestamp(2, Timestamp.valueOf(horaire.getFin()));
            ps.setInt(3, horaire.getSalle().getId());

            ps.executeUpdate();

            System.out.println("Horaire: " + horaire.getId() + " ajouté avec succès.");

        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout de l'horaire : " + e.getMessage());
        }
    }

    public void modifier(Horaire horaire) {
        String sql = "UPDATE horaires SET debut = ?, fin = ?, salle_id = ? WHERE id = ?";

        try (Connection session = Connexion.getSessionV2();
             PreparedStatement ps = session.prepareStatement(sql)) {

            ps.setTimestamp(1, Timestamp.valueOf(horaire.getDebut()));
            ps.setTimestamp(2, Timestamp.valueOf(horaire.getFin()));
            ps.setInt(3, horaire.getSalles().getId());
            ps.setInt(4, horaire.getId());

            ps.executeUpdate();

            System.out.println("Horaire: "  + horaire.getId() + " modifié avec succès.");

        } catch (SQLException e) {
            System.out.println("Erreur lors de la modification de l'horaire : " + e.getMessage());
        }
    }

    public void supprimer(Horaire horaire) {
        String sql = "DELETE FROM horaires WHERE id = ?";

        try (Connection session = Connexion.getSessionV2();
             PreparedStatement ps = session.prepareStatement(sql)) {

            ps.setInt(1, horaire.getId());
            ps.executeUpdate();

            System.out.println("Horaire: " + horaire.getId() + " supprimé avec succès.");

        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression de l'horaire : " + e.getMessage());
        }
    }

    public Horaire trouver(int id) {
        String sql = "SELECT * FROM seances WHERE id = ?";
        Horaire horaire = null;

        try (Connection session = Connexion.getSessionV2();
             PreparedStatement ps = session.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    LocalDateTime debut = null;
                    Timestamp tsDebut = rs.getTimestamp("debut");
                    if (tsDebut != null) debut = tsDebut.toLocalDateTime();

                    LocalDateTime fin = null;
                    Timestamp tsFin = rs.getTimestamp("fin");
                    if (tsFin != null) fin = tsFin.toLocalDateTime();

                    Salle salle = new Salle();
                    salle.setId(rs.getInt("salle_id"));

                    horaire = new Horaire(debut, fin, (List<Salle>) salle);
                    horaire.setId(id);
                }
            }

        } catch (SQLException e) {
            System.out.println("Erreur lors de la recherche de l'horaire : " + e.getMessage());
        }

        return horaire;
    }

    public List<Horaire> listerTous() {
        String sql = "SELECT * FROM horaires";
        List<Horaire> horaires = new ArrayList<>();

        try (Connection session = Connexion.getSessionV2();
             PreparedStatement ps = session.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Horaire horaire = new Horaire();
                horaire.setId(rs.getInt("id"));

                Timestamp tsDebut = rs.getTimestamp("debut");
                if (tsDebut != null) horaire.setDebut(tsDebut.toLocalDateTime());

                Timestamp tsFin = rs.getTimestamp("fin");
                if (tsFin != null) horaire.setFin(tsFin.toLocalDateTime());

                Salle salle = new Salle();
                salle.setId(rs.getInt("salle_id"));
                horaire.setSalle((List<Salle>) salle);

                horaires.add(horaire);
            }

        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des séances : " + e.getMessage());
        }

        return horaires;
    }

 */
}
