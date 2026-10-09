package scolarite.model;

public final class OffreFormation {
    private OffreFormation() {}

    public static NiveauComposite exemple() {
        UE acl = new UE("ACL", "UE1-ACL");
        UE dp1 = new UE("DP",  "UE2-DP");
        UE dp2 = new UE("DP",  "UE3-DP");
        UE ia  = new UE("IA",  "UE4-IA");
        UE ihm = new UE("IHM", "UE5-IHM");
        UE sa  = new UE("Sécurité Applis",  "UE6-SA");
        UE sr  = new UE("Sécurité Réseaux", "UE7-SR");
        UE gla = new UE("GLA", "UE8-GLA");


        GroupeUE s7 = new GroupeUE("Semestre", "Semester S7", "FL").ajouter(acl).ajouter(dp1);
        GroupeUE s8 = new GroupeUE("Semestre", "Semester S8", "FL").ajouter(ia).ajouter(ihm);
        GroupeUE s9 = new GroupeUE("Semestre", "Semester S9", "FL").ajouter(gla).ajouter(dp2);
        GroupeUE du = new GroupeUE("DU", "DU Sécurité", "SD").ajouter(sa).ajouter(sr);
       
        NiveauComposite masterInfo = new NiveauComposite("Master", "Master Info", "HC").ajouter(s7).ajouter(s8).ajouter(s9);
        
        NiveauComposite informatique = new NiveauComposite("Discipline", "Informatique", null).ajouter(masterInfo).ajouter(du);
       
        NiveauComposite fst = new NiveauComposite("Collégium", "Collegium FST", "SF").ajouter(informatique);
        
        return new NiveauComposite("Université", "Université de Lorraine", "UL").ajouter(fst);
    }
}