package entite;

import java.time.LocalDateTime;
import java.util.List;

public class Membre {

    private int id;
    private LocalDateTime dateInscription;
    private List<Abonnement> abonnements;
    private List<Seance> seances;
    private Client client;

    public Membre(){

    }

    public Membre(int id, LocalDateTime dateInscription, Client client) {
        this.id = id;
        this.dateInscription = dateInscription;
        this.client = client;
    }
    public Membre( LocalDateTime dateInscription, Client client) {
        this.dateInscription = dateInscription;
        this.client = client;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getDateInscription() {
        return dateInscription;
    }

    public void setDateInscription(LocalDateTime dateInscription) {
        this.dateInscription = dateInscription;
    }

    public List<Abonnement> getAbonnements() {
        return abonnements;
    }

    public void setAbonnements(List<Abonnement> abonnements) {
        this.abonnements = abonnements;
    }

    public List<Seance> getSeances() {
        return seances;
    }

    public void setSeances(List<Seance> seances) {
        this.seances = seances;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    @Override
    public String toString() {
        return "Membre{" +
                "id=" + id +
                ", dateInscription=" + dateInscription +
                ", client=" + client.getNom() + " " + client.getPrenom() +
                '}';
    }


}
