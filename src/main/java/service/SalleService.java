package service;

import dao.SalleDao;
import entite.Salle;

import java.sql.SQLException;
import java.util.List;

public class SalleService {
    private SalleDao dao = new SalleDao();

    public void ajouter(Salle salle) throws SQLException {
        dao.ajouter(salle);

    }

    public void modifier(Salle salle) throws SQLException {
        dao.modifier(salle);

    }

    public void supprimer(int id) throws SQLException {
        dao.supprimer(id);

    }

    public Salle trouver(int id) throws SQLException {
        return dao.trouver(id);
    }

    public List<Salle> listerTous() throws SQLException {
        return dao.listerTous();
    }
}
