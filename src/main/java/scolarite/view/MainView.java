package scolarite.view;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.*;
import javafx.util.Duration;
import scolarite.model.GroupeUE;
import scolarite.model.Niveau;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Main view of the application: header, navigation tree, central content area and status bar.
 */
public class MainView {

    /**
     * One row of the UE table.
     * @param code         the UE code.
     * @param nom          the UE name.
     * @param rattachement the name of the UE group the UE belongs to.
     * @param mutualisee   true if the UE is shared between several groups of the level.
     */
    public record LigneUE(String code, String nom, String rattachement, boolean mutualisee) {}

    private final BorderPane racine = new BorderPane();
    private final TreeView<Niveau> arbre = new TreeView<>();
    private final TextField recherche = new TextField();
    private final Button boutonTexte = new Button("Présentation texte");
    private final Button boutonTheme = new Button("☾  Sombre");
    private final VBox contenu = new VBox(22);
    private final Label statut = new Label();
    private Consumer<Niveau> surNavigation = n -> {};

    /**
     * Builds the main view of the application by creating the graphical components
     * and placing them in the root layout.
     */
    public MainView() {
        racine.getStyleClass().add("app");
        racine.setTop(creerEntete());
        racine.setLeft(creerBarreLaterale());
        racine.setCenter(creerZoneCentrale());
        racine.setBottom(creerBarreStatut());
    }

    //----------------------------------------------- LAYOUT

    /**
     * Creates the application header, containing the logo, the title, the search bar and the buttons.
     * @return a Parent representing the header.
     */
    private Parent creerEntete() {
        Label logo = new Label("UL");
        logo.getStyleClass().add("logo");

        Label titre = new Label("Offre de formation");
        titre.getStyleClass().add("app-title");
        Label sousTitre = new Label("Scolarité · Université de Lorraine");
        sousTitre.getStyleClass().add("app-subtitle");
        VBox textes = new VBox(2, titre, sousTitre);

        Region espace = new Region();
        HBox.setHgrow(espace, Priority.ALWAYS);

        recherche.setPromptText("Rechercher un niveau, une UE, un code...");
        recherche.getStyleClass().add("search");
        recherche.setPrefWidth(320);

        boutonTexte.getStyleClass().addAll("btn", "btn-primary");
        boutonTheme.getStyleClass().addAll("btn", "btn-ghost");

        HBox entete = new HBox(14, logo, textes, espace, recherche, boutonTexte, boutonTheme);
        entete.setAlignment(Pos.CENTER_LEFT);
        entete.getStyleClass().add("header");
        return entete;
    }

    /**
     * Creates the application sidebar, containing the title and the navigation tree.
     * @return a Parent representing the sidebar.
     */
    private Parent creerBarreLaterale() {
        Label titre = new Label("STRUCTURE");
        titre.getStyleClass().add("sidebar-title");

        arbre.setCellFactory(t -> new NiveauTreeCell());
        arbre.setShowRoot(true);
        arbre.getStyleClass().add("level-tree");
        VBox.setVgrow(arbre, Priority.ALWAYS);

        VBox barre = new VBox(10, titre, arbre);
        barre.getStyleClass().add("sidebar");
        barre.setPrefWidth(330);
        return barre;
    }

    /**
     * Creates the central area of the application: the main content wrapped in a scroll pane.
     * @return a Parent representing the central area.
     */
    private Parent creerZoneCentrale() {
        contenu.getStyleClass().add("content");

        ScrollPane defilement = new ScrollPane(contenu);
        defilement.setFitToWidth(true);
        defilement.getStyleClass().add("content-scroll");
        return defilement;
    }

    /**
     * Creates the application status bar, used to display messages about the current state.
     * @return a Parent representing the status bar.
     */
    private Parent creerBarreStatut() {
        statut.getStyleClass().add("status-text");

        HBox barre = new HBox(statut);
        barre.getStyleClass().add("status-bar");
        return barre;
    }

    //----------------------------------------------- DISPLAY

    /**
     * Displays the navigation tree in the sidebar.
     * @param racineArbre the root of the tree to display.
     */
    public void afficherArbre(TreeItem<Niveau> racineArbre) {
        arbre.setRoot(racineArbre);
    }

