package entite;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Abonnement {
    private int id;
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
    private TypeAbonnement typeAbonnement;

    private Paiement paiement;
    private Membre membre;

    public Abonnement(){

    }

    public Abonnement(int id, LocalDateTime dateDebut, LocalDateTime dateFin, Membre membre, TypeAbonnement typeAbonnement) {
        this.id = id;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.membre = membre;
        this.typeAbonnement = typeAbonnement;
    }

    public Abonnement( LocalDateTime dateDebut, LocalDateTime dateFin, Membre membre, TypeAbonnement typeAbonnement) {
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.membre = membre;
        this.typeAbonnement = typeAbonnement;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDateTime dateDebut) {
        this.dateDebut = dateDebut;
    }

    public LocalDateTime getDateFin() {
        return dateFin;
    }

    public void setDateFin(LocalDateTime dateFin) {
        this.dateFin = dateFin;
    }

    public TypeAbonnement getTypeAbonnement() {
        return typeAbonnement;
    }

    public void setTypeAbonnement(TypeAbonnement typeAbonnement) {
        this.typeAbonnement = typeAbonnement;
    }

    public Paiement getPaiement() {
        return paiement;
    }

    public void setPaiement(Paiement paiement) {
        this.paiement = paiement;
    }

    public Membre getMembre() {
        return membre;
    }

    public void setMembre(Membre membre) {
        this.membre = membre;
    }

    @Override
    public String toString() {
        return typeAbonnement.toString();
    }



}

