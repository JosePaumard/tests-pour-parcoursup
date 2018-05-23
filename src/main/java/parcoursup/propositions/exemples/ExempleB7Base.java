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

import parcoursup.propositions.algo.GroupeAffectation;
import parcoursup.propositions.algo.GroupeInternat;
import parcoursup.propositions.algo.GroupeAffectationUID;
import parcoursup.propositions.algo.GroupeInternatUID;

public class ExempleB7Base {

    public final GroupeAffectation groupe;

    public final GroupeInternat internat;

    public final int nbPlacesTotalInternat = 10;

    ExempleB7Base() {

        groupe = new GroupeAffectation(
                40,
                new GroupeAffectationUID(1, 1, 1),
                0);

        internat = new GroupeInternat(
                new GroupeInternatUID(1, groupe.id.G_TA_COD),
                nbPlacesTotalInternat,
                100 //pourcentageOuverture
        );

    }

}
