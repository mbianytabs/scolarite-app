package scolarite;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import scolarite.controller.MainController;
import scolarite.model.Niveau;
import scolarite.model.OffreFormation;
import scolarite.view.MainView;

/**
 * Entry point of the JavaFX application: creates the Model, the View and the Controller, then shows the window.
 */
public class App extends Application {

    /**
     * Builds the MVC triad, creates the scene with the stylesheet and shows the main window.
     * @param stage the main window provided by JavaFX.
     */
    @Override
    public void start(Stage stage) {
        Niveau modele = OffreFormation.exemple();   // M
        MainView vue = new MainView();               // V
        new MainController(modele, vue);             // C

        Scene scene = new Scene(vue.getRacine(), 1280, 800);
        String css = App.class.getResource("/scolarite/view/style.css").toExternalForm();
        scene.getStylesheets().add(css);

        stage.setTitle("Scolarité: Offre de formation");
        stage.setMinWidth(1000);
        stage.setMinHeight(640);
        stage.setScene(scene);
        stage.show();
    }
}
