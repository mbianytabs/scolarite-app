package scolarite.view;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TreeCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import scolarite.model.Niveau;

class NiveauTreeCell extends TreeCell<Niveau> {
    @Override
    protected void updateItem(Niveau niveau, boolean empty) {
        super.updateItem(niveau, empty);
        if (empty || niveau == null) {
            setText(null);
            setGraphic(null);
            return;
        }
        Label badge = new Label(Apparence.initiales(niveau.getType()));
        badge.getStyleClass().add("tree-badge");
        badge.setStyle("-fx-background-color: " + Apparence.couleur(niveau.getType()) + ";");

        Label nom = new Label(niveau.getNom());
        nom.getStyleClass().add("tree-name");

        Region espace = new Region();
        HBox.setHgrow(espace, Priority.ALWAYS);

        Label compteur = new Label(String.valueOf(niveau.getNombreUEs()));
        compteur.getStyleClass().add("tree-count");
        
        HBox ligne = new HBox(10, badge, nom, espace, compteur);
        ligne.setAlignment(Pos.CENTER_LEFT);
        setText(null);
        setGraphic(ligne);
    }
}