
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
package parcoursup.ordreappel.donnees;

import parcoursup.ordreappel.algo.AlgoOrdreAppelEntree;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import oracle.jdbc.OracleDriver;
import oracle.jdbc.pool.OracleDataSource;
import parcoursup.ordreappel.algo.AlgoOrdreAppelSortie;
import parcoursup.ordreappel.algo.GroupeClassement;
import parcoursup.ordreappel.algo.OrdreAppel;
import parcoursup.ordreappel.algo.VoeuClasse;

public class ConnecteurDonneesAppelOracle implements ConnecteurDonneesAppel {

    /* connection à la base de données */
    Connection conn = null;

    public ConnecteurDonneesAppelOracle(String url, String user, String password) throws SQLException {
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

    /* chargement des classements depuis la base de donnéees */
    @Override
    public AlgoOrdreAppelEntree recupererDonneesOrdreAppel() throws SQLException {

        Map<Integer, GroupeClassement> groupesClassements
                = new HashMap<>();

        try (Statement stmt = conn.createStatement()) {

            /* récupère la liste des groupes et les taux minimum de boursiers 
            et de résidents depuis la base de données */
            log("Récupération des groupes");
            stmt.setFetchSize(1000000);

            String sql
                    = "SELECT DISTINCT "
                    //groupe de classement
                    + "rg.C_GP_COD,"
                    //flag taux de bousier 0/null = non 1=oui
                    + "NVL(r.A_RC_FLG_TAU_BRS,0),"
                    //taux min de boursier
                    + "NVL(r.A_RC_TAU_BRS_REC,0),"
                    //flag taux max de non-resident 0/null = non 1=oui
                    + "NVL(A_RC_FLG_TAU_NON_RES,0),"
                    //taux max de non-resident
                    + "NVL(r.A_RC_TAU_NON_RES_REC, 100)"
                    + " FROM a_rec_grp rg,"
                    + " a_rec r,"
                    + " g_tri_ins ti,"
                    + " g_for fr,"
                    + " g_fil fl,"
                    + " c_grp g "
                    + " WHERE rg.g_ti_cod=r.g_ti_cod "
                    + " AND   rg.g_ta_cod=r.g_ta_cod "
                    + " AND   rg.g_ti_cod=ti.g_ti_cod "
                    + " AND   ti.g_fr_cod_ins=fr.g_fr_cod "
                    + " AND   ti.g_fl_cod_ins=fl.g_fl_cod "
                    + " AND   rg.c_gp_cod=g.c_gp_cod "
                    + " AND   g.c_gp_eta_cla=2 "
                    + " AND   ti.g_ti_flg_par_eff > 0 "
                    + " AND   ti.g_ti_eta_cla=2 "
                    + " AND   NVL(g.c_gp_flg_pas_cla, 0)!=1";

            log(sql);

            try (ResultSet result = stmt.executeQuery(sql)) {

                while (result.next()) {

                    int C_GP_COD = result.getInt(1);

                    if (groupesClassements.containsKey(C_GP_COD)) {
                        throw new RuntimeException("Groupe dupliqué " + C_GP_COD);
                    }

                    /* un ou deux taux selon le type de formation */
                    boolean tauxBoursierDisponible = result.getBoolean(2);
                    boolean tauxResDisponible = result.getBoolean(4);
                    int tauxMinBoursier = tauxBoursierDisponible ? result.getInt(3) : 0;
                    int tauxMaxNonResident = tauxResDisponible ? result.getInt(5) : 100;
                    int tauxMinResident = 100 - tauxMaxNonResident;

                    if (tauxMinBoursier < 0
                            || tauxMinBoursier > 100
                            || tauxMinResident < 0
                            || tauxMinResident > 100) {
                        throw new RuntimeException("Taux incohérents");
                    }

                    groupesClassements.put(C_GP_COD,
                            new GroupeClassement(
                                    C_GP_COD,
                                    tauxMinBoursier,
                                    tauxMinResident
                            ));

                }
            }
        }
        try (Statement stmt = conn.createStatement()) {

            Set<Integer> groupesManquants = new HashSet<>();

            /* récupère la liste des voeux depuis la base de données */
            log("Récupération des voeux");
            stmt.setFetchSize(1000000);
            String sql = "SELECT "
                    //id du groupe de classement
                    + "cg.C_GP_COD, "
                    //id du candidat
                    + "c.G_CN_COD, "
                    //le rang peut-être nul pourles formations qui ne classent pas
                    + "NVL(C_CG_RAN,0), "
                    //le candidat a t'il déclaré être boursier? non 0 lycee 1 dusup 2
                    + "g_cn_brs, "
                    //cette déclaration a t'elle été confirmée 
                    //via les remontées de base SIECLE (1)
                    //ou directement par le chef d'établissement (2)
                    + "g_cn_flg_brs_cer,"
                    //le candidat est il du secteur sur les voeux
                    //passés par ce groupe
                    + "I_IS_FLC_SEC "
                    + " FROM c_can_grp cg, c_grp gp, c_jur_adm ja, g_can c, i_ins i"
                    //groupe de classement
                    + " WHERE cg.c_gp_cod=gp.c_gp_cod"
                    //jury admission
                    + " AND   gp.c_ja_cod=ja.c_ja_cod"
                    //candidat classé 5 non-classé 4
                    + " AND   i_ip_cod=5"
                    //id candidat
                    + " AND   cg.g_cn_cod=c.g_cn_cod"
                    //dossier non-annulé (décès...)
                    + " AND   c.g_ic_cod >= 0"
                    //id candidat
                    + " AND   cg.g_cn_cod=i.g_cn_cod"
                    //formation inscription
                    + " AND   ja.g_ti_cod=i.g_ti_cod"
                    //seulement les formations qui classent
                    + " AND   NVL(gp.c_gp_flg_pas_cla, 0)!=1";

            log(sql);

            try (ResultSet result = stmt.executeQuery(sql)) {

                /* Remarque: le rang est à null / 0 pour celles desformations 
            non-sélectives qui ne réalisent pas de classement. */
                while (result.next()) {

                    int C_GP_COD = result.getInt(1);
                    int G_CN_COD = result.getInt(2);
                    int rang = result.getInt(3);

                    boolean estDeclareBoursierLycee = (result.getInt(4) == 1);
                    int confirmationBoursier = result.getInt(5);
                    boolean estConsidereBoursier
                            = estDeclareBoursierLycee
                            && (confirmationBoursier == 1 || confirmationBoursier == 2);
                    boolean estConsidereDuSecteur = result.getBoolean(6);

                    if (!groupesClassements.containsKey(C_GP_COD)) {
                        //peut arriver si les classements ne sont pas encore remontés
                        groupesManquants.add(C_GP_COD);
                        continue;
                    }

                    GroupeClassement ga = groupesClassements.get(C_GP_COD);
                    ga.ajouterVoeu(
                            new VoeuClasse(
                                    G_CN_COD,
                                    rang,
                                    estConsidereBoursier,
                                    estConsidereDuSecteur)
                    );
                }

                result.close();
                stmt.close();

                if (!groupesManquants.isEmpty()) {
                    log(groupesManquants.size() + " groupes manquants.");
                    System.out.print("(");
                    for (int c_gp_cod : groupesManquants) {
                        System.out.print(c_gp_cod + ",");
                    }
                    System.out.println(")");
                    //throw new RuntimeException(groupesManquants.size() + " groupes manquants.");
                }

                AlgoOrdreAppelEntree entree = new AlgoOrdreAppelEntree();
                entree.groupesClassements.addAll(groupesClassements.values());
                return entree;

            } catch (SQLException ex) {
                throw new RuntimeException("Erreur de chargement des données", ex);
            }

        }
    }

    /* exportation des classements vers la base de donnéees */
    @Override
    public void exporterDonneesOrdresAppel(AlgoOrdreAppelSortie donnees) {

        try {

            log("Début de l'exportation");

            preparerExport();

            conn.setAutoCommit(false);

            try (PreparedStatement ps
                    = conn.prepareStatement(
                            "INSERT INTO J_ORD_APPEL_TMP (C_GP_COD,G_CN_COD,C_CG_ORD_APP) VALUES (?,?,?)")) {
                int count = 0;
                for (Entry<Integer, OrdreAppel> paire
                        : donnees.ordresAppel.entrySet()) {

                    Integer C_GP_COD = paire.getKey();
                    OrdreAppel ordre = paire.getValue();

                    int rang = 1;
                    for (VoeuClasse voe : ordre.voeux) {
                        ps.setInt(1, C_GP_COD);
                        ps.setInt(2, voe.G_CN_COD);
                        ps.setInt(3, rang++);
                        ps.addBatch();
                        if (++count % 500000 == 0) {
                            log("Exportation des ordres d'appel des voeux " + (count - 499999) + " a " + count);
                            ps.executeBatch();
                            ps.clearBatch();
                            log("Fait");
                        }
                    }

                }
                ps.executeBatch();
            }
            conn.commit();

            log("Mise-à-jour de la table C_CAN_GRP");
            conn.createStatement().execute("UPDATE "
                    + "(SELECT  a.C_CG_ORD_APP cible, b.C_CG_ORD_APP source FROM C_CAN_GRP a,"
                    + "J_ORD_APPEL_TMP b WHERE a.G_CN_COD=b.G_CN_COD AND a.C_GP_COD=b.C_GP_COD)"
                    + "SET cible=source");

            /* exportation des statistiques mesurant l'ecart entre l'ordre d'appel
            et le classement initial. 
             */
            log("Exportation des coefficients de divergence");
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO C_CAN_ORD_APPEL_DIV (C_GP_COD,COEF_DIV) VALUES (?,?)")) {
                for (Entry<Integer, OrdreAppel> paire
                        : donnees.ordresAppel.entrySet()) {
                    Integer C_GP_COD = paire.getKey();
                    ps.setInt(1, C_GP_COD);
                    ps.setInt(2, (int) (100 * paire.getValue().coefficientDivergence()));
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            log("Application des changements");
            conn.commit();

            log("Fin de l'exportation");

        } catch (SQLException ex) {
            log("Erreur d'exportation des données");
            throw new RuntimeException("Erreur d'exportation des données", ex);
        }
    }

    /* crée les deux tables destinées à recevoir les données exportées */
    private void preparerExport() throws SQLException {

        conn.setAutoCommit(true);

        /* pour optimiser le temps d'exportation,
            les ordres d'appel sont stockées dans une table temporaire J_ORD_APPEL_TMP
            avant la mise a jour de la table C_CAN_GRP */
        try {
            conn.createStatement().execute("DROP TABLE J_ORD_APPEL_TMP");
        } catch (SQLException e) {
            /* peut arriver si la table n'existait pas */
        };

        conn.createStatement().execute(
                "CREATE GLOBAL TEMPORARY TABLE J_ORD_APPEL_TMP ("
                + "C_GP_COD NUMBER(6,0),"
                + "G_CN_COD NUMBER(8,0),"
                + "C_CG_ORD_APP NUMBER(6,0),"
                + "PRIMARY KEY (C_GP_COD,G_CN_COD)"
                + ") ON COMMIT PRESERVE ROWS"
        );

        /* table stockant les coefficients de divergence */
        try {
            conn.createStatement().execute("DROP TABLE C_CAN_ORD_APPEL_DIV");
        } catch (SQLException e) {
            /* peut arriver si la table n'existait pas */
        };

        conn.createStatement().execute(
                "CREATE TABLE C_CAN_ORD_APPEL_DIV ("
                + "C_GP_COD NUMBER(6,0),"
                + "COEF_DIV NUMBER(3,0),"
                + "PRIMARY KEY (C_GP_COD)"
                + ")"
        );

    }

    private void log(String message) {
        System.out.println(LocalDateTime.now().toLocalTime() + ": " + message);
    }

}
