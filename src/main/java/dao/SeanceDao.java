package dao;

import entite.Salle;
import entite.Seance;
import util.Connexion;

import java.sql.*; // Importation de java.sql.* inclut Connection, PreparedStatement, ResultSet, SQLException, Statement
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SeanceDao {

    public void ajouter(Seance seance) throws SQLException { // Ajout de throws SQLException
        // Ne PAS inclure 'id' dans la clause INSERT car c'est AUTO_INCREMENT
        String sql = "INSERT INTO seances(date_debut, date_fin, salle_id) VALUES (?, ?, ?)";

        try (Connection session = Connexion.getSessionV2();
             // Indiquer à la PreparedStatement de retourner les clés générées
             PreparedStatement ps = session.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) { // Ajout de Statement.RETURN_GENERATED_KEYS

            ps.setTimestamp(1, Timestamp.valueOf(seance.getDateDebut()));
            // Gérer le cas où date_fin peut être null si votre BDD le permet et que l'entité le reflète.
            // Si date_fin est obligatoire, assurez-vous qu'elle est toujours non-null dans votre objet Seance.
            ps.setTimestamp(2, seance.getDateFin() != null ? Timestamp.valueOf(seance.getDateFin()) : null);
            // Ceci est crucial : assurez-vous que l'objet Salle a bien son ID de BDD
            // après son insertion via SalleService.ajouter().
            ps.setInt(3, seance.getSalle().getId());

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                // Récupérer l'ID auto-généré par la base de données
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int id = generatedKeys.getInt(1); // Récupère le premier ID généré
                        seance.setId(id); // Met à jour l'objet Seance avec l'ID réel de la base de données
                        System.out.println("Seance DAO: " + seance.getId() + " ajoutée avec succès.");
                    } else {
                        System.out.println("Seance DAO: " + seance.getId() + " ajoutée, mais aucun ID généré n'a été trouvé.");
                    }
                }
            } else {
                System.out.println("Seance DAO: Aucune ligne ajoutée pour la séance.");
            }

        } catch (SQLException e) {
            System.err.println("Seance DAO: Erreur lors de l'ajout de la séance : " + e.getMessage());
            e.printStackTrace();
            throw e; // Relancer l'exception
        }
    }

    public void modifier(Seance seance) throws SQLException { // Ajout de throws SQLException
        String sql = "UPDATE seances SET date_debut = ?, date_fin = ?, salle_id = ? WHERE id = ?";

        try (Connection session = Connexion.getSessionV2();
             PreparedStatement ps = session.prepareStatement(sql)) {

            ps.setTimestamp(1, Timestamp.valueOf(seance.getDateDebut()));
            ps.setTimestamp(2, seance.getDateFin() != null ? Timestamp.valueOf(seance.getDateFin()) : null);
            ps.setInt(3, seance.getSalle().getId());
            ps.setInt(4, seance.getId());

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Seance DAO: " + seance.getId() + " modifiée avec succès.");
            } else {
                System.out.println("Seance DAO: Aucune ligne affectée lors de la modification de la séance (ID: " + seance.getId() + ").");
            }

        } catch (SQLException e) {
            System.err.println("Seance DAO: Erreur lors de la modification de la séance : " + e.getMessage());
            e.printStackTrace();
            throw e; // Relancer l'exception
        }
    }

    public void supprimer(int id) throws SQLException { // Modification pour prendre un int id et ajout de throws SQLException
        String sql = "DELETE FROM seances WHERE id = ?";

        try (Connection session = Connexion.getSessionV2();
             PreparedStatement ps = session.prepareStatement(sql)) {

            ps.setInt(1, id);
            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Seance DAO: Seance avec ID " + id + " supprimée avec succès.");
            } else {
                System.out.println("Seance DAO: Aucune séance trouvée pour la suppression avec ID: " + id + ".");
            }

        } catch (SQLException e) {
            System.err.println("Seance DAO: Erreur lors de la suppression de la séance (ID: " + id + ") : " + e.getMessage());
            e.printStackTrace();
            throw e; // Relancer l'exception
        }
    }

    public Seance trouver(int id) throws SQLException { // Ajout de throws SQLException
        String sql = "SELECT * FROM seances WHERE id = ?";
        Seance seance = null;

        try (Connection session = Connexion.getSessionV2();
             PreparedStatement ps = session.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) { // Utilisation de try-with-resources pour ResultSet
                if (rs.next()) {
                    LocalDateTime dateDebut = null;
                    Timestamp tsDebut = rs.getTimestamp("date_debut");
                    if (tsDebut != null) dateDebut = tsDebut.toLocalDateTime();

                    LocalDateTime dateFin = null;
                    Timestamp tsFin = rs.getTimestamp("date_fin");
                    if (tsFin != null) dateFin = tsFin.toLocalDateTime();

                    Salle salle = new Salle();
                    salle.setId(rs.getInt("salle_id"));
                    // Optionnel: charger les détails complets de la salle ici via SalleDao si nécessaire.

                    seance = new Seance(dateDebut, dateFin, salle);
                    seance.setId(rs.getInt("id")); // L'ID vient du ResultSet, pas du paramètre
                }
            }
            if (seance != null) {
                System.out.println("Seance DAO: Seance trouvée avec succès (ID: " + seance.getId() + ").");
            } else {
                System.out.println("Seance DAO: Aucune séance trouvée pour l'ID : " + id + ".");
            }

        } catch (SQLException e) {
            System.err.println("Seance DAO: Erreur lors de la recherche de la séance (ID: " + id + ") : " + e.getMessage());
            e.printStackTrace();
            throw e; // Relancer l'exception
        }

        return seance;
    }

    public List<Seance> listerTous() throws SQLException { // Ajout de throws SQLException
        String sql = "SELECT * FROM seances";
        List<Seance> seances = new ArrayList<>();

        try (Connection session = Connexion.getSessionV2();
             PreparedStatement ps = session.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Seance seance = new Seance();
                seance.setId(rs.getInt("id"));

                Timestamp tsDebut = rs.getTimestamp("date_debut");
                if (tsDebut != null) seance.setDateDebut(tsDebut.toLocalDateTime());

                Timestamp tsFin = rs.getTimestamp("date_fin");
                if (tsFin != null) seance.setDateFin(tsFin.toLocalDateTime());

                Salle salle = new Salle();
                salle.setId(rs.getInt("salle_id"));
                // Optionnel: charger les détails complets de la salle ici via SalleDao si nécessaire.
                seance.setSalle(salle);

                seances.add(seance);
            }
            if (seances.isEmpty()) {
                System.out.println("Seance DAO: La liste des séances est vide.");
            }
        } catch (SQLException e) {
            System.err.println("Seance DAO: Erreur lors de la récupération des séances : " + e.getMessage());
            e.printStackTrace();
            throw e; // Relancer l'exception
        }

        return seances;
    }
}