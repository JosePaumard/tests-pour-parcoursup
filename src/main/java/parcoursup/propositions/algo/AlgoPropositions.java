
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
package parcoursup.propositions.algo;

import java.time.LocalDateTime;
import java.util.HashMap;
import parcoursup.propositions.test.VerificationsResultats;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AlgoPropositions {

    /* la boucle principale du calcul des propositions à envoyer */
    public static AlgoPropositionsSortie calculePropositions(AlgoPropositionsEntree entree) {

        entree.verifierIntegrite();

        log("Début calcul propositions");

        /* groupes à mettre à jour */
        Set<GroupeAffectation> groupesAMettreAJour
                = new HashSet<>();
        groupesAMettreAJour.addAll(entree.groupesAffectations);

        /* initialisation des positions maximales d'admission dans les internats */
        for (GroupeInternat internat : entree.internats) {
            internat.initialiserPositionAdmission();
        }

        int compteurBoucle = 0;
        while (groupesAMettreAJour.size() > 0) {

            /* calcul des propositions à effectuer, 
            étant données les positions actuelles d'admissions aux internats */
            for (GroupeAffectation gc : groupesAMettreAJour) {
                gc.mettreAJourPropositions();
            }

            /* Test de surcapacité des internats, avec 
               mise-à-jour de la position d'admission si nécessaire.
            
            Baisser la position d'admission d'un internat ne diminue
            pas le nombre de candidats dans les autres internats, voire augmente ces nombres,
            car les formations devront potentiellement descendre plus bas dans l'ordre d'appel.
            
            Par conséquent, on peut mettre à jour toutes les positions d'admission
            de tous les internats sans mettre à jour systématiquement les propositions:
            si un internat est détecté en surcapacité avant la mise
            à jour, il l'aurait été également après la mise-à-jour.
            (Mais la réciproque est fausse en général).
            
            De cette manière, on reste bien dans l'ensemble E des vecteurs de positions
            d'admission supérieurs sur chaque composante au vecteur de positions d'admission
            le plus permissif possible parmi tous ceux respectant les contraintes
            de capacité des internats et situés en deça des positions maximales
            d'admission.
            
            Ce vecteur est égal, sur chaque composante, à la valeur minimum de cette
            compsoante parmi les éléments de E.
            
            La boucle termine quand les contraintes de capacité des internats
            sont satisfaites, c'est à dire quand ce minimum global est atteint.
            
            Une propriété de symétrie i.e. d'équité intéressante:
            le résultat ne dépend pas de l'ordre dans lequel on itère sur les internats et
            les formations.
             */
            
            groupesAMettreAJour.clear();

            for (GroupeInternat internat : entree.internats) {
                boolean maj = internat.mettreAJourPositionAdmission();
                if (maj) {
                    groupesAMettreAJour.addAll(internat.groupesConcernes);
                }
            }
            compteurBoucle++;
        }

        log("Calcul terminé après " + compteurBoucle + " itération(s).");

        log("Vérification des propriétés attendues des propositions pour un des "
                + entree.groupesAffectations.size()
                + " groupes d'affectation");

        int step = Integer.max(1, entree.groupesAffectations.size() / 100);
        int count = 0;
        afficherJauge();

        for (GroupeAffectation groupe : entree.groupesAffectations) {

            if (count++ % step == 0) {
                System.out.print("-");
                System.out.flush();
            }

            VerificationsResultats.verifierRespectOrdreAppelVoeuxSansInternat(groupe);
            VerificationsResultats.verifierVoeuxAvecInternat(groupe);
            VerificationsResultats.verifierSurcapaciteEtRemplissage(groupe);

        }
        System.out.println();

        log("Vérification des propriétés attendues des propositions d'un des "
                + entree.internats.size() + " internats");

        step = Integer.max(1, entree.internats.size() / 100);
        count = 0;
        afficherJauge();

        /* precalcul des rangs d'appel maximum dans chaque groupe parmi les nouveaux entrants */
        Map<GroupeAffectation, Integer> rangsMaxNouvelArrivant = new HashMap<>();
        for (GroupeAffectation groupe : entree.groupesAffectations) {
            int rangMax = 0;
            for (VoeuEnAttente v : groupe.voeuxTries()) {
                if (v.estAProposer() && !v.formationDejaObtenue()) {
                    rangMax = Integer.max(rangMax, v.ordreAppel);
                }
            }
            rangsMaxNouvelArrivant.put(groupe, rangMax);
        }

        for (GroupeInternat internat : entree.internats) {

            if (count++ % step == 0) {
                System.out.print("-");
                System.out.flush();
            }

            VerificationsResultats.verifierRespectClassementInternat(internat);
            VerificationsResultats.verifierSurcapaciteEtRemplissage(
                    internat,
                    rangsMaxNouvelArrivant);

        }
        System.out.println();

        log("Vérification ok");

        log("Préparation données de sortie");

        AlgoPropositionsSortie sortie = new AlgoPropositionsSortie();

        for (GroupeAffectation gc : entree.groupesAffectations) {
            for (VoeuEnAttente voe : gc.voeux) {
                if (voe.estAProposer()) {
                    sortie.propositions.add(voe);
                } else {
                    sortie.enAttente.add(voe);
                }
            }
        }

        sortie.internats.addAll(entree.internats);

        return sortie;

    }

    private static void log(String msg) {
        System.out.println(LocalDateTime.now().toLocalTime() + ": " + msg);
    }

    private static void afficherJauge() {
        for (int i = 0; i < 100; i++) {
            System.out.print("-");
        }
        System.out.println();
    }
}
