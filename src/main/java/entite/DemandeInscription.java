package entite;

import java.time.LocalDateTime;

public class DemandeInscription {

    private int id;
    private LocalDateTime dateDeDemande;
    private LocalDateTime dateDeTraitement;
    private Client client;

    public DemandeInscription(){

    }

    public DemandeInscription(LocalDateTime dateDeDemande, LocalDateTime dateDeTraitement, Client client) {
        this.dateDeDemande = dateDeDemande;
        this.dateDeTraitement = dateDeTraitement;
        this.client = client;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getDateDeDemande() {
        return dateDeDemande;
    }

    public void setDateDeDemande(LocalDateTime dateDeDemande) {
        this.dateDeDemande = dateDeDemande;
    }

    public LocalDateTime getDateDeTraitement() {
        return dateDeTraitement;
    }

    public void setDateDeTraitement(LocalDateTime dateDeTraitement) {
        this.dateDeTraitement = dateDeTraitement;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

}
