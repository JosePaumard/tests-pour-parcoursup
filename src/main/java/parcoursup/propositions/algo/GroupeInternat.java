
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

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.xml.bind.annotation.XmlTransient;

public class GroupeInternat {

    /* Le triplet identifiant le groupe de classement internat dans la base de donnees
     */
    public final GroupeInternatUID id;

    /* le nombre total de places */
    final int capacite;

    /* le pourcentage d'ouverture fixé par le chef d'établissement */
    final int pourcentageOuverture;

    /* le nombre de places vacantes dans cet internat */
    public int nbPlacesVacantes() {
        /* On seuille à 0,
        en cas de réduction du nombre de lits conduisant à une différence négative */
        return Integer.max(0, capacite - candidatsAffectes.size());
    }

    /* le nombre de demandes d'internats considérées
    Bmax dans le document de spécification */
    public int contingentAdmission = 0;

    /* la position d'admission dans cet internat, calculée par l'algorithme */
    public int positionAdmission = 0;

    /* la position maximale d'admission dans cet internat, calculée par l'algorithme */
    public int positionMaximaleAdmission = 0;

    /* le nombre de jours depuis l'ouverture de la campagne, 1 le premie rjour */
    public static Integer nbJoursCampagne = null;

    /* la liste des groupes de classement concernes par cet internat */
    @XmlTransient
    public final Set<GroupeAffectation> groupesConcernes
            = new HashSet<>();

    /* la liste des voeux du groupe.
    Apres le calcul de la position initiale d'admission
    cette liste est triée par ordre de classement internat */
    public final List<VoeuEnAttente> voeux = new LinkedList<>();

    public GroupeInternat(
            GroupeInternatUID id,
            int nbPlacesTotal,
            int pourcentageOuverture
    ) {
        this.id = id;
        this.capacite = nbPlacesTotal;
        this.pourcentageOuverture = pourcentageOuverture;
    }

    void ajouterVoeu(VoeuEnAttente voe, GroupeAffectation groupe) {
        assert voe.avecInternat();
        if (estInitialise) {
            throw new RuntimeException("Groupe déjà initialisé");
        }
        voeux.add(voe);
        groupesConcernes.add(groupe);
        if (!candidatsAffectes.contains(voe.id.G_CN_COD)) {
            candidatsEnAttente.add(voe.id.G_CN_COD);
        }
    }

    /* ajoute un candidat affecté.
    Supprime le candidat de la liste des candidats en attente , si il y a lieu*/
    public void ajouterCandidatAffecte(int G_CN_COD) {
        candidatsAffectes.add(G_CN_COD);
        candidatsEnAttente.remove(G_CN_COD);
    }

    public boolean estAffecte(int G_CN_COD) {
        return candidatsAffectes.contains(G_CN_COD);
    }

