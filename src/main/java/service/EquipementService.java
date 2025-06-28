package service;

import dao.EquipementDao;


import entite.Equipement;

import java.sql.SQLException;
import java.util.List;

public class EquipementService {
    private EquipementDao dao = new EquipementDao();

    public void ajouter(Equipement equipement) throws SQLException {
        dao.ajouter(equipement);
    }

    public void modifier(Equipement equipement) throws SQLException {
        dao.modifier(equipement);

    }

    public void supprimer(int equipement) throws SQLException {
        dao.supprimer(equipement);

    }

    public Equipement trouver (int id) throws SQLException {
        return dao.trouver(id);
    }

    public List<Equipement> listerTous() throws SQLException {
        return dao.listerTous();
    }
}
