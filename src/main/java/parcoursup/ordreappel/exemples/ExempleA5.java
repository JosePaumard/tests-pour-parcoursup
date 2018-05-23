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
package parcoursup.ordreappel.exemples;

import parcoursup.ordreappel.algo.GroupeClassement;
import parcoursup.ordreappel.algo.VoeuClasse;

public class ExempleA5 extends ExempleOrdreAppel {

    @Override
    String nom() {
        return "exemple_A5";
    }

    @Override
    GroupeClassement initialise() {

        GroupeClassement groupe = new GroupeClassement(0, 10, 95);

        /*  (BR)1(BR)2R3 . . . R19C20
            (BR)21(BR)22R23 . . . R39C40
            (BR)41R42 . . . R50
            B51R52 . . . R60
            B61R62 . . . R70
            B71R72 . . . R80
            B81R82 . . . R90
            B91R92 . . . R100. */
        for (int i = 0; i <= 1; i++) {
            groupe.ajouterVoeu(new VoeuClasse(20 * i + 1, 20 * i + 1, true, true));
            groupe.ajouterVoeu(new VoeuClasse(20 * i + 2, 20 * i + 2, true, true));
            for (int j = 3; j <= 19; j++) {
                groupe.ajouterVoeu(new VoeuClasse(20 * i + j, 20 * i + j, false, true));
            }
            groupe.ajouterVoeu(new VoeuClasse(20 * i + 20, 20 * i + 20, false, false));
        }

        groupe.ajouterVoeu(new VoeuClasse(41, 41, true, true));
        for (int k = 42; k <= 50; k++) {
            groupe.ajouterVoeu(new VoeuClasse(k, k, false, true));
        }

        for (int l = 51; l <= 91; l += 10) {
            groupe.ajouterVoeu(new VoeuClasse(l, l, true, false));
            for (int m = l + 1; m <= l + 9; m++) {
                groupe.ajouterVoeu(new VoeuClasse(m, m, false, true));
            }
        }

        return groupe;

    }

}