    /**
     * Displays the details of the given level in the central area:
     * breadcrumb, title, statistics, sub-levels (if any) and the UE table.
     * @param niveau the level whose details must be displayed.
     */
    public void afficherDetails(Niveau niveau) {
        contenu.getChildren().setAll(creerFilAriane(niveau), creerTitre(niveau), creerCartesStatistiques(niveau));

        if (!niveau.regroupeDesUEs()) {
            contenu.getChildren().addAll(titreSection("Sous-niveaux", niveau.getEnfants().size()), creerCartesEnfants(niveau));
        }

        List<LigneUE> lignes = lignesUE(niveau);
        contenu.getChildren().addAll(titreSection("Unités d'enseignement", lignes.size()), creerTableUE(lignes));
        animerEntree();
    }

    /**
     * Creates the breadcrumb for the given level, showing the navigation path from the root to the current level.
     * Every step except the current one is clickable and navigates to that level.
     * @param niveau the level for which the breadcrumb must be created.
     * @return a Parent representing the breadcrumb.
     */
    private Parent creerFilAriane(Niveau niveau) {
        HBox fil = new HBox(6);
        fil.setAlignment(Pos.CENTER_LEFT);

        List<Niveau> chemin = niveau.getChemin();
        for (int i = 0; i < chemin.size(); i++) {
            Niveau etape = chemin.get(i);
            Label lien = new Label(etape.getNom());
            lien.getStyleClass().add(i == chemin.size() - 1 ? "crumb-current" : "crumb");
            if (etape != niveau) lien.setOnMouseClicked(e -> surNavigation.accept(etape));
            fil.getChildren().add(lien);

            // Add a separator between two consecutive steps
            if (i < chemin.size() - 1) {
                Label sep = new Label("›");
                sep.getStyleClass().add("crumb-sep");
                fil.getChildren().add(sep);
            }
        }
        return fil;
    }

    /**
     * Creates the page title for the given level, showing its type (as a colored chip) and its name.
     * @param niveau the level for which the title must be created.
     * @return a Parent representing the title.
     */
    private Parent creerTitre(Niveau niveau) {
        Label type = new Label(niveau.getType().toUpperCase());
        type.getStyleClass().add("type-chip");
        type.setStyle("-fx-background-color: " + Apparence.couleur(niveau.getType()) + ";");

        Label titre = new Label(niveau.getNom());
        titre.getStyleClass().add("page-title");
        return new VBox(8, type, titre);
    }

    /**
     * Creates the statistics cards for the given level: contact, number of distinct UEs,
     * and either the number of UE codes or the number of sub-levels.
     * @param niveau the level for which the statistics cards must be created.
     * @return a Parent representing the statistics cards.
     */
    private Parent creerCartesStatistiques(Niveau niveau) {
        String origine = niveau.contactEstHerite() && niveau.getSourceContact() != null
                ? "Hérité de " + niveau.getSourceContact().getNom()
                : "Défini sur ce niveau";

        // Number of UEs that appear in more than one group (shared UEs)
        long mutualisees = niveau.getOccurrencesUEs().values().stream().filter(codes -> codes.size() > 1).count();
        String detailUEs = mutualisees == 0
                ? "Aucun doublon dans ce niveau"
                : mutualisees + " UE(s) mutualisée(s) comptée(s) une fois";

        // Third card: UE codes for a level grouping UEs, sub-levels otherwise
        Parent troisieme = niveau.regroupeDesUEs()
                ? carte("Codes UE", String.valueOf(niveau.getUEs().size()),
                        "Modules de ce " + niveau.getType().toLowerCase(), "#7c3aed")
                : carte("Sous-niveaux", String.valueOf(niveau.getEnfants().size()),
                        niveau.getGroupesUE().size() + " groupe(s) d'UEs au total", "#059669");

        HBox cartes = new HBox(16,
                carte("Contact", niveau.getContact(), origine, "#0284c7"),
                carte("UEs distinctes", String.valueOf(niveau.getNombreUEs()), detailUEs, "#4f46e5"),
                troisieme);
        cartes.getChildren().forEach(c -> HBox.setHgrow(c, Priority.ALWAYS));
        return cartes;
    }

