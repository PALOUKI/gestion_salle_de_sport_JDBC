package entite;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Paiement {

    private int id;
    private LocalDateTime dateDePaiement;
    private int montant;
    private MoyenDePaiement moyenDePaiement;
    private Abonnement abonnement;

    public Paiement() {

    }

    public Paiement(int id, LocalDateTime dateDePaiement, int montant, MoyenDePaiement moyenDePaiement, Abonnement abonnement) {
        this.id = id;
        this.dateDePaiement = dateDePaiement;
        this.montant = montant;
        this.moyenDePaiement = moyenDePaiement;
        this.abonnement = abonnement;
    }

    public Paiement(LocalDateTime dateDePaiement, int montant, MoyenDePaiement moyenDePaiement, Abonnement abonnement) {
        this.dateDePaiement = dateDePaiement;
        this.montant = montant;
        this.moyenDePaiement = moyenDePaiement;
        this.abonnement = abonnement;
    }




    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Abonnement getAbonnement() {
        return abonnement;
    }

    public void setAbonnement(Abonnement abonnement) {
        this.abonnement = abonnement;
    }

    public LocalDateTime getDateDePaiement() {
        return dateDePaiement;
    }

    public void setDateDePaiement(LocalDateTime dateDePaiement) {
        this.dateDePaiement = dateDePaiement;
    }

    public int getMontant() {
        return montant;
    }

    public void setMontant(int montant) {
        this.montant = montant;
    }

    public MoyenDePaiement getMoyenDePaiement() {
        return moyenDePaiement;
    }

    public void setMoyenDePaiement(MoyenDePaiement moyenDePaiement) {
        this.moyenDePaiement = moyenDePaiement;
    }

    public Abonnement getAbonement() {
        return abonnement;
    }

    public void setAbonement(Abonnement abonnement) {
        this.abonnement = abonnement;
    }


}

