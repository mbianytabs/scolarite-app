package scolarite.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NiveauComposite extends Niveau {
    private final List<Niveau> enfants = new ArrayList<>();

    /**
     * Constructeur de la classe NiveauComposite.
     *
     * @param type    le type du niveau composite
     * @param nom     le nom du niveau composite
     * @param contact le contact associé au niveau composite (peut être null)
     */
    public NiveauComposite(String type, String nom, String contact) {
        super(type, nom, contact);
    }

    /**
     * Ajoute un enfant au niveau composite.
     *
     * @param enfant le niveau enfant à ajouter
     * @return le niveau composite actuel
     */
    public NiveauComposite ajouter(Niveau enfant) {
        enfants.add(enfant);
        enfant.setParent(this);
        return this;
    }

    @Override
    public List<Niveau> getEnfants() {
        return Collections.unmodifiableList(enfants);
    }

    @Override
    public List<UE> getUEs() {
        return List.of();
    }

    @Override
    public boolean regroupeDesUEs() {
        return false;
    }

    @Override
    public List<GroupeUE> getGroupesUE() {
        List<GroupeUE> groupesUE = new ArrayList<>();
        for (Niveau enfant : enfants) {
            groupesUE.addAll(enfant.getGroupesUE());
        }
        return groupesUE;
    }
    
}