    /**
     * Creates an information card with a title, a value, a detail and an accent color.
     * @param titre  the title of the card.
     * @param valeur the main value displayed on the card.
     * @param detail the additional detail displayed on the card.
     * @param accent the accent color of the card (as a hexadecimal string).
     * @return a Region representing the information card.
     */
    private Region carte(String titre, String valeur, String detail, String accent) {
        Region barre = new Region();
        barre.getStyleClass().add("stat-accent");
        barre.setStyle("-fx-background-color: " + accent + ";");

        Label t = new Label(titre.toUpperCase());
        t.getStyleClass().add("stat-label");
        Label v = new Label(valeur);
        v.getStyleClass().add("stat-value");
        Label d = new Label(detail);
        d.getStyleClass().add("stat-detail");
        d.setWrapText(true);

        VBox carte = new VBox(6, barre, t, v, d);
        carte.getStyleClass().add("stat-card");
        carte.setMaxWidth(Double.MAX_VALUE);
        carte.setPrefWidth(220);
        return carte;
    }

    /**
     * Creates a section header made of a title and a counter, laid out horizontally.
     * @param texte  the title of the section.
     * @param nombre the number displayed next to the title.
     * @return a Parent representing the section header.
     */
    private Parent titreSection(String texte, int nombre) {
        Label titre = new Label(texte);
        titre.getStyleClass().add("section-title");
        Label n = new Label(String.valueOf(nombre));
        n.getStyleClass().add("section-count");

        HBox ligne = new HBox(10, titre, n);
        ligne.setAlignment(Pos.CENTER_LEFT);
        return ligne;
    }

    /**
     * Creates a grid of clickable cards, one per child of the given level.
     * Each card shows the child's badge, name, type, number of UEs and contact.
     * @param niveau the level whose children must be displayed.
     * @return a Parent representing the grid of child cards.
     */
    private Parent creerCartesEnfants(Niveau niveau) {
        FlowPane grille = new FlowPane(14, 14);

        for (Niveau enfant : niveau.getEnfants()) {
            Label badge = new Label(Apparence.initiales(enfant.getType()));
            badge.getStyleClass().add("child-badge");
            badge.setStyle("-fx-background-color: " + Apparence.couleur(enfant.getType()) + ";");

            Label nom = new Label(enfant.getNom());
            nom.getStyleClass().add("child-name");
            Label type = new Label(enfant.getType());
            type.getStyleClass().add("child-type");

            VBox textes = new VBox(2, nom, type);
            HBox haut = new HBox(12, badge, textes);
            haut.setAlignment(Pos.CENTER_LEFT);

            Label infos = new Label(enfant.getNombreUEs() + " UEs  ·  Contact " + enfant.getContact());
            infos.getStyleClass().add("child-info");

            VBox carte = new VBox(12, haut, infos);
            carte.getStyleClass().add("child-card");
            carte.setPrefWidth(250);
            carte.setOnMouseClicked(e -> surNavigation.accept(enfant));
            grille.getChildren().add(carte);
        }
        return grille;
    }

    /**
     * Builds the rows of the UE table for the given level, one row per UE of each UE group.
     * @param niveau the level whose UEs must be listed.
     * @return the list of table rows.
     */
    private List<LigneUE> lignesUE(Niveau niveau) {
        return niveau.getGroupesUE().stream()
                .flatMap((GroupeUE g) -> g.getUEs().stream()
                        .map(ue -> new LigneUE(ue.getCode(), ue.getNom(), g.getNom(), niveau.estMutualisee(ue))))
                .toList();
    }