    /* initialise la position d'admission à son maximum
    Bmax dans le document de référence */
    public void initialiserPositionAdmission() {

        /* on calcule le nombre de candidats éligibles à une admission
        dans l'internat aujourd'hui, stocké dans la variable assietteAdmission.
        On colle aux notations du document de référence */
        int M = candidatsEnAttente.size() + candidatsAffectes.size();
        int L = capacite;
        int t = nbJoursCampagne;
        int p = pourcentageOuverture;

        final int assietteAdmission;

        if (M <= L) {
            assietteAdmission = M;
        } else if (t == 1) {
            /* le premier jour on s'en tient aux lits disponibles */
            assietteAdmission = L;
        } else if (t <= 30) {
            /* les 30 jours suivants, on élargit progressivement
            l'assiette, en tenant compte de la correction du proviseur */
            assietteAdmission
                    = L + (M - L) * (t - 1) * p / 100 / 30;
        } else if (t < 60) {
            /* les 29 jours suivants, l'assiette est maximale,
            possiblement réduite par la correction du proviseur */
            assietteAdmission
                    = L + (M - L) * p / 100;
        } else {
            /* finalement, l'assiette est maximale */
            assietteAdmission = M;
        }

        this.contingentAdmission = Integer.max(0, assietteAdmission - candidatsAffectes.size());

        if (t <= 0
                || t > 120
                || p < 0
                || p > 100
                || L < 0
                || assietteAdmission > M
                || contingentAdmission > candidatsEnAttente.size()
                || contingentAdmission < 0) {
            throw new RuntimeException("Problème de calcul du contingent d'admisison,"
                    + " veuillez vérifier les données.");
        }

        if (contingentAdmission == 0) {
            positionMaximaleAdmission = 0;
        } else {

            /* tri des voeux par ordre de classement à l'internat */
            voeux.sort((VoeuEnAttente v1, VoeuEnAttente v2) -> v1.rangInternat - v2.rangInternat);

            /* on itere les candidats en attente d'internat jusqu'à arriver
            au contingent calculé. Remarque: il peut y avoir plusieurs voeux pour
            le même candidat, et les voeux sont triés par rang internat,
            donc les voeux d'un même candidat sont consécutifs */
            int compteurCandidat = 0;
            int dernierRangComptabilise = 0;

            for (VoeuEnAttente voe : voeux) {

                /* sortie de boucle: le contingent est atteint */
                if (compteurCandidat == contingentAdmission) {
                    break;
                }

                /* deux cas où le voeu ne change pas la valeur du dernier rang comptabilisé
                et du nombre de candidat comptés dans le contingent.
                 Premier cas: on a vu le même candidat au passage précédent dans la boucle */
                if (voe.rangInternat == dernierRangComptabilise) {
                    continue;
                }

                /* Second cas: l'internat est déjà obtenu par le candidat */
                if (voe.internatDejaObtenu()) {
                    continue;
                }

                /* Dans les cas restants, on met à jour.*/
                dernierRangComptabilise = voe.rangInternat;
                compteurCandidat++;

            }

            positionMaximaleAdmission = dernierRangComptabilise;

        }

        positionAdmission = positionMaximaleAdmission;

        estInitialise = true;

    }

    /* Met à jour la position d'admission si nécessaire.
    Renvoie true si la position d'admission a été effectivement mise à jour */
    public boolean mettreAJourPositionAdmission() {

        if (!estInitialise) {
            throw new RuntimeException("La position doit être initialisée au prélable");
        }
        /* L'initialisation implique que
            la liste des voeux est triée par classement internat */

        /* on compte le nombre de propositions a faire.
        En cas de dépassement, on met à jour la position d'admission */
        int comptePlacesProposees = 0;
        int dernierCandidatComptabilise = -1;
        
        for (VoeuEnAttente voe : voeux) {

            /* si on a dépassé la position d'admission, on arrête */
            if (voe.rangInternat > positionAdmission) {
                return false;
            }

            /* les propositions à un même candidat comptent pour une seule place */
            if (voe.id.G_CN_COD == dernierCandidatComptabilise) {
                continue;
            }

            /* le candidat a déjà l'internat, ignoré pour la mise a jour de la pos admision            
            et du rang sur liste d'attente internat */
            if (voe.internatDejaObtenu()) {
                continue;
            }

            /* si ok pour déclencher la proposition, on met à jour */
            if (voe.formationDejaObtenue()
                    || voe.estAProposer()) {

                comptePlacesProposees++;
                dernierCandidatComptabilise = voe.id.G_CN_COD;

                if (comptePlacesProposees > nbPlacesVacantes()) {
                    positionAdmission = voe.rangInternat - 1;
                    return true;
                }
            }
        }
        return false;
    }

    /* true si et seulement si la position maximal d'admission a été calculée,
    ce qui implique que la  liste des voeux est triée par ordre de classement internat.
     */
    private boolean estInitialise = false;

    private final Set<Integer> candidatsAffectes
            = new HashSet<>();

    public final Set<Integer> candidatsEnAttente
            = new HashSet<>();

}
