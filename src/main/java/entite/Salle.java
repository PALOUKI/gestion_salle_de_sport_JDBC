package entite;

import java.util.List;

public class Salle {

    private int id;
    private String libelle;
    private String description;
    private List<Seance> seances;
    private List<Equipement> equipements;
    private List<Horaire> horaires;

    public Salle() {}
    public Salle(String libelle, String description) {
        this.libelle = libelle;
        this.description = description;
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

    public List<Seance> getSeances() {
        return seances;
    }

    public void setSeances(List<Seance> seances) {
        this.seances = seances;
    }

    public List<Equipement> getEquipements() {
        return equipements;
    }

    public void setEquipements(List<Equipement> equipements) {
        this.equipements = equipements;
    }

    public List<Horaire> getHoraires() {
        return horaires;
    }

    public void setHoraires(List<Horaire> horaires) {
        this.horaires = horaires;
    }

    @Override
    public String toString() {
        return  libelle ;
    }

}

