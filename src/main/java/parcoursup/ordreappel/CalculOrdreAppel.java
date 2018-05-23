
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
package parcoursup.ordreappel;

import java.sql.SQLException;
import parcoursup.ordreappel.algo.AlgoOrdreAppel;
import parcoursup.ordreappel.algo.AlgoOrdreAppelEntree;
import parcoursup.ordreappel.algo.AlgoOrdreAppelSortie;
import parcoursup.ordreappel.donnees.ConnecteurDonneesAppelOracle;

/* Le calcul des ordres d'appel dans Parcoursup 
    et leur enregistrement dans la base de données est effectué par le code suivant.
    Ce code est exécuté une fois en début de campagne,  à une date située entre
    la réception des classements et des taux et l'envoi des premières propositions. */
public class CalculOrdreAppel {

    public static void main(String[] args) throws SQLException, Exception {

        if (args.length < 3) {
            System.out.println("Usage: calculeOrdreAppel serveur login password");
            return;
        }

        ConnecteurDonneesAppelOracle acces
                = new ConnecteurDonneesAppelOracle(args[0], args[1], args[2]);

        AlgoOrdreAppelEntree entree = acces.recupererDonneesOrdreAppel();

        AlgoOrdreAppelSortie sortie = AlgoOrdreAppel.calculeOrdresAppels(entree);

        acces.exporterDonneesOrdresAppel(sortie);

    }
}
