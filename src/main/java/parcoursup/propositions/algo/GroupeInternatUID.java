
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

public class GroupeInternatUID {

    /*l'identifiant unique de l'internat dans la base de données */
    public final int C_GI_COD;

    /*l'identifiant unique de la formation d'affecttaion dans la base de données.
        Positionné à 0 pour un internat commun à plusieurs formations.*/
    public final int G_TA_COD;

    public GroupeInternatUID(
            int C_GI_COD,
            int G_TA_COD) {
        this.C_GI_COD = C_GI_COD;
        this.G_TA_COD = G_TA_COD;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof GroupeInternatUID) {
            GroupeInternatUID ta = (GroupeInternatUID) obj;
            return this.C_GI_COD == ta.C_GI_COD
                    && this.G_TA_COD == ta.G_TA_COD;
        } else {
            throw new RuntimeException("Test d'égalité imprévu");
        }
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(
                C_GI_COD ^ (G_TA_COD << 16));
    }

}
