package entite;

import java.util.ArrayList;
import java.util.List;

public class Ticket {

    private int id;
    private int nombreDeSeance;
    private int montant;
    private Client client;

    public Ticket(){

    }

    public Ticket(int nombreDeSeance, int montant, Client client){
        this.nombreDeSeance = nombreDeSeance;
        this.montant = montant;
        this.client = client;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNombreDeSeance() {
        return nombreDeSeance;
    }

    public void setNombreDeSeance(int nombreDeSeance) {
        this.nombreDeSeance = nombreDeSeance;
    }

    public int getMontant() {
        return montant;
    }

    public void setMontant(int montant) {
        this.montant = montant;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

}

