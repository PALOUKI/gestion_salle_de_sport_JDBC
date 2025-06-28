package entite;

import java.time.LocalDateTime;
import java.util.List;

public class Horaire {

    private int id;
    private LocalDateTime debut;
    private LocalDateTime fin;
    private List<Salle> salles;

    public Horaire(){

    }
    public Horaire(LocalDateTime debut, LocalDateTime fin, List<Salle> salles) {
        this.debut = debut;
        this.fin = fin;
        this.salles = salles;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getDebut() {
        return debut;
    }

    public void setDebut(LocalDateTime debut) {
        this.debut = debut;
    }

    public LocalDateTime getFin() {
        return fin;
    }

    public void setFin(LocalDateTime fin) {
        this.fin = fin;
    }

    public List<Salle> getSalles() {
        return salles;
    }

    public void setSalle(List<Salle> salles) {
        this.salles = salles;
    }

}