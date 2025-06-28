package service;

import dao.AbonnementDao;
import entite.Abonnement;

import java.sql.SQLException;
import java.util.List;

public class AbonnementService {
    private AbonnementDao dao = new AbonnementDao();

    public void ajouter(Abonnement abonnement) throws SQLException {
        dao.ajouter(abonnement);
    }

    public void modifier(Abonnement abonnement) throws SQLException {
        dao.modifier(abonnement);
    }

    public void supprimer(int abonnement) throws SQLException {
        dao.supprimer(abonnement);
    }

    public Abonnement trouver (int id) throws SQLException {
        return dao.trouver(id);
        //
    }

    public List<Abonnement> listerTous() throws SQLException {
        return dao.listerTous();
    }
}

/*
TypeAbonnement typeAbonnement = new TypeAbonnement("HEBDO", "Abonnement hebdomadaire", 2000);
        typeAbonnementService.ajouter(typeAbonnement);
        */