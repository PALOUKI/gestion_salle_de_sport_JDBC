package service;

import dao.TypeAbonnementDao;
import entite.TypeAbonnement;

import java.util.List;


public class TypeAbonnementService {

    private TypeAbonnementDao dao =  new TypeAbonnementDao();

    public void ajouter(TypeAbonnement typeAbonnement){
        dao.ajouter(typeAbonnement);

    }

    public void supprimer(TypeAbonnement typeAbonnement){
        dao.supprimer(typeAbonnement);

    }

    public void modifier(TypeAbonnement typeAbonnement){
        dao.modifier(typeAbonnement);

    }

    public TypeAbonnement trouver(String code){
        return dao.trouver(code);
    }

    public List<TypeAbonnement> listerTous(){
        return dao.listerTous();
    }




}
