
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
package parcoursup.propositions.test;

import java.sql.SQLException;
import java.util.Random;
import parcoursup.propositions.exemples.ExempleAleatoire;

public class TestAlgoPropositions {

    public static void main(String[] args) throws SQLException, Exception {

        Random random = new Random();

        /* 
        On genere des exemples aleatoires,
        pour les quels les calculs sont effectués et la procédure de
        validation des propriéts automatiquement exécutées.
        Si une propriété n'est pas validée, une exception est remontée.
        
        Ces tests ne permettent pas de couvrir tous les exemples possibles.
        
        Une procédure de certification du code est envisagée,
        afin de prouver la correction du code.
        
        */
        
        while (true) {
            ExempleAleatoire exempleAleatoire = new ExempleAleatoire(random.nextInt(1000));
            exempleAleatoire.execute();
            /* produit des fichiers xml de taille conséquente      
            exemple.executeAndLog();  */
        }

    }
}
