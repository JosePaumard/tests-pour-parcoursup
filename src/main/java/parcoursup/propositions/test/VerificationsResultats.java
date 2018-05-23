/* 
    Copyright 2018, 2018 Hugo Gimbert (hugo.gimbert@enseignementsup.gouv.fr) 
    
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
package parcoursup.propositions.test;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import parcoursup.propositions.algo.GroupeAffectation;
import parcoursup.propositions.algo.GroupeInternat;
import parcoursup.propositions.algo.VoeuEnAttente;

/* Permet de vérifier un certain nombre de propriétés statiques
des sorties de l'algorithme.
Sans garantir la correction du code,
cela garantit que les résultats produits satisfont les principales proprotétés
énoncées dans le document. 
Des tests complémentaires sont effectués en base par des scripts PL/SQL.
 */
public class VerificationsResultats {

    /*
    P1 (respect ordre appel pour les voeux sans internat)

    Si un candidat C1 précède un candidat C2 dans l'ordre d'appel d'une formation F
    et si C1 a un voeu en attente pour F sans demande d'internat
    alors C2 n'a pas de proposition pour F.
     */
    public static void verifierRespectOrdreAppelVoeuxSansInternat(GroupeAffectation formation) {
        for (VoeuEnAttente v1 : formation.voeux) {
            if (!v1.avecClassementInternat() && !v1.estAProposer()) {
                for (VoeuEnAttente v2 : formation.voeux) {
                    alerterSi(
                            v2.ordreAppel > v1.ordreAppel
                            && (v2.estAProposer() || v2.formationDejaObtenue()),
                            "Violation respect ordre appel pour les voeux sans demande internat"
                    );
                }
            }
        }
    }

    /*
    P2  (respect ordre appel et classement internat pour les voeux avec internat)

    Si un candidat C1 précède un candidat C2 
    à la fois dans l'ordre d'appel d'une formation F
    et dans un classement d'internat I
    et si C1 a un voeu en attente pour F avec internat I
    alors C2 n'a pas de proposition pour F avec internat I.
     */
    public static void verifierVoeuxAvecInternat(GroupeAffectation formation) {
        for (VoeuEnAttente v1 : formation.voeux) {
            if (v1.avecClassementInternat() && !v1.estAProposer()) {
                for (VoeuEnAttente v2 : formation.voeux) {
                    alerterSi(
                            v2.avecClassementInternat()
                            && v1.internatID() == v2.internatID()
                            && (!v2.formationDejaObtenue() && v2.ordreAppel > v1.ordreAppel)
                            && (!v2.internatDejaObtenu() && v2.rangInternat > v1.rangInternat)
                            && v2.avecInternat()
                            && v2.estAProposer(),
                            "Violation respect ordre et classement pour les voeux sans demande internat"
                    );
                }
            }
        }
    }

    /*
    P3 (respect classement internat pour les candidats avec une proposition sans internat)

    Si un candidat C1 a un voeu en attente pour une formation F avec demande d'internat I
    et une proposition acceptés ou en attente de réponse de sa part pour la formation F 
    sans demande d'internat,
    et si C2 est un candidat moins bien classé que C1 à l'internat I
    et si une des nouvelles propositions du jour offre l'internat I à C2
    alors que C2 n'avait pas de propositions pour I auparavant
    alors une des nouvelles propositions du jour offre la formation F et l'internat I à C1.
     */
    public static void verifierRespectClassementInternat(GroupeInternat internat) {
        for (VoeuEnAttente v1 : internat.voeux) {
            if (v1.formationDejaObtenue() && !v1.estAProposer()) {
                for (VoeuEnAttente v2 : internat.voeux) {
                    alerterSi(
                            v2.rangInternat > v1.rangInternat
                            && !v2.internatDejaObtenu()
                            && v2.estAProposer(),
                            "Violation respect ordre appel pour les attributions d'internat"
                    );
                }
            }
        }
    }

