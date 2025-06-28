package service;

import dao.MembreDao;
import entite.Membre;

import java.sql.SQLException;
import java.util.List;

public class MembreService {
    private MembreDao dao = new MembreDao();

    public void ajouter(Membre membre) throws SQLException {
        dao.ajouter(membre);
    }

    public void modifier(Membre membre) throws SQLException {
        dao.modifier(membre);

    }

    public void supprimer(int id) throws SQLException {
        dao.supprimer(id);

    }

    public Membre trouver (int id) throws SQLException {
        return dao.trouver(id);
    }

    public List<Membre> listerTous() throws SQLException {
        return dao.listerTous();
    }

    public int countMembers() throws SQLException {
        return dao.countMembers();
    }
}
