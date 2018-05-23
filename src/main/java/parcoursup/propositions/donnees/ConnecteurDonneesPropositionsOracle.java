/* Copyright 2018, 2018 Hugo Gimbert (hugo.gimbert@enseignementsup.gouv.fr) 

    This file is part of Algorithmes-de-parcoursup.

    Algorithmes-de-parcoursup is free software: you can redistribute it and/or modify
    it under the terms of the Affero GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Algorithmes-de-parcoursup is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    Affero GNU General Public License for more details.

    You should have received a copy of the Affero GNU General Public License
    along with Algorithmes-de-parcoursup.  If not, see <http://www.gnu.org/licenses/>.

 */
package parcoursup.propositions.donnees;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import oracle.jdbc.OracleDriver;
import oracle.jdbc.pool.OracleDataSource;
import parcoursup.propositions.algo.AlgoPropositionsEntree;
import parcoursup.propositions.algo.GroupeAffectation;
import parcoursup.propositions.algo.GroupeInternat;
import parcoursup.propositions.algo.AlgoPropositionsSortie;
import parcoursup.propositions.algo.GroupeAffectationUID;
import parcoursup.propositions.algo.GroupeInternatUID;
import parcoursup.propositions.algo.VoeuEnAttente;
import parcoursup.propositions.algo.VoeuUID;


/* 
    Récupération et injection des données depuis et vers la base Oracle

    La base identifie:
    
    * chaque candidat par un G_CN_COD
    * chaque formation d'inscription par un G_TI_COD
    * chaque formation d'affectation par un G_TA_COD
    * chaque commission de classement pédagogique des voeux par un C_GP_COD
    * chaque commission de classement internat des voeux par un C_GI_COD

    Plus de détails dans doc/implementation.txt
*/
public class ConnecteurDonneesPropositionsOracle implements ConnecteurDonneesPropositions {

    /* connection a la base de donnees */
    Connection conn = null;

    public ConnecteurDonneesPropositionsOracle(String url, String user, String password) throws SQLException {
        if (url == null || url.isEmpty()) {
            OracleDriver ora = new OracleDriver();
            conn = ora.defaultConnection();
        } else {
            OracleDataSource ods = new OracleDataSource();
            ods.setURL(url);
            ods.setUser(user);
            ods.setPassword(password);
            conn = ods.getConnection();
        }
    }

    @Override
    public AlgoPropositionsEntree recupererDonnees() throws SQLException {

        log("Vérification de l'interruption du flux de données entrantes.");
        /*Si = 1 indique si le programme d'admission est en train de tourner 
        pour faire des propositions. Si c'est le cas, tout est bloqué*/
        try (ResultSet result
                = conn.createStatement().executeQuery(
                        "SELECT g_pr_val FROM g_par WHERE g_pr_cod=31")) {
            result.next();
            boolean estBloque = result.getBoolean(1);
            if (!estBloque) {
                throw new RuntimeException(
                        "Veuillez interrompre le flux de données entrantes "
                        + "et positionner le g_pr_cod=31 à 1");
            }

        }

        recupererGroupesEtInternats();

        /* permet de vérifier l'unicité des voeux
        récupérés via les deux requêtes */
        VoeuUID.debuterVerificationUnicite();

        recupererVoeuxAvecInternatsAClassementPropre();

        recupererVoeuxSansInternatAClassementPropre();

        if (!groupesManquants.isEmpty()) {
            System.out.println(groupesManquants.size() + " groupes manquants.");
        }

        if (!internatsManquants.isEmpty()) {
            System.out.println(internatsManquants.size() + " internats manquants.");
        }

        log("Fin de la récupération des données depuis la base Oracle");

        AlgoPropositionsEntree entree = new AlgoPropositionsEntree();

        entree.groupesAffectations.addAll(groupesAffectations.values());
        entree.internats.addAll(internats.values());
        return entree;

    }

