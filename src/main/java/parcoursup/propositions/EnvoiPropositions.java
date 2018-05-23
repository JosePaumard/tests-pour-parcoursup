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
package parcoursup.propositions;

import java.sql.SQLException;
import java.time.LocalDateTime;
import parcoursup.propositions.algo.AlgoPropositions;
import parcoursup.propositions.algo.AlgoPropositionsEntree;
import parcoursup.propositions.algo.AlgoPropositionsSortie;
import parcoursup.propositions.donnees.ConnecteurDonneesPropositionsOracle;

/* Le calcul des propositions à envoyer est effectué par le code suivant.
Ce code est exécuté de manière quotidienne.
 */
public class EnvoiPropositions {
    
    public static void main(String[] args) throws SQLException, Exception {
        
        if (args.length < 3) {
            log("Usage: envoiPropositions serveur login password");
            return;
        }
        
        ConnecteurDonneesPropositionsOracle acces
                = new ConnecteurDonneesPropositionsOracle(args[0], args[1], args[2]);
        
        log("Récupération des données");
        AlgoPropositionsEntree entree = acces.recupererDonnees();
        
        log("Sauvegarde local de l'entrée");
        entree.serialiser(null);
        
        log("Calcul des propositions");
        AlgoPropositionsSortie sortie = AlgoPropositions.calculePropositions(entree);

        log("Sauvegarde local de la sortie");
        sortie.serialiser(null);
        
        log("Export des données");
        acces.exporterDonnees(sortie);
        
    }
    
    static void log(String msg) {
        System.out.println(LocalDateTime.now().toLocalTime() + ": " + msg);
    }
}