    /*

    P4  (remplissage maximal des formations dans le respect des positions d'admission à l'internat)

    Le nombre de propositios doit être inférieur au nombre de places vacantes.
    
    Si le nombre de nouvelles propositions dans une formation est strictement inférieur
    au nombre de places vacantes dans cette formation, alors tous les voeux en attente
    pour cette formation sont des voeux avec internat,
    effectués par des candidats dont le rang de classement dans l'internat correspondant
    est strictement supérieur à la position d'admission dans cet internat.
     */
    public static void verifierSurcapaciteEtRemplissage(GroupeAffectation formation) {

        /* un même candidat peut avoir plusieurs voeux, avec ou sans internat */
        Set<Integer> candidatsProposes = new HashSet<>();

        int rangDernierNouvelAppele = 0;
        for (VoeuEnAttente v : formation.voeux) {
            if (v.estAProposer() && !v.formationDejaObtenue()) {
                candidatsProposes.add(v.id.G_CN_COD);
                rangDernierNouvelAppele
                        = Integer.max(rangDernierNouvelAppele, v.ordreAppel);
            }
        }
        int nbPropositions = candidatsProposes.size();

        prevenirSi(
                nbPropositions > formation.nbPlacesVacantes()
                && (rangDernierNouvelAppele > formation.rangLimite),
                "surcapacité formation non expliquée par le rang limite,"
                + "veuillez vérifier qu'une diminuation du surbooking "
                + "a eu lieu pour le groupe de classement "
                + "C_GP_COD " + formation.id.C_GP_COD
                + " G_TA_COD " + formation.id.G_TA_COD
                + " G_TI_COD " + formation.id.G_TI_COD
        );
        alerterSi(
                nbPropositions > formation.nbPlacesVacantes()
                && !formation.estInitialementEnSurcapacite()
                && (rangDernierNouvelAppele > formation.rangLimite),
                "surcapacité formation "
                + "C_GP_COD " + formation.id.C_GP_COD
                + " G_TA_COD " + formation.id.G_TA_COD
                + " G_TI_COD " + formation.id.G_TI_COD
        );

        if (nbPropositions < formation.nbPlacesVacantes()) {
            for (VoeuEnAttente v : formation.voeux) {
                if (!v.estAProposer()) {
                    alerterSi(
                            !v.avecClassementInternat(),
                            "souscapacité formation compensable par un voeu sans classement internat");
                    alerterSi(
                            !v.estDesactiveParPositionAdmissionInternat(),
                            "souscapacité compensable par un voeu avec classement internat"
                            + "classé sous la position d'admission internat");
                }
            }
        }

    }

    /*
    P5  (remplissage maximal des internats dans le respect des ordres d'appel)

    Le nombre de propositios doit être inférieur au nombre de places vacantes.

    Si le nombre de nouvelles propositions dans un internat I est strictement inférieur
    au nombre de places vacantes dans I, alors tous les voeux en attente
    pour une formation F et demande d'internat I sont 
    soit effectués par des candidats 
    dont le classement à l'internat I est strictement supérieur
    à la position d'admission dans I ou bien situés plus bas dans l'ordre d'appel de F
    que tous les candidats ayant reçu une estAProposer de F ce jour là.
     */
    public static void verifierSurcapaciteEtRemplissage(
            GroupeInternat internat,
            Map<GroupeAffectation,Integer> rangDernierAppeles
            ) {
        
        /* un même candidat peut avoir plusieurs 
        propositions pour le même internat,
        émanant de différentes formations */
        Set<Integer> candidatsProposes = new HashSet<>();

        for (VoeuEnAttente v : internat.voeux) {
            if (v.estAProposer() && !v.internatDejaObtenu()) {
                candidatsProposes.add(v.id.G_CN_COD);
            }
        }
        
        int nbNouveauxArrivants = candidatsProposes.size();

        alerterSi(nbNouveauxArrivants > 0
                && nbNouveauxArrivants > internat.nbPlacesVacantes(), "surcapacité internat");

        if (nbNouveauxArrivants < internat.nbPlacesVacantes()) {
            for (VoeuEnAttente v : internat.voeux) {
                if (!v.estAProposer()) {

                    GroupeAffectation formation = v.groupe;
                    alerterSi(!internat.groupesConcernes.contains(formation), "formation inconnue (?)");

                    alerterSi(
                            (v.internatDejaObtenu() 
                                    || v.rangInternat <= internat.positionAdmission)
                            && (v.formationDejaObtenue() 
                                    || v.ordreAppel <=  rangDernierAppeles.get(v.groupe)),
                            "souscapacité internat compensable par un voeu"
                            + "classé sous la position d'admission internat"
                            + "et classé sous le rang du dernier appelé dans la formation");
                }
            }
        }
    }

    
    /*
    P6  (maximalité des positions d'admission) 
    
    Pour tout internat, la position d'admission est inférieure ou égale à la position maximale d'admission.
    Dans le cas où elle est strictement inférieure, augmenter d'une unité la position d'admission
    entrainerait une surcapacité d'un des internats.
    
    Non-implémenté.
    
     */
    private static void alerterSi(boolean prop, String message) {
        if (prop) {
            throw new RuntimeException("Donnees non integres: " + message);
        }
    }

    private static void prevenirSi(boolean prop, String message) {
        if (prop) {
            System.out.println("Attention: " + message);
        }
    }

}
