package service;

import dao.ClientDao;
import dao.TypeAbonnementDao;
import entite.Client;
import entite.TypeAbonnement;

import java.sql.SQLException;
import java.util.List;

public class ClientService {

    private ClientDao dao =  new ClientDao();

    public void ajouter(Client client) throws SQLException {
        dao.ajouter(client);
    }

    public void supprimer(int id) throws SQLException {
        dao.supprimer(id);

    }

    public void modifier(Client client) throws SQLException {
        dao.modifier(client);

    }

    public Client trouver(int id) throws SQLException {
        return dao.trouver(id);
    }

    public List<Client> listerTous() throws SQLException {
        return dao.listerTous();
    }
}
