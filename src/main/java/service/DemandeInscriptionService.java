package service;

import dao.DemandeInscriptionDao;
import entite.DemandeInscription;

import java.sql.SQLException;
import java.util.List;

public class DemandeInscriptionService {

    private DemandeInscriptionDao dao = new DemandeInscriptionDao();

    public void ajouter(DemandeInscription demandeInscription) throws SQLException {
        dao.ajouter(demandeInscription);

    }

    public void modifier(DemandeInscription demandeInscription) throws SQLException {
        dao.modifier(demandeInscription);

    }

    public void supprimer(int demandeInscription) throws SQLException {
        dao.supprimer(demandeInscription);

    }

    public DemandeInscription trouver (int id) throws SQLException {
        return dao.trouver(id);
    }

    public List<DemandeInscription> listerTous() throws SQLException {
        return dao.listerTous();
    }

}