    /* exportation des résultats du calcul: propositions à faire 
    et barres internats */
    @Override
    public void exporterDonnees(AlgoPropositionsSortie sortie) throws SQLException {

        conn.setAutoCommit(false);

        log("Préparation des tables avant export");
        conn.createStatement().executeQuery(
                "DELETE FROM A_ADM_PROP WHERE NB_JRS=" + GroupeInternat.nbJoursCampagne);
        conn.createStatement().executeQuery(
                "DELETE FROM A_ADM_POSITIONS_INT WHERE NB_JRS=" + GroupeInternat.nbJoursCampagne);
 
        log("Exportation des propositions d'admission");

        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO A_ADM_PROP "
                + "(G_CN_COD,G_TA_COD,I_RH_COD,C_GP_COD,G_TI_COD,C_GI_COD,NB_JRS)"
                + " VALUES (?,?,?,?,?,?," + GroupeInternat.nbJoursCampagne + ")")) {
            int count = 0;

            for (VoeuEnAttente voe : sortie.propositions) {
                GroupeAffectationUID groupe = voe.groupe.id;
                GroupeInternatUID internat = voe.internatID();
                
                assert voe.id.G_TA_COD == groupe.G_TA_COD;

                ps.setInt(1, voe.id.G_CN_COD);
                ps.setInt(2, voe.id.G_TA_COD);
                ps.setInt(3, voe.id.I_RH_COD ? 1 : 0);
                ps.setInt(4, groupe.C_GP_COD);
                ps.setInt(5, groupe.G_TI_COD);
                if (internat == null) {
                    ps.setNull(6, java.sql.Types.INTEGER);
                } else {
                    ps.setInt(6, internat.C_GI_COD);
                }

                ps.addBatch();
                if (++count % 500000 == 0) {
                    log("Exportation des propositions " + (count - 499999) + " a " + count);
                    ps.executeBatch();
                    ps.clearBatch();
                    log("Fait");
                }
            }

            ps.executeBatch();
            log(count + " propositions exportées.");
            
        }

        conn.commit();

        log("Exportation des positions d'admission aux internats");

        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO A_ADM_POSITIONS_INT "
                + "(C_GI_COD,G_TA_COD,POS_ADM,POS_MAX_ADM,CONTINGENT_ADM,NB_JRS)"
                + " VALUES (?,?,?,?,?," + GroupeInternat.nbJoursCampagne + " )")) {

            for (GroupeInternat internat : sortie.internats) {
                ps.setInt(1, internat.id.C_GI_COD);
                ps.setInt(2, internat.id.G_TA_COD);
                ps.setInt(3, internat.positionAdmission);
                ps.setInt(4, internat.positionMaximaleAdmission);
                ps.setInt(5, internat.contingentAdmission);
                ps.addBatch();
            }
            ps.executeBatch();
        }

        conn.commit();

    }
    /* les internats ¨*/
    private final Map<GroupeInternatUID, GroupeInternat> internats
            = new HashMap<>();

    /* les groupes d'affectation */
    private final Map<GroupeAffectationUID, GroupeAffectation> groupesAffectations
            = new HashMap<>();

    /* permet de comptabiliser les internats  manquants, avant le début de campagne */
    private final Set<GroupeInternatUID> internatsManquants
            = new HashSet<>();

    /* permet de comptabiliser les groupes manquants, avant le début de campagne */
    Set<GroupeAffectationUID> groupesManquants
            = new HashSet<>();

    private void recupererGroupesEtInternats() throws SQLException {
        internats.clear();
        groupesAffectations.clear();
      
        log("Récupération des groupes d'affectation");
        try (Statement stmt = conn.createStatement()) {
            stmt.setFetchSize(1000000);
            try (ResultSet result = stmt.executeQuery(
                    "SELECT C_GP_COD, G_TI_COD, G_TA_COD,"
                    + " A_RG_NBR_SOU, NVL(a_rg_ran_lim,0)"
                    + " FROM a_rec_grp")) {

                while (result.next()) {

                    int C_GP_COD = result.getInt(1);
                    int G_TI_COD = result.getInt(2);
                    int G_TA_COD = result.getInt(3);
                    int capacite = result.getInt(4);
                    int rangLimite = result.getInt(5);/* peut etre null, vaut 0 dans ce cas */

                    GroupeAffectationUID id
                            = new GroupeAffectationUID(C_GP_COD, G_TI_COD, G_TA_COD);
                    if (groupesAffectations.containsKey(id)) {
                        throw new RuntimeException("GroupeAffectation dupliqué");
                    }

                    groupesAffectations.put(id,
                            new GroupeAffectation(capacite, id, rangLimite)
                    );

                }
            }
        }

        log("Récupération des internats");
        try (Statement stmt = conn.createStatement()) {
            stmt.setFetchSize(1000000);

            try (ResultSet result = stmt.executeQuery(
                    "SELECT C_GI_COD, NVL(G_TA_COD,0), A_RI_NBR_SOU, NVL(A_RI_PCT_OUV,100) "
                    + "FROM a_rec_grp_int")) {
                while (result.next()) {
                    int C_GI_COD = result.getInt(1);
                    int G_TA_COD = result.getInt(2);
                    int nbPlacesTotal = result.getInt(3);
                    int pourcentageOuverture = result.getInt(4);

                    GroupeInternatUID id
                            = new GroupeInternatUID(C_GI_COD, G_TA_COD);

                    if (internats.containsKey(id)) {
                        throw new RuntimeException("Internat dupliqué");
                    }

                    internats.put(id, new GroupeInternat(
                            id,
                            nbPlacesTotal,
                            pourcentageOuverture
                    )
                    );

                }
            }
        }

        log("Récupération du nombre de jours écoulés depuis l'ouverture de la campagne");
        try (ResultSet result
                = conn.createStatement().executeQuery(
                        "SELECT TRUNC(SYSDATE) - TRUNC(TO_DATE(g_pr_val, 'DD/MM/YYYY:HH24'))+1"
                        + " FROM g_par WHERE g_pr_cod=35")) {
            result.next();
            int nb = result.getInt(1);
            if (nb < 1) {
                throw new RuntimeException("Date incohérente");
            }
            GroupeInternat.nbJoursCampagne = nb;
        }

    }

    private void recupererVoeuxAvecInternatsAClassementPropre() throws SQLException {

        int compteur = 0;

        log("Récupération des voeux avec demande internat dans un internat à classement propre");
        try (Statement stmt = conn.createStatement()) {
            stmt.setFetchSize(1000000);
            String requete
                    = "SELECT "
                    + "v.g_cn_cod,"//id candidat
                    + "v.g_ta_cod,"//id affectation
                    + "ti.g_ti_cod,"//id inscription
                    + "cg.c_gp_cod,"//groupe de classement pédagogique
                    + "cg.c_cg_ord_app,"//ordre d'appel 
                    + "cgi.c_gi_cod,"//id internat

                    + "cgi.c_ci_ran,"//rang de classement internat
                    + "ti.g_ti_cla_int_uni,"//type d'internat (cf notes ci-dessous)
                    + "sv.a_sv_flg_aff,"
                    + "sv.a_sv_flg_att"
                    + " FROM "
                    + "g_can c,"//candidats
                    + "a_voe v,"//voeux
                    + "a_sit_voe sv,"//codes situations des voeux
                    + "a_rec_grp rg,"//groupes de classement pédagogique
                    + "c_can_grp cg,"//classements pédgogiques
                    + "a_rec_grp_int rgi,"//groupes de classement internats
                    + "c_can_grp_int cgi,"//classements internats
                    + "g_tri_ins ti"//données formations inscriptions
                    + " WHERE "
                    + " v.i_rh_cod=1"//voeux avec internat
                    + " AND cg.i_ip_cod=5" //candidat classé
                    + " AND c.g_ic_cod >= 0" //dossier non-annulé (décès...)
                    + " AND ti.g_ti_eta_cla=2" //classement terminé
                    + " AND (sv.a_sv_flg_aff=1 OR sv.a_sv_flg_att=1)"//en attente ou affecte
                    + " AND c.g_cn_cod=v.g_cn_cod"
                    + " AND cg.c_cg_ord_app is not null"
                    + " AND v.a_sv_cod=sv.a_sv_cod"
                    + " AND v.g_cn_cod=cg.g_cn_cod"
                    + " AND cg.c_gp_cod=rg.c_gp_cod"
                    + " AND rg.g_ta_cod=v.g_ta_cod"
                    + " AND rg.g_ti_cod=ti.g_ti_cod"
                    + " AND v.g_cn_cod=cgi.g_cn_cod"
                    + " AND cgi.c_gi_cod=rgi.c_gi_cod"
                    + " AND "
                    + "( "
                    + "   (rgi.g_ta_cod is null AND rgi.g_ea_cod_ins=ti.g_ea_cod_ins AND rg.g_ti_cod=ti.g_ti_cod) " //internat par établissement -> un seul groupe de classement
                    + "   OR "
                    + "   (rgi.g_ta_cod is not null AND rgi.g_ta_cod=v.g_ta_cod AND rgi.g_ti_cod=ti.g_ti_cod)" //internat par formation
                    + ")"
                    + "AND ti.g_ti_cla_int_uni IN (0,1)" //restriction aux internats à classement propre
                    ;

            log(requete);

            try (ResultSet result = stmt.executeQuery(requete)) {
                while (result.next()) {
                    int G_CN_COD = result.getInt(1);
                    int G_TA_COD = result.getInt(2);
                    int G_TI_COD = result.getInt(3);
                    int C_GP_COD = result.getInt(4);
                    int ordreAppel = result.getInt(5);
                    int C_GI_COD = result.getInt(6);
                    int C_CI_RAN = result.getInt(7);
                    int type_internat = result.getInt(8);
                    /*
                    g_ti_cla_int_uni = 3 : internat obligatoire
                    g_ti_cla_int_uni = 2 : internat sans élection
                    g_ti_cla_int_uni = -1 : pas d'internat
                    g_ti_cla_int_uni = 0 : l'internat est par formation
                    g_ti_cla_int_uni = 1 :  l'internat est commun 
                                            à plusieurs formations de l'établissement
                     */
                    boolean estAffecte = result.getBoolean(9);
                    boolean estEnAttente = result.getBoolean(10);

                    GroupeAffectationUID groupeId
                            = new GroupeAffectationUID(C_GP_COD, G_TI_COD, G_TA_COD);
                    GroupeAffectation groupe
                            = groupesAffectations.getOrDefault(groupeId, null);

                    if (groupe == null) {
                        /* peut arriver si les classements ou données d'appel ne sont pas renseignées */
                        groupesManquants.add(groupeId);
                        continue;
                    }

                    if (estAffecte) {
                        groupe.ajouterCandidatAffecte(G_CN_COD);
                    }

                    /* on distingue le cas des établissements avec internat unique propre à plusieurs
                    formations et celui des établissements avec des internats propres à chaque formation.
                    Remarque: dans les deux cas les internats peuvent être mixtes ou non, cf doc de ref.
                     */
                    boolean internatUnique = (type_internat == 1);
                    GroupeInternatUID internatId
                            = new GroupeInternatUID(C_GI_COD, internatUnique ? 0 : G_TA_COD);
                    GroupeInternat internat = internats.getOrDefault(internatId, null);
                    if (internat == null) {
                        internatsManquants.add(internatId);
                        continue;
                    }

                    assert !estAffecte || !estEnAttente;
                    assert estAffecte || estEnAttente;

                    if (estAffecte) {
                        internat.ajouterCandidatAffecte(G_CN_COD);
                    }
                    if (estEnAttente) {
                        VoeuEnAttente.ajouterVoeu(
                                G_CN_COD,
                                groupe,
                                ordreAppel,
                                internat,
                                C_CI_RAN
                        );
                        compteur++;
                    }
                }
            }
        }
        log(compteur + " voeux en attente avec internat à classement propre");
    }

    private void recupererVoeuxSansInternatAClassementPropre() throws SQLException {
        int compteur = 0;
        log("Récupération des voeux sans internat, ou dans un internat sans classement propre");
        try (Statement stmt = conn.createStatement()) {
            stmt.setFetchSize(1000000);
            String requete
                    = "SELECT "
                    + "v.g_cn_cod,"//id candidat
                    + "v.g_ta_cod,"//id affectation
                    + "v.i_rh_cod,"//demande internat (1) ou pas (0)
                    + "ti.g_ti_cod,"//id inscription
                    + "cg.c_gp_cod,"//groupe de classement pédagogique
                    + "cg.c_cg_ord_app rang,"//ordre d'appel
                    + "sv.a_sv_flg_aff,"
                    + "sv.a_sv_flg_att"
                    + " FROM "
                    + "g_can c,"//candidats
                    + "a_voe v,"//voeux
                    + "a_sit_voe sv,"//codes situations des voeux
                    + "a_rec_grp rg,"//groupes de classement pédagogique
                    + "c_can_grp cg,"//classements pédgogiques
                    + "g_tri_ins ti"//données formations inscriptions
                    + " WHERE "
                    + " cg.i_ip_cod=5" //candidat classé
                    + " AND c.g_ic_cod >= 0" //dossier non-annulé (décès...)
                    + " AND ti.g_ti_eta_cla=2" //classement terminé
                    + " AND (sv.a_sv_flg_aff =1 OR sv.a_sv_flg_att=1)"//en attente ou affecte
                    + " AND cg.C_CG_ORD_APP is not null"
                    + " AND c.g_cn_cod=v.g_cn_cod"
                    + " AND v.a_sv_cod=sv.a_sv_cod"
                    + " AND v.g_cn_cod=cg.g_cn_cod"
                    + " AND cg.c_gp_cod=rg.c_gp_cod"
                    + " AND rg.g_ta_cod=v.g_ta_cod"
                    + " AND rg.g_ti_cod=ti.g_ti_cod"
                    //exclut les formations d'inscriptions avec internat à classemnt propre
                    + " AND (v.i_rh_cod =0 or ti.g_ti_cla_int_uni NOT IN (0,1))";

            log(requete);

            try (ResultSet result = stmt.executeQuery(requete)) {
                while (result.next()) {
                    int G_CN_COD = result.getInt(1);
                    int G_TA_COD = result.getInt(2);
                    boolean avecInternat = result.getBoolean(3);
                    int G_TI_COD = result.getInt(4);
                    int C_GP_COD = result.getInt(5);
                    int ordreAppel = result.getInt(6);
                    boolean estAffecte = result.getBoolean(7);
                    boolean estEnAttente = result.getBoolean(8);

                    GroupeAffectationUID groupeId
                            = new GroupeAffectationUID(C_GP_COD, G_TI_COD, G_TA_COD);
                    GroupeAffectation groupe
                            = groupesAffectations.getOrDefault(groupeId, null);

                    if (groupe == null) {
                        /* peut arriver si les classements ou données d'appel ne sont pas renseignées */
                        groupesManquants.add(groupeId);
                        continue;
                    }
                    if (estAffecte) {
                        groupe.ajouterCandidatAffecte(G_CN_COD);
                    }

                    if (estEnAttente) {
                        VoeuEnAttente.ajouterVoeu(
                                G_CN_COD,
                                avecInternat,
                                groupe,
                                ordreAppel
                        );
                        compteur++;
                    }
                }
            }
        }
        log(compteur + " voeux en attente sans internat a classement propre");
    }

    private void log(String message) {
        System.out.println(LocalDateTime.now().toLocalTime() + ": " + message);
    }

}
