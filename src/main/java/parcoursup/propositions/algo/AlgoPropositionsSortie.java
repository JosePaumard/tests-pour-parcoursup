
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

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.*;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class AlgoPropositionsSortie {

    /* liste des proposiitons à faire */
    public final Collection<VoeuEnAttente> propositions
            = new ArrayList<>();

    /* liste des voeux restant en attente. */    
    public final Collection<VoeuEnAttente> enAttente
            = new ArrayList<>();

    /* liste des internats, permettant de récupérer les positions max d'admission */
    public final Collection<GroupeInternat> internats
            = new ArrayList<>();

    /* sauvegarde des données */
    public void serialiser(String filename) throws JAXBException {
        if(filename == null) {
            filename = "sortie_" + LocalDateTime.now() + ".xml";
        }
        Marshaller m = JAXBContext.newInstance(AlgoPropositionsSortie.class).createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        m.marshal(this, new File(filename));
    }

}
