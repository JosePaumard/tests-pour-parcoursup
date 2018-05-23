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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.*;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class AlgoPropositionsEntree {

    /* La liste des groupes d'affecttaion, contenant leurs voeux respectifs */
    public final Collection<GroupeAffectation> groupesAffectations
            = new ArrayList<>();

    /* La liste des internats, contenant leurs voeux respectifs */
    public final Collection<GroupeInternat> internats
            = new ArrayList<>();

    /* verifie l'integrite des donnees d'entree et lève une exception si necessaire.
    Propriétés:
        a) tous les voeux sont en attente
        b) pas deux voeux distincts avec la même id
        c) pas deux candidats distincts avec le même classement, formation et internat
        d) pas le même candidat avec deux classement distincts, formation et internat
        e) classement positifs
        f) chaque voeu avec internat se retrouve dans l'internat correspondant
     */
    public void verifierIntegrite() {

        for (GroupeAffectation g : groupesAffectations) {

            /* integrite des classements: un classement == un candidat */
            Map<Integer, Integer> ordreVersCandidat
                    = new HashMap<>();
            Map<Integer, Integer> candidatVersOrdre
                    = new HashMap<>();
            Set<VoeuUID> voeuxVus = new HashSet<>();

            for (VoeuEnAttente v : g.voeux) {
                alerterSi(
                        v.internatDejaObtenu() && v.formationDejaObtenue(),
                        "a) ce voeu n'est pas en attente");

                alerterSi(
                        voeuxVus.contains(v.id),
                        "b) deux voeux avec la même id");

                voeuxVus.add(v.id);

                if (ordreVersCandidat.containsKey(v.ordreAppel)) {
                    alerterSi(
                            ordreVersCandidat.get(v.ordreAppel) != v.id.G_CN_COD,
                            "c) candidats distincst avec le même classement");
                } else {
                    ordreVersCandidat.put(v.ordreAppel, v.id.G_CN_COD);
                }

                if (candidatVersOrdre.containsKey(v.id.G_CN_COD)) {
                    alerterSi(
                            candidatVersOrdre.get(
                                    v.id.G_CN_COD) != (v.ordreAppel),
                            "d) candidats distincts avec le même classement");
                } else {
                    candidatVersOrdre.put(v.id.G_CN_COD, (v.ordreAppel));
                }

                alerterSi(v.ordreAppel <= 0,
                        "e) ordre appel formation négatif");

                /* remarque le voeu peut-être marqué "avecInternat"
                et en même temps internat==null car c'est un internat sans classement
                (obligatoire ou non-sélectif) */
                if (v.avecClassementInternat()) {
                    alerterSi(!v.internat.voeux.contains(v), "intégrité données");
                }
            }
        }

        for (GroupeInternat internat : internats) {

            /* integrite des classements: un classement == un candidat */
            Map<Integer, Integer> ordreVersCandidat
                    = new HashMap<>();
            Map<Integer, Integer> candidatVersOrdre
                    = new HashMap<>();

            for (VoeuEnAttente v : internat.voeux) {

                alerterSi(!v.avecInternat(), "intégrité données");

                alerterSi(v.internat != internat, "intégrité données");

                alerterSi(v.rangInternat <= 0,
                        "e) classement internat négatif");

                if (ordreVersCandidat.containsKey(v.rangInternat)) {
                    alerterSi(
                            ordreVersCandidat.get(v.rangInternat) != v.id.G_CN_COD,
                            "c) candidats distincst avec le même classement");
                } else {
                    ordreVersCandidat.put(v.rangInternat, v.id.G_CN_COD);
                }

                if (candidatVersOrdre.containsKey(v.id.G_CN_COD)) {
                    alerterSi(
                            candidatVersOrdre.get(
                                    v.id.G_CN_COD) != (v.rangInternat),
                            "d) candidats distincst avec le même classement");
                } else {
                    candidatVersOrdre.put(v.id.G_CN_COD, (v.rangInternat));
                }
            }
        }

    }

    /* permet de logger les calculs */
    public void serialiser(String filename) throws JAXBException {
        if(filename == null) {
            filename = "entree_" + LocalDateTime.now() + ".xml";
        }
        Marshaller m = JAXBContext.newInstance(AlgoPropositionsEntree.class).createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        m.marshal(this, new File(filename));
    }

    private void alerterSi(boolean prop, String message) {
        if (prop) {
            throw new RuntimeException("Donnees d'entree non integres: " + message);
        }
    }

}
