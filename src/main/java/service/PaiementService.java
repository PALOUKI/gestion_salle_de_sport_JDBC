package service;

import dao.PaiementDao;
import entite.Paiement;

import java.sql.SQLException;
import java.util.List;

public class PaiementService {
    private PaiementDao dao = new PaiementDao();

    public void ajouter(Paiement paiement) throws SQLException {
        dao.ajouter(paiement);

    }

    public void modifier(Paiement paiement) throws SQLException {
        dao.modifier(paiement);

    }

    public void supprimer(int id) throws SQLException {
        dao.supprimer(id);

    }

    public Paiement trouver (int id) throws SQLException {
        return dao.trouver(id);
    }

    public List<Paiement> listerTous() throws SQLException {
        return dao.listerTous();
    }
}