    /**
     * Creates the UE table (code, name, group, status) filled with the given rows.
     * The table height is adjusted to show every row without an inner scroll bar.
     * @param lignes the rows to display.
     * @return a Parent representing the UE table.
     */
    private Parent creerTableUE(List<LigneUE> lignes) {
        TableView<LigneUE> table = new TableView<>();
        table.getStyleClass().add("ue-table");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        table.getColumns().add(colonne("Code", LigneUE::code, 120));
        table.getColumns().add(colonne("Intitulé", LigneUE::nom, 220));
        table.getColumns().add(colonne("Rattachement", LigneUE::rattachement, 180));

        // "Status" column: displays a pill indicating whether the UE is shared or not
        TableColumn<LigneUE, Boolean> statutCol = new TableColumn<>("Statut");
        statutCol.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().mutualisee()));
        statutCol.setCellFactory(c -> new TableCell<>() {
            @Override
            protected void updateItem(Boolean mutualisee, boolean empty) {
                super.updateItem(mutualisee, empty);
                if (empty || mutualisee == null) {
                    setGraphic(null);
                    return;
                }
                Label pill = new Label(mutualisee ? "Mutualisée" : "Propre");
                pill.getStyleClass().addAll("pill", mutualisee ? "pill-shared" : "pill-own");
                setGraphic(pill);
            }
        });
        table.getColumns().add(statutCol);

        table.getItems().setAll(lignes);
        table.setFixedCellSize(40);
        table.setPrefHeight(46 + 40 * Math.max(lignes.size(), 1) + 2);
        table.setMinHeight(Region.USE_PREF_SIZE);
        return table;
    }

    /**
     * Creates a text column for the UE table.
     * @param titre   the column header.
     * @param valeur  the function extracting the cell value from a row.
     * @param largeur the preferred width of the column.
     * @return the created column.
     */
    private TableColumn<LigneUE, String> colonne(String titre, Function<LigneUE, String> valeur, double largeur) {
        TableColumn<LigneUE, String> col = new TableColumn<>(titre);
        col.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(valeur.apply(c.getValue())));
        col.setPrefWidth(largeur);
        return col;
    }

    /**
     * Plays the entry animation of the central content (fade-in combined with a small upward slide).
     */
    private void animerEntree() {
        FadeTransition fondu = new FadeTransition(Duration.millis(220), contenu);
        fondu.setFromValue(0);
        fondu.setToValue(1);

        TranslateTransition glisse = new TranslateTransition(Duration.millis(220), contenu);
        glisse.setFromY(8);
        glisse.setToY(0);

        new ParallelTransition(fondu, glisse).play();
    }

    /**
     * Opens a dialog showing the text presentation of the offer.
     * The "Copier" button copies the text to the system clipboard.
     * @param titre the header text of the dialog.
     * @param texte the text presentation to display.
     */
    public void afficherPresentationTexte(String titre, String texte) {
        TextArea zone = new TextArea(texte);
        zone.setEditable(false);
        zone.getStyleClass().add("mono");
        zone.setPrefSize(720, 420);

        Dialog<ButtonType> dialogue = new Dialog<>();
        dialogue.initOwner(racine.getScene().getWindow());
        dialogue.setTitle("Présentation texte");
        dialogue.setHeaderText(titre);

        ButtonType copier = new ButtonType("Copier");
        dialogue.getDialogPane().getButtonTypes().addAll(copier, ButtonType.CLOSE);
        dialogue.getDialogPane().setContent(new StackPane(zone));

        // Reuse the main window's stylesheets and theme so the dialog matches the application
        dialogue.getDialogPane().getStylesheets().addAll(racine.getScene().getStylesheets());
        dialogue.getDialogPane().getStyleClass().addAll(racine.getStyleClass());

        dialogue.showAndWait().filter(b -> b == copier).ifPresent(b -> {
            ClipboardContent c = new ClipboardContent();
            c.putString(texte);
            Clipboard.getSystemClipboard().setContent(c);
            afficherStatut("Présentation copiée dans le presse-papiers");
        });
    }

    /**
     * Displays a message in the status bar.
     * @param message the message to display.
     */
    public void afficherStatut(String message) {
        statut.setText(message);
    }

    /**
     * Applies the light or dark theme and updates the label of the theme button accordingly.
     * @param sombre true to apply the dark theme, false for the light theme.
     */
    public void appliquerTheme(boolean sombre) {
        racine.getStyleClass().remove("dark");
        if (sombre) racine.getStyleClass().add("dark");
        boutonTheme.setText(sombre ? "☀  Clair" : "☾  Sombre");
    }

    //----------------------------------------------- CONTROLLER

    /** @return the root node of the view. */
    public Parent getRacine()            { return racine; }

    /** @return the navigation tree. */
    public TreeView<Niveau> getArbre()   { return arbre; }

    /** @return the search field. */
    public TextField getRecherche()      { return recherche; }

    /** @return the "Présentation texte" button. */
    public Button getBoutonTexte()       { return boutonTexte; }

    /** @return the theme toggle button. */
    public Button getBoutonTheme()       { return boutonTheme; }

    /**
     * Sets the action executed when the user navigates to a level (breadcrumb or child card click).
     * @param action the action receiving the target level.
     */
    public void setSurNavigation(Consumer<Niveau> action) { this.surNavigation = action; }
}
