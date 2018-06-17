/*
 * Copyright (C) 2018 José Paumard
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package propositions;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.assertj.core.api.Assertions;
import org.mockito.Mockito;
import parcoursup.propositions.algo.*;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Propositions51InternatStepDef {
    private int rangLimite;
    private int capacite;
    private List<String> internesClasses;
    private List<String> candidatsSansInternat;
    private GroupeInternat groupeInternat;
    private GroupeAffectation groupeAffectation;
    private List<String> candidatsFormation;
    private List<String> candidatsInternat;
    private AlgoPropositionsSortie sortie;
    private int capaciteFormation;

    @Given("^une formation dont le rang limite de proposition est (\\d+) et dont la capacité d'accueil est (\\d+)$")
    public void une_formation_dont_le_rang_limite_de_proposition_est(int rangLimite, int capaciteFormation) {
        this.rangLimite = rangLimite;
        this.capaciteFormation = capaciteFormation;
    }
    @And("^un internat dont la capacité d'accueil est (\\d+)$")
    public void un_internat_dont_la_capacite_d_accueil_est(int capaciteInternat) {
        this.capacite = capaciteInternat;
    }

    @And("^la valeur de B est (\\d+)$")
    public void la_valeur_de_B_est(int B) {

        GroupeAffectationUID id = new GroupeAffectationUID(1, 1, 1);

        groupeAffectation = new GroupeAffectation(capaciteFormation, id, rangLimite);

        GroupeInternatUID internatUID = new GroupeInternatUID(1, groupeAffectation.id.G_TA_COD);
        groupeInternat = new GroupeInternat(internatUID, capacite, 10);
        groupeInternat = Mockito.spy(groupeInternat);
        Mockito.doReturn(B).when(groupeInternat).getAssietteAdmission(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt());
    }

    @And("^les candidats à la formation sont (.*)$")
    public void les_candidats_a_la_formation_sont(String candidatsFormation) {
        this.candidatsFormation = Arrays.stream(candidatsFormation.split(" ")).collect(Collectors.toList());
    }

    @And("^les candidats à l'internat sont (.*)$")
    public void les_candidats_a_l_internat_sont(String candidatsInternat) {
        this.candidatsInternat = Arrays.stream(candidatsInternat.split(" ")).collect(Collectors.toList());
    }

    @And("^les candidats à la formation sans internat sont (.*)$")
    public void les_candidats_a_la_formation_sans_internat_sont(String candidatsFormationUniquement) {
        this.candidatsSansInternat = Arrays.stream(candidatsFormationUniquement.split(" ")).collect(Collectors.toList());
    }

    @And("^le classement à l'internat est (.*)$")
    public void le_classement_a_l_internat_est(String internesClasses) {
        this.internesClasses = Arrays.stream(internesClasses.split(" ")).collect(Collectors.toList());
    }

    @When("^l'ordre d'appel est calculé$")
    public void l_ordre_d_appel_est_calcule() {

        for (String candidat : candidatsInternat) {
            int g_cn_cod = Integer.parseInt(candidat.substring(1));
            int ordreAppel = candidatsFormation.indexOf(candidat) + 1;
            int rangInternat = internesClasses.indexOf(candidat) + 1;
            VoeuEnAttente.ajouterVoeu(g_cn_cod, groupeAffectation, ordreAppel, groupeInternat, rangInternat);
        }

        for (String candidat : candidatsSansInternat) {
            int g_cn_cod = Integer.parseInt(candidat.substring(1));
            int ordreAppel = candidatsFormation.indexOf(candidat) + 1;
            VoeuEnAttente.ajouterVoeu(g_cn_cod, false, groupeAffectation, ordreAppel);
        }

        AlgoPropositionsEntree entree = new AlgoPropositionsEntree();
        entree.internats.add(this.groupeInternat);
        entree.groupesAffectations.add(this.groupeAffectation);
        GroupeInternat.nbJoursCampagne = 1;

        sortie = AlgoPropositions.calculePropositions(entree);
        // sortie.serialiser("xml/sortie.xml");
    }

    @Then("^les candidats suivants reçoivent une proposition pour la formation (.*)$")
    public void les_candidats_suivants_recoivent_une_proposition_pour_la_formation(String candidatsRetenusFormation) {

        List<Integer> idCandidatsRetenus = sortie.propositions.stream()
                .sorted(Comparator.comparing(voeuEnAttente -> voeuEnAttente.ordreAppel))
                .map(voeuEnAttente -> voeuEnAttente.id.G_CN_COD)
                .distinct()
                .collect(Collectors.toList());

        List<Integer> candidatsRetenusFormationAttendus = Arrays.stream(candidatsRetenusFormation.split(" "))
                .filter(candidat -> !candidat.equals(("-")))
                .map(candidat -> Integer.parseInt(candidat.substring(1)))
                .collect(Collectors.toList());
        Assertions.assertThat(idCandidatsRetenus).containsExactlyElementsOf(candidatsRetenusFormationAttendus);
    }

    @And("^les candidats suivants reçoivent une proposition pour l'internat (.*)$")
    public void les_candidats_suivants_recoivent_une_proposition_pour_l_internat(String candidatsRetenusInternat) {

        List<Integer> idCandidatsRetenusInternat = sortie.propositions.stream()
                .sorted(Comparator.comparing(voeuEnAttente -> voeuEnAttente.rangInternat))
                .filter(voeuEnAttente -> voeuEnAttente.id.I_RH_COD)
                .map(voeuEnAttente -> voeuEnAttente.id.G_CN_COD)
                .distinct()
                .collect(Collectors.toList());

        List<Integer> candidatsRetenusAttendus = Arrays.stream(candidatsRetenusInternat.split(" "))
                .filter(candidat -> !candidat.equals(("-")))
                .map(candidat -> Integer.parseInt(candidat.substring(1)))
                .collect(Collectors.toList());
        Assertions.assertThat(idCandidatsRetenusInternat).containsExactlyElementsOf(candidatsRetenusAttendus);
    }

    @And("^les candidats suivants sont en attente pour l'internat (.*)$")
    public void les_candidats_suivants_sont_en_attente_pour_l_internat(String candidatsEnAttenteAttendus) {

        List<Integer> idCandidatsEnAttente = sortie.enAttente.stream()
                .sorted(Comparator.comparing(voeuEnAttente -> voeuEnAttente.rangInternat))
                .filter(voeuEnAttente -> voeuEnAttente.id.I_RH_COD)
                .map(voeuEnAttente -> voeuEnAttente.id.G_CN_COD)
                .distinct()
                .collect(Collectors.toList());

        List<Integer> candidatsEnAttenteAttendusTab = Arrays.stream(candidatsEnAttenteAttendus.split(" "))
                .filter(candidat -> !candidat.equals(("-")))
                .map(candidat -> Integer.parseInt(candidat.substring(1)))
                .collect(Collectors.toList());

        Assertions.assertThat(idCandidatsEnAttente).containsExactlyElementsOf(candidatsEnAttenteAttendusTab);
    }
}
