
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

public class GroupeAffectation {

    /* le id d'affectation identifiant d emanière unique le groupe dans la base */
    public final GroupeAffectationUID id;

    /* la capacité de la formation */
    public final int capacite;

    /* le rang limite des candidats (dans l'ordre d'appel): 
    tous les candidats de rang inférieur reçoivent une proposition */
    public final int rangLimite;

    /* constructeur */
    public GroupeAffectation(
            int capacite,
            GroupeAffectationUID id,
            int rangLimite) {
        this.id = id;
        this.capacite = capacite;
        this.rangLimite = rangLimite;
    }

    /* ajoute un voeu dans le groupe */
    void ajouterVoeu(VoeuEnAttente voe) {
        voeux.add(voe);
        voeuxsontTries = false;
    }

    /* ajoute un candidat affecté */
    public void ajouterCandidatAffecte(int G_CN_COD) {
        candidatsAffectes.add(G_CN_COD);
    }

    public boolean estAffecte(int G_CN_COD) {
        return candidatsAffectes.contains(G_CN_COD);
    }

    /* met a jour le statut aProposer, pour chaque voeu du groupe */
    void mettreAJourPropositions() {

        int aPourvoir = nbPlacesVacantes();

        voeux.forEach((v) -> {
            v.conserverEnAttente();
        });

        /* on calcule le nombre de propositions dues au rang limite.
           Les voeux désactivés pour cause de demande d'internat non satisfiables
            ne sont pas pris en compte.
         */
        int dernierCandidatAvecProposition = -1;

        for (VoeuEnAttente v : voeuxTries()) {

            if (v.estDesactiveParPositionAdmissionInternat()) {
                continue;
            }

            /* Deux voeux consécutifs peuvent concerner un même candidat,
            ayant deux voeux en attente (un avec et un sans internat).
            Cela est pris en compte dans le calcul du nombre de places restantes
            et dans les propositions.
             */
            if (aPourvoir > 0
                    || v.ordreAppel <= rangLimite
                    || v.formationDejaObtenue()
                    || dernierCandidatAvecProposition == v.id.G_CN_COD) {

                v.proposer();

                if (!v.formationDejaObtenue()
                        && dernierCandidatAvecProposition != v.id.G_CN_COD) {
                    aPourvoir--;
                }

                dernierCandidatAvecProposition = v.id.G_CN_COD;

            }
        }
    }

    /* le nombre de places vacantes au lancement du calcul.
    Peut être négatif en cas de modification à la baisse 
    des paramètres de surbooking. */
    public int nbPlacesVacantes() {
        return capacite - candidatsAffectes.size();
    }

    /* la formation était elle initialement en surcapacite */
    public boolean estInitialementEnSurcapacite() {
        return nbPlacesVacantes() < 0;
    }

    /* la liste initiale des voeux du groupe, triée dans l'ordre d'appel du candidat.
    Remarque: c'est un ordre partiel car il peut y avoir deux voeux du même candidat,
    un avec internat et l'autre sans. */
    public final List<VoeuEnAttente> voeux = new LinkedList<>();

    /* trie les voeux dans l'ordre d'appel */
    public List<VoeuEnAttente> voeuxTries() {
        if (!voeuxsontTries) {
            voeux.sort((VoeuEnAttente v1, VoeuEnAttente v2) -> v1.ordreAppel - v2.ordreAppel);
            voeuxsontTries = true;
        }
        return voeux;
    }

    private boolean voeuxsontTries = false;

    private final Set<Integer> candidatsAffectes
            = new HashSet<>();

}
