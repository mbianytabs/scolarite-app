package scolarite.view;

import scolarite.model.Niveau;

public class PresentationTexte {
    private PresentationTexte() {}

    public static String generer(Niveau niveau) {
        StringBuilder sb = new StringBuilder();
        ecrire(niveau, "", sb);
        return sb.toString();
    }

    private static void ecrire(Niveau niveau, String indent, StringBuilder sb) {
        if (niveau.regroupeDesUEs()) {
            sb.append(indent)
            .append(niveau.getNom())
            .append(" (Contact: ")
            .append(niveau.getContact())
            .append(")\n");

            sb.append(indent)
            .append("    Modules: ")
            .append(niveau.getUEs())
            .append("\n");
        } 
        else {
            sb.append(indent)
            .append(niveau.getNom())
            .append(" - Contact: ")
            .append(niveau.getContact())
            .append(", nombre UEs: ")
            .append(niveau.getNombreUEs())
            .append("\n");

            for (Niveau enfant : niveau.getEnfants()){
                ecrire(enfant, indent + "    ", sb);
            }
        }
    }
}
