package scolarite;

import javafx.application.Application;
import scolarite.model.OffreFormation;
import scolarite.view.PresentationTexte;

/**
 * Real main class. It does not extend Application, so it can run in console mode
 * and IntelliJ can start it without extra JavaFX settings.
 */
public class Launcher {

    /**
     * Starts the application: prints the text presentation of the offer when "--console" is passed,
     * otherwise launches the JavaFX window.
     * @param args the command-line arguments.
     */
    public static void main(String[] args) {
        if (args.length > 0 && args[0].equals("--console")) {
            System.out.print(PresentationTexte.generer(OffreFormation.exemple()));
        } else {
            Application.launch(App.class, args);
        }
    }
}
