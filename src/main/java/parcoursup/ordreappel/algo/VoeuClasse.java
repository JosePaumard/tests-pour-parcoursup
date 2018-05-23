
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
package parcoursup.ordreappel.algo;

public class VoeuClasse implements Comparable<VoeuClasse> {

    /* les différents types de candidats */
    public enum TypeCandidat {
        BoursierResident,
        BoursierNonResident,
        NonBoursierResident,
        NonBoursierNonResident
    };

    /* le type du candidat */
    public final TypeCandidat typeCandidat;

    /* code identifiant le candidat dans la base de données */
    public final int G_CN_COD;

    /* le rang du voeu transmis par la commission de classement des voeux */
    public final int rang;

    public VoeuClasse(
            int G_CN_COD,
            int rang,
            boolean estBoursier,
            boolean estResident) {
        this.G_CN_COD = G_CN_COD;
        this.rang = rang;
        this.typeCandidat
                = estBoursier
                        ? (estResident ? TypeCandidat.BoursierResident : TypeCandidat.BoursierNonResident)
                        : (estResident ? TypeCandidat.NonBoursierResident : TypeCandidat.NonBoursierNonResident);
    }

    public boolean estBoursier() {
        return typeCandidat == TypeCandidat.BoursierResident
                || typeCandidat == TypeCandidat.BoursierNonResident;
    }

    public boolean estResident() {
        return typeCandidat == TypeCandidat.BoursierResident
                || typeCandidat == TypeCandidat.NonBoursierResident;
    }

    /* comparateur permettant de trier les voeux par ordre du groupe de classement */
    @Override
    public int compareTo(VoeuClasse voe) {
        return rang - voe.rang;
    }

}
