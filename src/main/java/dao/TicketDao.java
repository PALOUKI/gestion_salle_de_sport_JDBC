package dao;

import entite.Client;
import entite.Ticket;
import util.Connexion;

import java.sql.*; // Importation de java.sql.* inclut Connection, PreparedStatement, ResultSet, SQLException, Statement
import java.util.ArrayList;
import java.util.List;

public class TicketDao {

    public void ajouter(Ticket ticket) throws SQLException { // Ajout de throws SQLException
        // Ne PAS inclure 'id' dans la clause INSERT car c'est AUTO_INCREMENT
        String sql = "INSERT INTO tickets(nombre_de_seance, montant, client_id) VALUES (?, ?, ?)";

        try (Connection session = Connexion.getSessionV2();
             // Indiquer à la PreparedStatement de retourner les clés générées
             PreparedStatement ps = session.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) { // Ajout de Statement.RETURN_GENERATED_KEYS

            ps.setInt(1, ticket.getNombreDeSeance());
            ps.setInt(2, ticket.getMontant());
            // Ceci est crucial : assurez-vous que l'objet Client a bien son ID de BDD
            // après son insertion via ClientService.ajouter().
            ps.setInt(3, ticket.getClient().getId());

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                // Récupérer l'ID auto-généré par la base de données
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int id = generatedKeys.getInt(1); // Récupère le premier ID généré
                        ticket.setId(id); // Met à jour l'objet Ticket avec l'ID réel de la base de données
                        System.out.println("Ticket DAO: " + ticket.getId() + " ajouté avec succès.");
                    } else {
                        System.out.println("Ticket DAO: " + ticket.getId() + " ajouté, mais aucun ID généré n'a été trouvé.");
                    }
                }
            } else {
                System.out.println("Ticket DAO: Aucune ligne ajoutée pour le ticket.");
            }

        } catch (SQLException e) {
            System.err.println("Ticket DAO: Erreur lors de l'ajout du ticket : " + e.getMessage());
            e.printStackTrace();
            throw e; // Relancer l'exception
        }
    }

    public void modifier(Ticket ticket) throws SQLException { // Ajout de throws SQLException
        String sql = "UPDATE tickets SET nombre_de_seance = ?, montant = ?, client_id = ? WHERE id = ?";

        try (Connection session = Connexion.getSessionV2();
             PreparedStatement ps = session.prepareStatement(sql)) {

            ps.setInt(1, ticket.getNombreDeSeance());
            ps.setInt(2, ticket.getMontant());
            ps.setInt(3, ticket.getClient().getId());
            ps.setInt(4, ticket.getId());

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Ticket DAO: " + ticket.getId() + " modifié avec succès.");
            } else {
                System.out.println("Ticket DAO: Aucune ligne affectée lors de la modification du ticket (ID: " + ticket.getId() + ").");
            }

        } catch (SQLException e) {
            System.err.println("Ticket DAO: Erreur lors de la modification du ticket : " + e.getMessage());
            e.printStackTrace();
            throw e; // Relancer l'exception
        }
    }

    public void supprimer(int ticket) throws SQLException { // Modification pour prendre un int id et ajout de throws SQLException
        String sql = "DELETE FROM tickets WHERE id = ?";

        try (Connection session = Connexion.getSessionV2();
             PreparedStatement ps = session.prepareStatement(sql)) {

            ps.setInt(1, ticket);
            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Ticket DAO: Ticket avec ID " + ticket + " supprimé avec succès.");
            } else {
                System.out.println("Ticket DAO: Aucun ticket trouvé pour la suppression avec ID: " + ticket + ".");
            }

        } catch (SQLException e) {
            System.err.println("Ticket DAO: Erreur lors de la suppression du ticket (ID: " + ticket + ") : " + e.getMessage());
            e.printStackTrace();
            throw e; // Relancer l'exception
        }
    }

    public Ticket trouver(int id) throws SQLException { // Ajout de throws SQLException
        String sql = "SELECT * FROM tickets WHERE id = ?";
        Ticket ticket = null;

        try (Connection session = Connexion.getSessionV2();
             PreparedStatement ps = session.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) { // Utilisation de try-with-resources pour ResultSet
                if (rs.next()) {
                    int nombreDeSeance = rs.getInt("nombre_de_seance");
                    int montant = rs.getInt("montant");

                    Client client = new Client();
                    client.setId(rs.getInt("client_id")); // Ne pas oublier de récupérer l'ID du client

                    ticket = new Ticket(nombreDeSeance, montant, client);
                    ticket.setId(rs.getInt("id")); // L'ID vient du ResultSet, pas du paramètre
                }
            }
            if (ticket != null) {
                System.out.println("Ticket DAO: Ticket trouvé avec succès (ID: " + ticket.getId() + ").");
            } else {
                System.out.println("Ticket DAO: Aucun ticket trouvé pour l'ID : " + id + ".");
            }
        } catch (SQLException e) {
            System.err.println("Ticket DAO: Erreur lors de la recherche du ticket (ID: " + id + ") : " + e.getMessage());
            e.printStackTrace();
            throw e; // Relancer l'exception
        }

        return ticket;
    }

    public List<Ticket> listerTous() throws SQLException { // Ajout de throws SQLException
        String sql = "SELECT * FROM tickets";
        List<Ticket> tickets = new ArrayList<>();

        try (Connection session = Connexion.getSessionV2();
             PreparedStatement ps = session.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Ticket ticket = new Ticket();
                ticket.setId(rs.getInt("id"));
                ticket.setNombreDeSeance(rs.getInt("nombre_de_seance"));
                ticket.setMontant(rs.getInt("montant"));

                Client client = new Client();
                client.setId(rs.getInt("client_id"));
                ticket.setClient(client);

                tickets.add(ticket);
            }
            if (tickets.isEmpty()) {
                System.out.println("Ticket DAO: La liste des tickets est vide.");
            }
        } catch (SQLException e) {
            System.err.println("Ticket DAO: Erreur lors de la récupération des tickets : " + e.getMessage());
            e.printStackTrace();
            throw e; // Relancer l'exception
        }

        return tickets;
    }
}
