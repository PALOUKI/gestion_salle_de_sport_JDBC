package entite;

import java.util.ArrayList;
import java.util.List;

public class Equipement {

    private int id;
    private String libelle;
    private String description;
    private Salle salle;

    public Equipement(){

    }

    public Equipement(String libelle, String description, Salle salle) {
        this.libelle = libelle;
        this.description = description;
        this.salle = salle;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Salle getSalle() {
        return salle;
    }

    public void setSalle(Salle salle) {
        this.salle = salle;
    }

}
