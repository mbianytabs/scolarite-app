package scolarite.view;

import java.util.Map;

final class Apparence {
    private Apparence() {}

    // Couleurs pour chaque type de niveau
    private static final Map<String, String> COULEURS = Map.of(
        "Université", "#4f46e5",
        "Collégium",  "#0284c7",
        "Discipline", "#059669",
        "Master",     "#d97706",
        "Licence",    "#ca8a04",
        "Semestre",   "#7c3aed",
        "DU",         "#e11d48");
    
        /**
         * Retourne la couleur associée à un type de niveau. 
         * @param type le type de niveau (ex: "Université", "Collégium", etc.)
         * @return la couleur correspondante sous forme de chaîne hexadécimale (ex: "#4f46e5").
         */
    static String couleur(String type) {
        return COULEURS.getOrDefault(type, "#64748b");
    }

    /**
     * Retourne les initiales associées à un type de niveau.
     * @param type le type de niveau (ex: "Université", "Collégium", etc.)
     * @return les initiales correspondantes (ex: "U" pour "Université").
     */
    static String initiales(String type) {
        if (type.equals("DU")) return "DU";
        return type.substring(0, 1).toUpperCase();
    }
}