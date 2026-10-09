package scolarite.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import scolarite.view.PresentationTexte;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests of the level hierarchy (Composite pattern) built from the example training offer.
 */
class NiveauTest {

    private Niveau universite, fst, informatique, master, s7;

    /**
     * Rebuilds the example offer before each test and keeps references to the levels used in the tests.
     */
    @BeforeEach
    void setUp() {
        universite   = OffreFormation.exemple();
        fst          = universite.getEnfants().get(0);
        informatique = fst.getEnfants().get(0);
        master       = informatique.getEnfants().get(0);
        s7           = master.getEnfants().get(0);
    }

    /**
     * A level without its own contact inherits the contact of the first ancestor that has one.
     */
    @Test
    void contactHeriteDuPremierParentQuiEnAUn() {
        assertEquals("SF", informatique.getContact());
        assertTrue(informatique.contactEstHerite());
        assertSame(fst, informatique.getSourceContact());
    }

    /**
     * A level with its own contact uses it and does not inherit it.
     */
    @Test
    void contactPropreNonHerite() {
        assertEquals("HC", master.getContact());
        assertFalse(master.contactEstHerite());
    }

    /**
     * A shared UE (same name in several groups) is counted only once.
     */
    @Test
    void ueMutualiseeCompteeUneSeuleFois() {
        assertEquals(5, master.getNombreUEs());        // ACL, DP, IA, IHM, GLA
        assertEquals(7, informatique.getNombreUEs());  // + 2 UEs of the DU
        assertEquals(7, fst.getNombreUEs());
        assertEquals(2, master.getOccurrencesUEs().get("DP").size());
    }

    /**
     * DP is detected as shared even when looked at from a single semester (S7).
     */
    @Test
    void dpEstMutualiseeMemeVueDepuisUnSeulSemestre() {
        UE dpDeS7 = s7.getUEs().get(1);
        assertTrue(s7.estMutualisee(dpDeS7));
        assertFalse(s7.estMutualisee(s7.getUEs().get(0)));  // ACL
    }

    /**
     * The path of a level goes from the root down to the level itself.
     */
    @Test
    void cheminDepuisLaRacine() {
        assertEquals(List.of(universite, fst, informatique, master, s7), s7.getChemin());
    }

    /**
     * The text presentation matches the expected output given in the assignment.
     */
    @Test
    void presentationTexteConformeAuSujet() {
        String attendu = """
                Informatique - Contact: SF, nombre UEs: 7
                    Master Info - Contact: HC, nombre UEs: 5
                        Semester S7 (Contact: FL)
                            Modules: [ACL (UE1-ACL), DP (UE2-DP)]
                        Semester S8 (Contact: FL)
                            Modules: [IA (UE4-IA), IHM (UE5-IHM)]
                        Semester S9 (Contact: FL)
                            Modules: [GLA (UE8-GLA), DP (UE3-DP)]
                    DU Sécurité (Contact: SD)
                        Modules: [Sécurité Applis (UE6-SA), Sécurité Réseaux (UE7-SR)]
                """;
        assertEquals(attendu, PresentationTexte.generer(informatique));
    }

    /**
     * A new intermediate level type can be added without creating a new class,
     * and contact inheritance and UE counting still work through it.
     */
    @Test
    void ajouterUnNiveauIntermediaireSansNouvelleClasse() {
        NiveauComposite composante = new NiveauComposite("Composante", "FST Nancy", null);
        NiveauComposite collegium = new NiveauComposite("Collégium", "Collegium X", "CX").ajouter(composante);
        GroupeUE s1 = new GroupeUE("Semestre", "S1", null).ajouter(new UE("Algo", "UE1-ALGO"));
        composante.ajouter(new NiveauComposite("Discipline", "Maths", null).ajouter(s1));

        assertEquals("CX", s1.getContact());
        assertEquals(1, collegium.getNombreUEs());
    }
}
