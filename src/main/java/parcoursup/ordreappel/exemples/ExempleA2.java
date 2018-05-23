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

public class ExempleA2 extends ExempleOrdreAppel {

    @Override
    String nom() {
        return "exemple_A2";
    }

    @Override
    GroupeClassement initialise() {

        GroupeClassement groupe = new GroupeClassement(0, 2, 0);

        /* C1 C2 C3 C4 C5 B6 C7 C8 */
        for (int i = 1; i <= 5; i++) {
            groupe.ajouterVoeu(new VoeuClasse(i, i, false, false));
        }
        groupe.ajouterVoeu(new VoeuClasse(6, 6, true, false));
        groupe.ajouterVoeu(new VoeuClasse(7, 7, false, false));
        groupe.ajouterVoeu(new VoeuClasse(8, 8, false, false));

        return groupe;

    }

}
