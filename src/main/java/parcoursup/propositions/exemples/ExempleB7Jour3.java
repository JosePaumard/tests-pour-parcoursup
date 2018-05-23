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

import parcoursup.propositions.algo.AlgoPropositions;
import parcoursup.propositions.algo.AlgoPropositionsEntree;
import parcoursup.propositions.algo.AlgoPropositionsSortie;
import parcoursup.propositions.algo.GroupeInternat;
import parcoursup.propositions.algo.VoeuEnAttente;

public class ExempleB7Jour3 extends ExemplePropositions {

    /* nom de l'exemple */
    @Override
    String nom() {
        return "ExempleB7Jour3";
    }

    @Override
    AlgoPropositionsEntree donneesEntree() throws Exception {

        ExempleB7Jour2 J2 = new ExempleB7Jour2();
        AlgoPropositionsEntree entree
                = J2.donneesEntree();

        AlgoPropositionsSortie sortie
                = AlgoPropositions.calculePropositions(entree);

        /* C1 à C20 d́eclinent. */
        ExempleB7Base base = new ExempleB7Base();
        for (VoeuEnAttente voe : sortie.propositions) {
            if (voe.id.G_CN_COD <= 20) {
                continue;
            }
            base.internat.ajouterCandidatAffecte(voe.id.G_CN_COD);
        }
        for (VoeuEnAttente voe : sortie.enAttente) {
            if (voe.id.G_CN_COD <= 20) {
                continue;
            }
            base.internat.ajouterCandidatAffecte(voe.id.G_CN_COD);
        }

        GroupeInternat.nbJoursCampagne = 3;

        for (VoeuEnAttente voe : sortie.enAttente) {
            if (voe.id.G_CN_COD <= 20) {
                continue;
            }

            VoeuEnAttente.ajouterVoeu(
                    voe.id.G_CN_COD,
                    base.groupe,
                    voe.ordreAppel,
                    base.internat,
                    voe.rangInternat);

        }

        AlgoPropositionsEntree J3 = new AlgoPropositionsEntree();
        J3.groupesAffectations.add(base.groupe);
        J3.internats.add(base.internat);
        return J3;

    }

}
