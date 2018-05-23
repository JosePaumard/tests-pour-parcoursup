
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
import java.util.Set;


/* Classe comprenant les aractéristiques 
identifiant de manière unique un voeu 
dans la base de données */
public class VoeuUID {

    /*l'identifiant unique du candidat dans la base de données */
    public final int G_CN_COD;

    /*l'identifiant unique de la formation d'affecttaion dans la base de données.
        Positionné à -1 pour les internats commun à plusieurs formations.*/
    public final int G_TA_COD;

    /* indique si le voeu comprend une demande d'internat */
    public final boolean I_RH_COD;

    public VoeuUID(
            int G_CN_COD,
            int G_TA_COD,
            boolean avecInternat) {
        this.G_CN_COD = G_CN_COD;
        this.G_TA_COD = G_TA_COD;
        this.I_RH_COD = avecInternat;

        /* vérification de l'unicité des voeux 
        récupérés via deux requêtes distinctes */
        if (verificationUnicite) {
            if (voeuxCrees.contains(this)) {
                throw new RuntimeException("Deux voeux créés avec le meme id");
            }
            voeuxCrees.add(this);
        }

    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof VoeuUID) {
            VoeuUID o = (VoeuUID) obj;
            return (this.G_CN_COD == o.G_CN_COD
                    && this.G_TA_COD == o.G_TA_COD
                    && this.I_RH_COD == o.I_RH_COD);
        } else {
            throw new RuntimeException("Test d'égalité imprévu");
        }
    }

    @Override
    public int hashCode() {
        return Long.hashCode(
                ((long) (this.I_RH_COD ? 1 : 0))
                ^ (((long) G_CN_COD) << 1)
                ^ (((long) G_TA_COD) << 32)
        );
    }

    /* Vérifie qu'un identifiant est créé au plus une fois */
    public static void debuterVerificationUnicite() {
        verificationUnicite = true;
        voeuxCrees.clear();
    }
    private static boolean verificationUnicite = false;
    private static final Set<VoeuUID> voeuxCrees = new HashSet<>();

}
