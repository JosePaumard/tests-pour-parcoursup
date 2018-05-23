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

import javax.xml.bind.JAXBException;

import parcoursup.propositions.algo.AlgoPropositions;
import parcoursup.propositions.algo.AlgoPropositionsEntree;
import parcoursup.propositions.algo.AlgoPropositionsSortie;

public abstract class ExemplePropositions {

    /* nom de l'exemple */
    abstract String nom();

    /* cree des données d'entrée */
    abstract AlgoPropositionsEntree donneesEntree() throws Exception;

    public void execute() throws JAXBException, Exception {

        AlgoPropositions.calculePropositions(donneesEntree());

    }

    public void executeAndLog() throws JAXBException, Exception {

        AlgoPropositionsEntree entree = donneesEntree();

        AlgoPropositionsSortie sortie
                = AlgoPropositions.calculePropositions(entree);

        entree.serialiser(nom() + "_entree.xml");
        sortie.serialiser(nom() + "_sortie.xml");

    }

}
