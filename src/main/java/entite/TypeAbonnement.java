package entite;

import java.util.ArrayList;
import java.util.List;

public class TypeAbonnement {

    private String code;
    private String libelle;
    private int montant;
    private List<Abonnement> abonnements = new ArrayList<>();

    public TypeAbonnement(){

    }

    public TypeAbonnement(String code, String libelle, int montant){
        this.code = code;
        this.libelle = libelle;
        this.montant = montant;
    }

    public String getCode() {
        return code;
    }

    @Override
    public String toString() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public int getMontant() {
        return montant;
    }

    public void setMontant(int montant) {
        this.montant = montant;
    }

    public List<Abonnement> getAbonnements() {
        return abonnements;
    }

    public void setAbonnements(List<Abonnement> abonnements) {
        this.abonnements = abonnements;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TypeAbonnement that = (TypeAbonnement) o;
        return code != null ? code.equals(that.code) : that.code == null;
    }

    @Override
    public int hashCode() {
        return code != null ? code.hashCode() : 0;
    }


}

