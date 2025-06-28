package entite;

import java.util.ArrayList;
import java.util.List;

public class MoyenDePaiement {

    private String code;
    private String libelle;
    private List<Paiement> paiements = new ArrayList<>();

    public MoyenDePaiement() {
        // Default constructor
    }
    public MoyenDePaiement(String code, String libelle) {
        this.code = code;
        this.libelle = libelle;

    }

    public String getCode() {
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

    public List<Paiement> getPaiements() {
        return paiements;
    }

    public void setPaiements(List<Paiement> paiements) {
        this.paiements = paiements;
    }

    @Override
    public String toString() {
        return code;
    }

}

