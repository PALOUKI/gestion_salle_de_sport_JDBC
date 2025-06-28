package service;

import dao.SeanceDao;
import entite.Seance;

import java.sql.SQLException;
import java.util.List;

public class SeanceService {
    private SeanceDao dao = new SeanceDao();

    public void ajouter(Seance seance) throws SQLException {
        dao.ajouter(seance);

    }

    public void modifier(Seance seance) throws SQLException {
        dao.modifier(seance);

    }

    public void supprimer(int seance) throws SQLException {
        dao.supprimer(seance);

    }

    public Seance trouver (int id) throws SQLException {
        return dao.trouver(id);
    }

    public List<Seance> listerTous() throws SQLException {
        return dao.listerTous();
    }
}
