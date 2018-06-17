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

import cucumber.api.PendingException;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.assertj.core.api.Assertions;
import parcoursup.propositions.algo.GroupeInternat;

public class Propositions51BMaxStepDef {

    private int L;
    private int M;
    private int ouverture;
    private int jour;

    @Given("^un internat disposant de (\\d+) places$")
    public void un_internat_disposant_de(int L) throws Throwable {
        this.L = L;
    }

    @And("^une liste de (\\d+) candidats$")
    public void une_liste_de_candidats(int M) throws Throwable {
        this.M = M;
    }

    @And("^un taux d'ouverture de (\\d+)$")
    public void un_taux_ouverture_de(int ouverture) throws Throwable {
        this.ouverture = ouverture;
    }

    @When("^l'appel est lancé le jour (\\d+)$")
    public void l_appel_est_lance_le_jour(int jour) throws Throwable {
        this.jour = jour;
    }

    @Then("^(\\d+) sont appelés$")
    public void candidats_sont_appeles(int expectedBmax) throws Throwable {
        GroupeInternat groupeInternat = new GroupeInternat(null, L, ouverture);
        int bmax = groupeInternat.getAssietteAdmission(M, L, jour, ouverture);
        Assertions.assertThat(bmax).isEqualTo(expectedBmax);
    }
}
