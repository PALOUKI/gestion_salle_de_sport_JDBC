package service;

import dao.TicketDao;
import entite.Ticket;

import java.sql.SQLException;
import java.util.List;

public class TicketService {
    private TicketDao dao = new TicketDao();

    public void ajouter(Ticket ticket) throws SQLException {
        dao.ajouter(ticket);

    }

    public void modifier(Ticket ticket) throws SQLException {
        dao.modifier(ticket);

    }

    public void supprimer(int ticket) throws SQLException {
        dao.supprimer(ticket);

    }

    public Ticket trouver (int id) throws SQLException {
        return dao.trouver(id);
    }

    public List<Ticket> listerTous() throws SQLException {
        return dao.listerTous();
    }
}
