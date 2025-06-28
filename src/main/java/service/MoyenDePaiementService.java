package service;

import dao.MoyenDePaiementDao;
import entite.MoyenDePaiement;

import java.sql.SQLException;
import java.util.List;

public class MoyenDePaiementService {


    private MoyenDePaiementDao dao = new MoyenDePaiementDao();

    public void ajouter(MoyenDePaiement moyenDePaiement) throws SQLException {
        dao.ajouter(moyenDePaiement);

    }

    public void modifier(MoyenDePaiement moyenDePaiement) throws SQLException {
        dao.modifier(moyenDePaiement);

    }

    public void supprimer(String code) throws SQLException {
        dao.supprimer(code);

    }

    public MoyenDePaiement trouver(String code) throws SQLException {
        return dao.trouver(code);
    }

    public List<MoyenDePaiement> listerTous() throws SQLException {
        return dao.listerTous();
    }

}
