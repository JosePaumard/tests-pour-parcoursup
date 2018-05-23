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

public class ExempleB7Jour2 extends ExemplePropositions {

    /* nom de l'exemple */
    @Override
    String nom() {
        return "ExempleB7Jour2";
    }

    @Override
    AlgoPropositionsEntree donneesEntree() throws Exception {

        ExempleB7Jour1 J1 = new ExempleB7Jour1();
        AlgoPropositionsEntree entree
                = J1.donneesEntree();

        AlgoPropositionsSortie sortie
                = AlgoPropositions.calculePropositions(entree);

        ExempleB7Base base = new ExempleB7Base();

        /* C21 et C30 d́eclinent. */
        for (VoeuEnAttente voe : sortie.propositions) {
            if (voe.id.G_CN_COD == 21 || voe.id.G_CN_COD == 30) {
                continue;
            }
            base.internat.ajouterCandidatAffecte(voe.id.G_CN_COD);
        }
        for (VoeuEnAttente voe : sortie.enAttente) {
            if (voe.id.G_CN_COD == 21 || voe.id.G_CN_COD == 30) {
                continue;
            }
        }

        GroupeInternat.nbJoursCampagne = 2;

        for (VoeuEnAttente voe : sortie.enAttente) {
            if (voe.id.G_CN_COD == 21 || voe.id.G_CN_COD == 30) {
                continue;
            }

            VoeuEnAttente.ajouterVoeu(
                    voe.id.G_CN_COD,
                    base.groupe,
                    voe.ordreAppel,
                    base.internat,
                    voe.rangInternat);

        }

        AlgoPropositionsEntree J2 = new AlgoPropositionsEntree();
        J2.groupesAffectations.add(base.groupe);
        J2.internats.add(base.internat);
        return J2;

    }

}
