package scolarite.controller;

import javafx.scene.control.TreeItem;
import scolarite.model.Niveau;
import scolarite.model.UE;
import scolarite.view.MainView;
import scolarite.view.PresentationTexte;

/**
 * Reacts to user actions in the view and tells the view what to display from the model.
 */
public class MainController {

    private final Niveau modele;
    private final MainView vue;
    private boolean themeSombre = false;

    /**
     * Creates the controller, connects it to the view's events, displays the full tree
     * and selects the root level.
     * @param modele the root level of the training offer (the model).
     * @param vue    the main view of the application.
     */
    public MainController(Niveau modele, MainView vue) {
        this.modele = modele;
        this.vue = vue;
        brancherEvenements();
        vue.afficherArbre(construireArbre(modele, ""));
        selectionner(modele);
    }

    /**
     * Connects the view's events to the controller: tree selection, navigation,
     * search field, text presentation button and theme button.
     */
    private void brancherEvenements() {
        // Tree selection: display the details of the selected level and update the status bar
        var selection = vue.getArbre().getSelectionModel().selectedItemProperty();
        selection.addListener((obs, ancien, item) -> {
            if (item == null) return;
            Niveau niveau = item.getValue();
            vue.afficherDetails(niveau);
            vue.afficherStatut(niveau.getType() + " « " + niveau.getNom() + " » · "
                    + niveau.getNombreUEs() + " UEs distinctes · contact "
                    + niveau.getContact());
        });

        // Navigation from the breadcrumb or a child card
        vue.setSurNavigation(this::selectionner);

        // Search: filter the tree every time the text changes
        vue.getRecherche().textProperty().addListener((obs, ancien, texte) -> filtrer(texte));

        // Text presentation of the selected level
        vue.getBoutonTexte().setOnAction(e -> {
            Niveau niveau = niveauSelectionne();
            vue.afficherPresentationTexte("Présentation à partir de : " + niveau.getNom(),
                                          PresentationTexte.generer(niveau));
        });

        // Toggle between the light and dark themes
        vue.getBoutonTheme().setOnAction(e -> {
            themeSombre = !themeSombre;
            vue.appliquerTheme(themeSombre);
        });
    }

    //----------------------------------------------- SEARCH

    /**
     * Filters the tree with the given text, selects the root of the filtered tree
     * and shows the number of matching levels in the status bar.
     * @param texte the text typed in the search field.
     */
    private void filtrer(String texte) {
        String filtre = texte == null ? "" : texte.trim().toLowerCase();
        TreeItem<Niveau> racine = construireArbre(modele, filtre);
        vue.afficherArbre(racine);

        if (racine == null) {
            vue.afficherStatut("Aucun résultat pour « " + texte + " »");
            return;
        }

        vue.getArbre().getSelectionModel().select(racine);
        if (!filtre.isEmpty()) {
            int n = compter(racine);
            vue.afficherStatut(n + " niveau(x) affiché(s) pour « " + texte + " »");
        }
    }

    /**
     * Builds the tree items of a level, keeping only the parts that match the filter.
     * When a level matches, its whole subtree is kept.
     * @param niveau the level to build the tree from.
     * @param filtre the lower-case filter ("" keeps everything).
     * @return the tree item of the level, or null when nothing in this subtree matches.
     */
    private TreeItem<Niveau> construireArbre(Niveau niveau, String filtre) {
        boolean correspond = filtre.isEmpty() || correspond(niveau, filtre);
        TreeItem<Niveau> item = new TreeItem<>(niveau);
        item.setExpanded(true);

        for (Niveau enfant : niveau.getEnfants()) {
            TreeItem<Niveau> sousArbre = construireArbre(enfant, correspond ? "" : filtre);
            if (sousArbre != null) item.getChildren().add(sousArbre);
        }
        return (correspond || !item.getChildren().isEmpty()) ? item : null;
    }

    /**
     * Tells whether a level matches the filter: its name, its type, or the name or code of one of its UEs.
     * @param niveau the level to test.
     * @param filtre the lower-case filter.
     * @return true if the level matches the filter.
     */
    private boolean correspond(Niveau niveau, String filtre) {
        if (niveau.getNom().toLowerCase().contains(filtre)) return true;
        if (niveau.getType().toLowerCase().contains(filtre)) return true;

        for (UE ue : niveau.getUEs()) {
            if (ue.getNom().toLowerCase().contains(filtre) || ue.getCode().toLowerCase().contains(filtre)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Counts the number of items in a tree (the item itself and all its descendants).
     * @param item the root of the tree to count.
     * @return the number of items in the tree.
     */
    private int compter(TreeItem<Niveau> item) {
        int n = 1;
        for (TreeItem<Niveau> enfant : item.getChildren()) n += compter(enfant);
        return n;
    }

    //----------------------------------------------- NAVIGATION

    /**
     * Selects the given level in the tree and scrolls to it.
     * If the level is hidden by the current search, the search is cleared first.
     * @param niveau the level to select.
     */
    private void selectionner(Niveau niveau) {
        TreeItem<Niveau> item = chercher(vue.getArbre().getRoot(), niveau);

        // Hidden by the search: clear it first
        if (item == null) {
            vue.getRecherche().clear();
            item = chercher(vue.getArbre().getRoot(), niveau);
        }
        if (item == null) return;

        vue.getArbre().getSelectionModel().select(item);
        vue.getArbre().scrollTo(vue.getArbre().getRow(item));
    }

    /**
     * Searches recursively for the tree item that holds the given level.
     * @param item   the root of the subtree to search in.
     * @param niveau the level to find.
     * @return the matching tree item, or null if the level is not in the subtree.
     */
    private TreeItem<Niveau> chercher(TreeItem<Niveau> item, Niveau niveau) {
        if (item == null) return null;
        if (item.getValue() == niveau) return item;

        for (TreeItem<Niveau> enfant : item.getChildren()) {
            TreeItem<Niveau> trouve = chercher(enfant, niveau);
            if (trouve != null) return trouve;
        }
        return null;
    }

    /**
     * Returns the level currently selected in the tree.
     * @return the selected level, or the root level of the model if nothing is selected.
     */
    private Niveau niveauSelectionne() {
        TreeItem<Niveau> item = vue.getArbre().getSelectionModel().getSelectedItem();
        return item == null ? modele : item.getValue();
    }
}
