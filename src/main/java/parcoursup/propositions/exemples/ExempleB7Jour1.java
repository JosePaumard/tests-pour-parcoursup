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

import parcoursup.propositions.algo.AlgoPropositionsEntree;
import parcoursup.propositions.algo.GroupeInternat;
import parcoursup.propositions.algo.VoeuEnAttente;

/**
 *
 * @author gimbert
 */
public class ExempleB7Jour1 extends ExemplePropositions {

    /* nom de l'exemple */
    @Override
    String nom() {
        return "ExempleB7Jour1";
    }

    /* nombre total de candidats */
    public final int n = 332;

    @Override
    AlgoPropositionsEntree donneesEntree() {

        GroupeInternat.nbJoursCampagne = 1;

        ExempleB7Base base = new ExempleB7Base();

        VoeuEnAttente.ajouterVoeu(
                1,
                base.groupe,
                1,
                base.internat,
                28);

        VoeuEnAttente.ajouterVoeu(
                2,
                base.groupe,
                2,
                base.internat,
                15);

        VoeuEnAttente.ajouterVoeu(
                20,
                base.groupe,
                20,
                base.internat,
                5);

        VoeuEnAttente.ajouterVoeu(
                21,
                base.groupe,
                21,
                base.internat,
                6);

        for (int i = 1; i <= n; i++) {
            /* 
            1 -- 28
            2 -- 15
            3 -- 1
            4 -- 2
            5 -- 3
            6 -- 4
            7 -- 7
            ...
            14 -- 14
            15 -- 16
            ...
            19 -- 20
            20 -- 5
            21 -- 6
            22 -- 21
            ...
            28 -- 27
            29 -- 29
            ...
             */
            if (i == 1 || i == 2 || i == 20 || i == 21) {
                continue;
            }

            int G_CN_COD = i;
            int ordreAppel = i;
            int rangInternat
                    = (i <= 6) ? i - 2
                            : (i <= 14) ? i
                                    : (i <= 20) ? i + 1
                                            : (i <= 28) ? i - 1
                                                    : i;
            VoeuEnAttente.ajouterVoeu(
                    G_CN_COD,
                    base.groupe,
                    ordreAppel,
                    base.internat,
                    rangInternat
            );
        }

        AlgoPropositionsEntree J1 = new AlgoPropositionsEntree();
        J1.groupesAffectations.add(base.groupe);
        J1.internats.add(base.internat);
        return J1;
    }

}
