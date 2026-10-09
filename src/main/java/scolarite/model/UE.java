package scolarite.model;

/**
 * Classe représentant une Unité d'Enseignement (UE).
 */
public class UE {
  private final String nom;
  private final String code;


    public UE(String nom, String code) {
        this.nom = nom;
        this.code = code;
    }

    /**
     * Retourne le nom de l'UE.
     *
     * @return le nom de l'UE
     */
    public String getNom() {
        return nom;
    }

    /**
     * Retourne le code de l'UE.
     *
     * @return le code de l'UE
     */
    public String getCode() {
        return code;
    }

    /**
     * Retourne une représentation sous forme de chaîne de caractères de l'UE.
     *
     * @return une chaîne de caractères représentant l'UE
     */
    @Override
    public String toString() {
        return nom + " (" + code + ")";
    }
}

