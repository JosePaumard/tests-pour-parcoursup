
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

import java.sql.SQLException;
import java.util.Random;

public class DemoAlgoPropositions {

    public static void main(String[] args) throws SQLException, Exception {

        Random random = new Random();

        System.out.println("\n********** Exemple Aleatoire ************");
        ExempleAleatoire exempleAleatoire = new ExempleAleatoire(random.nextInt(10000));
        exempleAleatoire.execute();
        /* peut produire des fichiers de sortie de taille conséquente      
            exempleAleatoire.executeAndLog();  */

        System.out.println("********** Exemple B7, Jour 1 ************");
        ExempleB7Jour1 exempleB7Jour1 = new ExempleB7Jour1();
        exempleB7Jour1.executeAndLog();

        System.out.println("\n********** Exemple B7, Jour 2 ************");
        ExempleB7Jour2 exempleB7Jour2 = new ExempleB7Jour2();
        exempleB7Jour2.executeAndLog();

        System.out.println("\n********** Exemple B7, Jour 3 ************");
        ExempleB7Jour3 exempleB7Jour3 = new ExempleB7Jour3();
        exempleB7Jour3.executeAndLog();

    }
}
