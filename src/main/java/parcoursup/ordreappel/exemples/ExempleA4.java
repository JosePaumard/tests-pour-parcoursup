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

public class ExempleA4 extends ExempleOrdreAppel {

    @Override
    String nom() {
        return "exemple_A4";
    }

    @Override
    GroupeClassement initialise() {

        GroupeClassement groupe = new GroupeClassement(0, 10, 0);

        /* C1 B2 B3 C4 C5 C6 C7 B8 C9 C10 */
        groupe.ajouterVoeu(new VoeuClasse(1, 1, false, false));
        groupe.ajouterVoeu(new VoeuClasse(2, 2, true, false));
        groupe.ajouterVoeu(new VoeuClasse(3, 3, true, false));
        groupe.ajouterVoeu(new VoeuClasse(4, 4, false, false));
        groupe.ajouterVoeu(new VoeuClasse(5, 5, false, false));
        groupe.ajouterVoeu(new VoeuClasse(6, 6, false, false));
        groupe.ajouterVoeu(new VoeuClasse(7, 7, false, false));
        groupe.ajouterVoeu(new VoeuClasse(8, 8, true, false));
        groupe.ajouterVoeu(new VoeuClasse(9, 9, false, false));
        groupe.ajouterVoeu(new VoeuClasse(10, 10, false, false));

        return groupe;

    }

}
