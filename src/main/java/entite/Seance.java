package entite;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Seance {

    private int id;
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
    private Membre membre;
    private Salle salle;

    public Seance(){

    }

    public Seance(LocalDateTime dateDebut, LocalDateTime dateFin, Salle salle) {
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.salle = salle;
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

    public Membre getMembre() {
        return membre;
    }

    public void setMembre(Membre membre) {
        this.membre = membre;
    }

    public Salle getSalle() {
        return salle;
    }

    public void setSalle(Salle salle) {
        this.salle = salle;
    }

}

