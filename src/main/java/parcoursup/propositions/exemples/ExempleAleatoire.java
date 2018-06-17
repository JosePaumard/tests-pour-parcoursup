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
package parcoursup.propositions.exemples;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import parcoursup.propositions.algo.AlgoPropositionsEntree;
import parcoursup.propositions.algo.GroupeAffectation;
import parcoursup.propositions.algo.GroupeAffectationUID;
import parcoursup.propositions.algo.GroupeInternat;
import parcoursup.propositions.algo.GroupeInternatUID;
import parcoursup.propositions.algo.VoeuEnAttente;

public class ExempleAleatoire extends ExemplePropositions {

    @Override
    String nom() {
        return "ExempleAleatoire";// + dateCreation;
    }

    final int nbEtudiants;

    final double proportionConcoursCommuns = 0.1;
    final double proportionInternatsCommuns = 0.5;
    final double proportionInternats = 0.5;

    final int nbFormationsParConcours = 100;
    final int maxNbVoeuxParConcoursCommun = 80;

    final int nbFormationsParEtablissement = 5;

    final int maxNbVoeuxParCandidat = 40;

    final int capaciteMaxFormationNormale = 100;
    final int capaciteMaxFormationCC = 200;

    final int capaciteMaxInternat = 30;

    final int maxNbGroupesParFormation = 5;

    List<Etablissement> etablissements = new LinkedList<>();

    int last_G_TI_COD = 1;
    int last_G_TA_COD = 1;
    int last_C_GP_COD = 1;
    int last_G_CN_COD = 1;

    final Random random = new Random();

    class Etablissement {

        final int G_TI_COD;

        final boolean isConcoursCommun;

        final boolean isInternatCommun;

        final boolean isInternatParFormation;

        final ArrayList<FormationAffectation> formations = new ArrayList<>();

        final ArrayList<GroupeClassement> jurys = new ArrayList<>();

        final Map<GroupeClassement, GroupeInternat> internatsCommuns = new HashMap<>();

        Etablissement() {
            this.G_TI_COD = last_G_TI_COD++;
            isConcoursCommun = (Math.random() < proportionConcoursCommuns);

            if (isConcoursCommun) {

                GroupeClassement g1 = new GroupeClassement();
                GroupeClassement g2 = new GroupeClassement();
                jurys.add(g1);
                jurys.add(g2);

                int nbformations = 1 + random.nextInt(nbFormationsParConcours);
                for (int i = 0; i < nbformations; i++) {
                    FormationAffectation f = new FormationAffectation();
                    formations.add(f);
                    f.ajouterGroupe(g1);
                    f.ajouterGroupe(g2);
                }

                isInternatCommun = false;
                isInternatParFormation = false;

            } else {

                isInternatCommun
                        = (Math.random() < proportionInternatsCommuns);

                isInternatParFormation
                        = !isInternatCommun && (Math.random() < proportionInternats);

                if (isInternatCommun) {
                    GroupeClassement ifilles = new GroupeClassement();
                    GroupeClassement igarcons = new GroupeClassement();
                    GroupeInternatUID ifillesid = new GroupeInternatUID(
                            ifilles.C_G_COD,
                            0);
                    GroupeInternatUID igarconsid = new GroupeInternatUID(
                            igarcons.C_G_COD,
                            0);
                    internatsCommuns.put(ifilles, new GroupeInternat(
                            ifillesid,
                            1 + random.nextInt(capaciteMaxInternat),
                            1 + random.nextInt(100))
                    );
                    internatsCommuns.put(igarcons, new GroupeInternat(
                            igarconsid,
                            1 + random.nextInt(capaciteMaxInternat),
                            random.nextInt(101))
                    );
                }

                int nbFormations = 1 + random.nextInt(nbFormationsParEtablissement);

                for (int i = 0; i < nbFormations; i++) {
                    FormationAffectation f = new FormationAffectation();
                    formations.add(f);

                    if (isInternatParFormation) {
                        f.juryInternat = new GroupeClassement();
                        GroupeInternatUID iid = new GroupeInternatUID(
                                f.juryInternat.C_G_COD,
                                f.G_TA_COD);
                        f.internat = new GroupeInternat(
                                iid,
                                1 + random.nextInt(capaciteMaxInternat),
                                random.nextInt(101));
                    }

                    int nbGroupes = 1 + random.nextInt(maxNbGroupesParFormation);

                    for (int j = 0; j < nbGroupes; j++) {
                        GroupeClassement g = new GroupeClassement();
                        f.ajouterGroupe(g);
                        jurys.add(g);
                    }
                }
            }
        }

        int capacite() {
            int result = 0;
            for (FormationAffectation f : formations) {
                result += f.capacite();
            }
            return result;
        }

        int ajouterVoeux(Candidat candidat) {
            int nbVoeux = isConcoursCommun
                    ? 1 + random.nextInt(maxNbVoeuxParConcoursCommun)
                    : 1;
            for (int i = 0; i < nbVoeux; i++) {
                FormationAffectation fa = formations.get(random.nextInt(formations.size()));
                fa.ajouterVoeu(candidat, random.nextBoolean());
            }
            return nbVoeux;
        }

        class FormationAffectation {

            final int G_TA_COD;

            GroupeInternat internat = null;
            GroupeClassement juryInternat = null;

            FormationAffectation() {
                this.G_TA_COD = last_G_TA_COD++;
            }

