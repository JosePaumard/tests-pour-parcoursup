
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

import javax.xml.bind.annotation.XmlTransient;

public class VoeuEnAttente {

    /* caractéristiques identifiant de manière unique le voeu dans la base de données */
    public final VoeuUID id;

    /* groupe d'affectation du voeu */
    @XmlTransient
    public final GroupeAffectation groupe;

    /* rang du voeu dans l ordre d'appel */
    public final int ordreAppel;

    /* y a t il une demande d'internat sur ce voeu ? */
    public boolean avecInternat() {
        return id.I_RH_COD;
    }

    /* y a t il une demande d'internat avec classement sur ce voeux ? */
    public boolean avecClassementInternat() {
        return internat != null;
    }

    /* le candidat a t'il déjà une offre dans cet internat (pourune autre formation) ?*/
    public final boolean internatDejaObtenu() {
        return internat != null && internat.estAffecte(id.G_CN_COD);
    }

    /* le candidat a t'il déjà une offre dans cette formation (sans internat)? */
    public final boolean formationDejaObtenue() {
        return groupe != null && groupe.estAffecte(id.G_CN_COD);
    }

    /* le rang du candidat au classement internat */
    public final int rangInternat;

    /* le groupe de classement internat,
    qui donne accès à la position d'admission */
    @XmlTransient
    final GroupeInternat internat;

    public GroupeInternatUID internatID() {
        return internat == null ? null : internat.id;
    }

    /* resultat du calcul: fait t'on une proposition sur ce voeu? */
    private boolean aProposer = false;

    public boolean estAProposer() {
        return aProposer;
    }

    public void proposer() {
        aProposer = true;
    }

    public void conserverEnAttente() {
        aProposer = false;
    }

    /* rang sur liste attente */
    public int rangListeAttente = 0;

    /* rang sur liste attente internat */
    public int rangListeAttenteInternat = 0;

    /* vérifie si le voeu est désactivé du fait d'une demande d'internat */
    public boolean estDesactiveParPositionAdmissionInternat() {
        /* si le candidat demande l'internat mais que son classement
            a l'internat ne passe pas la barre définie par la position
            d'admission, alors on en fait pas de proposition */
        return ((internat != null)
                && !internatDejaObtenu()
                && rangInternat > internat.positionAdmission);
    }

    /* crée un voeu sans internat avec classement et l'ajoute dans le groupe */
    public static VoeuEnAttente ajouterVoeu(
            int G_CN_COD,
            boolean avecInternat,
            GroupeAffectation groupe,
            int ordreAppel
    ) {
        VoeuEnAttente voeu = new VoeuEnAttente(
                new VoeuUID(G_CN_COD, groupe.id.G_TA_COD, avecInternat),
                groupe, ordreAppel);
        groupe.ajouterVoeu(voeu);
        return voeu;
    }

    /* crée un voeu avec internat avec classement 
    et l'ajoute dans le groupe et l'internat correspondant */
    public static VoeuEnAttente ajouterVoeu(
            int G_CN_COD,
            GroupeAffectation groupe,
            int ordreAppel,
            GroupeInternat internat,
            int rangInternat
    ) {
        VoeuEnAttente voeu = new VoeuEnAttente(
                new VoeuUID(G_CN_COD, groupe.id.G_TA_COD, true),
                groupe, ordreAppel, internat, rangInternat);
        groupe.ajouterVoeu(voeu);
        internat.ajouterVoeu(voeu, groupe);
        return voeu;
    }

    /* constructeur d'un voeu sans internat ou avec internat obligatoire ou non-sélectif */
    private VoeuEnAttente(
            VoeuUID id,
            GroupeAffectation groupe,
            int ordreAppel
    ) {
        this.id = id;
        this.groupe = groupe;
        this.ordreAppel = ordreAppel;
        this.internat = null;
        this.rangInternat = 0;
    }

    /* constructeur d'un voeu avec internat */
    private VoeuEnAttente(
            VoeuUID id,
            GroupeAffectation groupe,
            int ordreAppel,
            GroupeInternat internat,
            int rangInternat
    ) {
        this.id = id;
        this.groupe = groupe;
        this.ordreAppel = ordreAppel;
        this.internat = internat;
        this.rangInternat = rangInternat;
    }
 
}
