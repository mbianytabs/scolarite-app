package scolarite.model;

import java.util.*;

public abstract class Niveau {
    private final String type;  //collegium, master,semestre (just a label)
    private final String nom;
    private final String contact; //can be null: contact is inherited
    private Niveau parent; //null for root

    /**
     * Constructeur de la classe Niveau.
     *
     * @param type    le type du niveau (collegium, master, semestre)
     * @param nom     le nom du niveau
     * @param contact le contact associé au niveau (peut être null)
     */
    protected Niveau(String type, String nom, String contact) {
        this.type = type;
        this.nom = nom;
        this.contact = contact;
    }

    /**
     * Retourne le type du niveau.
     * @return le type du niveau
     */
    public String getType(){ 
        return type; 
    }

    /**
     * Retourne le nom du niveau.
     * @return le nom du niveau
     */
    public String getNom(){
        return nom; 
    }

    /**
     * Retourne le parent du niveau.
     * @return le parent du niveau
     */
    public Niveau getParent(){
        return parent; 
    }

    /**
     * Définit le parent du niveau.
     * @param parent le parent du niveau
     */
    void setParent(Niveau parent){
        this.parent = parent; 
    }

    // --- CONTACT METHODS ---

    /**
     * Retourne le niveau source du contact.
     * @return le niveau source du contact
     */
    public Niveau getSourceContact(){
        if(contact != null){
            return this;
        } else if(parent != null){
            return parent.getSourceContact();
        } else {
            return null;
        }
    }

    /**
     * Retourne le contact associé au niveau.
     * @return le contact associé au niveau, ou null si aucun contact n'est défini
     */
    public String getContact(){
        Niveau source = getSourceContact();
        return (source != null) ? source.contact : null;
    }

    /**
     * Détermine si le contact est hérité.
     * @return true si le contact est hérité, false sinon
     */
    public boolean contactEstHerite(){
        return getSourceContact() != this;
    }

    // ---ABSTRACT METHODS---
    /**
     * Retourne la liste des enfants du niveau.
     * empty for a GroupeUE
     * @return la liste des enfants du niveau
     */
    public abstract List<Niveau> getEnfants();

    /**
     * Retourne la liste des Unités d'Enseignement (UE) associées au niveau.
     * empty for NiveauComposite
     * @return la liste des UE associées au niveau
     */
    public abstract List<UE> getUEs();

    /**
     * Détermine si le niveau se compose de Unités d'Enseignement directement
     * e.g semester, DU
     * @return true si le niveau se compose de UE, false sinon
     */
    public abstract boolean regroupeDesUEs();

    /**
     * Retourne la liste des groupes d'UE associés au niveau.
     * empty for NiveauFeuille
     * @return la liste des groupes d'UE associés au niveau
     */
    public abstract List<GroupeUE> getGroupesUE();

    //-- DERIVED INFORMATION METHODS ---

    /**
     * Retourne un ensemble contenant les noms des Unités d'Enseignement (UE) associées au niveau.
     * @return un ensemble contenant les noms des UE associées au niveau
     */
    public Set<String> getNomUEs(){
        Set<String> noms = new LinkedHashSet<>();
        for (GroupeUE groupe : getGroupesUE()) {
            for (UE ue : groupe.getUEs()) {
                noms.add(ue.getNom());
            }
        }
        return noms;
    }

    /**
     * Retourne le nombre d'Unités d'Enseignement (UE) associées au niveau.
     * @return le nombre d'UE associées au niveau
     */
    public int getNombreUEs(){
        return getNomUEs().size();
    }

    /**
     * Retourne un dictionnaire contenant les occurrences des Unités d'Enseignement (UE) associées au niveau.
     * @return un dictionnaire contenant les occurrences des UE associées au niveau
     */
    public Map<String,List<UE>> getOccurrencesUEs(){
        Map<String,List<UE>> occurrences = new LinkedHashMap<>();
        for (GroupeUE groupe : getGroupesUE()) {
            for (UE ue : groupe.getUEs()) {
                occurrences.computeIfAbsent(ue.getNom(), k -> new ArrayList<>()).add(ue);
            }
        }
        return occurrences;
    }

    /**
     * Retourne le niveau racine de la hiérarchie des niveaux.
     * @return le niveau racine
     */
    public Niveau getRacine(){
        return parent == null ? this : parent.getRacine();
    }

    /**
     * Détermine si une Unité d'Enseignement (UE) est mutualisée dans le niveau.
     * (a shared course is one that appears in more than one group)
     * @param ue l'UE à vérifier
     * @return true si l'UE est mutualisée, false sinon
     */
    public boolean estMutualisee(UE ue){
        return getRacine().getOccurrencesUEs().getOrDefault(ue.getNom(), new ArrayList<>()).size() > 1;
    }

    /**
     * Retourne le chemin depuis le niveau racine jusqu'au niveau actuel.
     * @return une liste représentant le chemin depuis le niveau racine jusqu'au niveau actuel
     */
    public List<Niveau> getChemin(){
        List <Niveau> chemin = new ArrayList<>();
        for (Niveau n = this; n != null; n = n.parent){
            chemin.add(n);
        }
        Collections.reverse(chemin);
        return chemin;
    }

    @Override 
    public String toString(){
        return nom;
    }
}