            void ajouterGroupe(GroupeClassement c) {

                GroupeAffectationUID gui
                        = new GroupeAffectationUID(
                                c.C_G_COD,
                                G_TI_COD,
                                G_TA_COD);

                int capaciteMax = isConcoursCommun ? capaciteMaxFormationCC : capaciteMaxFormationNormale;
                int capacite = random.nextInt(capaciteMax + 1);
                int rangLimite = (int) ((1 + Math.random()) * capacite);
                GroupeAffectation ga
                        = new GroupeAffectation(
                                capacite,
                                gui,
                                rangLimite
                        );

                this.classements.put(ga, c);
                groupes.add(ga);
            }

            void ajouterVoeu(Candidat candidat, boolean avecInternat) {

                /* pas deux fois le même voeu */
                if ((avecInternat && vusAvecInternat.contains(candidat))
                        || (!avecInternat && vusSansInternat.contains(candidat))) {
                    return;
                }

                (avecInternat ? vusAvecInternat : vusSansInternat).add(candidat);

                GroupeAffectation ga
                        = groupes.get(random.nextInt(groupes.size()));
                GroupeClassement cl
                        = classements.get(ga);
                int rang = cl.ajouterCandidat(candidat);

                if (!avecInternat || internat == null) {
                    if (rang <= cl.plusHautRangAffecte) {
                        ga.ajouterCandidatAffecte(candidat.G_CN_COD);
                    } else {
                        VoeuEnAttente.ajouterVoeu(
                                candidat.G_CN_COD,
                                avecInternat,
                                ga,
                                rang);
                    }
                } else {
                    int rangInternat = juryInternat.ajouterCandidat(candidat);
                            
                    if ((rang <= cl.plusHautRangAffecte && rangInternat <= juryInternat.plusHautRangAffecte)) {
                        ga.ajouterCandidatAffecte(candidat.G_CN_COD);
                        internat.ajouterCandidatAffecte(candidat.G_CN_COD);
                    } else {
                        VoeuEnAttente.ajouterVoeu(
                                candidat.G_CN_COD,
                                ga,
                                rang,
                                internat,
                                rangInternat
                        );
                    }

                }
            }

            final Map<GroupeAffectation, GroupeClassement> classements
                    = new HashMap<>();

            final ArrayList<GroupeAffectation> groupes
                    = new ArrayList<>();

            final Set<Candidat> vusAvecInternat = new HashSet<>();
            final Set<Candidat> vusSansInternat = new HashSet<>();

            int capacite() {
                int result = 0;
                for (GroupeAffectation g : groupes) {
                    result += g.capacite;
                }
                return result;
            }
        }

        class GroupeClassement {

            final int C_G_COD;

            /* le rang le plus haut dans l'ordre d'appel d'un candidat recruté */
            int plusHautRangAffecte = random.nextInt(nbEtudiants / 4);
            
            GroupeClassement() {
                this.C_G_COD = last_C_GP_COD++;
            }

            final Map<Candidat, Integer> rangs = new HashMap<>();

            /* ajoute un candidat et renvoie son rang.*/
            int ajouterCandidat(Candidat c) {

                if (rangs.containsKey(c)) {
                    return rangs.get(c);
                }

                while (true) {
                    int rang = 1 + random.nextInt(nbEtudiants);
                    if (!rangs.containsValue(rang)) {
                        rangs.put(c, rang);
                        return rang;
                    }
                }
            }
        }

    }

    class Candidat {

        final int G_CN_COD;

        Candidat() {
            this.G_CN_COD = last_G_CN_COD++;
        }

    }

    final String dateCreation = LocalDateTime.now().toString();

    public ExempleAleatoire(int nbEtudiants) {

        if(nbEtudiants < 100)
            nbEtudiants = 100;
        this.nbEtudiants = nbEtudiants;

    }

    @Override
    AlgoPropositionsEntree donneesEntree() throws Exception {

        int capacite_totale = 0;

        GroupeInternat.nbJoursCampagne = 1 + random.nextInt(70);
        log("Jours de campagne: " + GroupeInternat.nbJoursCampagne);

        log("Génération aléatoire des établissements et formations");
        while (capacite_totale < nbEtudiants) {
            Etablissement e = new Etablissement();
            etablissements.add(e);
            capacite_totale += e.capacite();
        }

        log("Génération aléatoire des voeux et classements");
        for (int i = 0; i < nbEtudiants; i++) {
            Candidat c = new Candidat();
            int nbVoeux = (int) (Math.random() * maxNbVoeuxParCandidat);
            while (nbVoeux > 0) {
                Etablissement e
                        = etablissements.get(random.nextInt(etablissements.size()));
                nbVoeux -= e.ajouterVoeux(c);
            }
            if ((i + 1) % 100000 == 0) {
                log((1 + i) + " étudiants générés ...");
            }
        }

        log("Génération données entrée algorithme");

        AlgoPropositionsEntree entree = new AlgoPropositionsEntree();

        for (Etablissement e : etablissements) {
            for (Etablissement.FormationAffectation fa : e.formations) {
                entree.groupesAffectations.addAll(fa.groupes);
                if (fa.internat != null && fa.internat.candidatsEnAttente.size() > 0) {
                    entree.internats.add(fa.internat);
                }
            }
            for (GroupeInternat internat : e.internatsCommuns.values()) {
                if (internat.candidatsEnAttente.size() > 0) {
                    entree.internats.add(internat);
                }
            }
        }

        JAXBContext jc = JAXBContext.newInstance(AlgoPropositionsEntree.class);
        Marshaller m = jc.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        m.marshal(entree, new File("xml/" + nom() + "_entree.xml"));
        
        return entree;

    }

    void log(String msg) {
        System.out.println(LocalDateTime.now().toLocalTime() + ": " + msg);
    }
}
