package scolarite.model;

import java.util.ArrayList;
import java.util.List;

public class GroupeUE extends Niveau {
     private final List<UE> ues = new ArrayList<>();

     /**
      * Constructeur de la classe GroupeUE.
      *
      * @param type    le type du groupe d'UE
      * @param nom     le nom du groupe d'UE
      * @param contact le contact associé au groupe d'UE (peut être null)
      */
     public GroupeUE(String type, String nom, String contact) {
         super(type, nom, contact);
     }

    /**
     * Ajoute une UE au groupe d'UE.
     * @param ue l'UE à ajouter
     * @return le groupe d'UE actuel
     */
    public GroupeUE ajouter(UE ue) {
        ues.add(ue);
        return this;
    }

    @Override
    public List<Niveau> getEnfants() {
        return List.of();
    }

    @Override
    public List<UE> getUEs() {
        return List.copyOf(ues);
    }

    @Override
    public boolean regroupeDesUEs() {
        return true;
    }

    @Override
    public List<GroupeUE> getGroupesUE() {
        return List.of(this);
    }
}
