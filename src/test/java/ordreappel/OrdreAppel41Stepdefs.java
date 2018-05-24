package ordreappel;

import cucumber.api.PendingException;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.assertj.core.api.Assertions;
import parcoursup.ordreappel.algo.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class OrdreAppel41Stepdefs {

    private int tauxMinBoursier;
    private List<VoeuClasse> voeuxClasses;
    private List<String> candidatsClasses;


    @Given("^les candidats sont (.*)$")
    public void les_candidats_sont(String candidats) throws Throwable {
        int idCandidat = 0;
        voeuxClasses = new ArrayList<>();
        for (String candidat : candidats.split(" ")) {
            boolean estBoursier = candidat.startsWith("B");
            int rangCandidat = Integer.parseInt(candidat.substring(1));
            VoeuClasse voeuClasse = new VoeuClasse(idCandidat, rangCandidat, estBoursier, false);
            voeuxClasses.add(voeuClasse);
            idCandidat++;
        }

    }

    @And("^le taux minimum de boursiers est (\\d+)$")
    public void le_taux_minimum_de_boursiers_est(int qb) throws Throwable {
        this.tauxMinBoursier = qb;
    }

    @When("^l'appel est calculé$")
    public void l_appel_est_calcule() throws Throwable {
        GroupeClassement groupeClassement = new GroupeClassement(1, this.tauxMinBoursier, 0);
        voeuxClasses.forEach(groupeClassement::ajouterVoeu);
        AlgoOrdreAppelEntree algoOrdreAppelEntree = new AlgoOrdreAppelEntree();
        algoOrdreAppelEntree.groupesClassements.add(groupeClassement);

        AlgoOrdreAppelSortie ordresAppels = AlgoOrdreAppel.calculeOrdresAppels(algoOrdreAppelEntree);
        OrdreAppel ordreAppel = ordresAppels.ordresAppel.values().iterator().next();

        this.candidatsClasses = ordreAppel.voeux.stream()
                .map(voeuClasse -> (voeuClasse.estBoursier() ? "B" : "C") + voeuClasse.rang)
                .collect(Collectors.toList());
    }

    @Then("^l'ordre d'appel est (.*)$")
    public void le_candidat_suivant_appele_est(String ordreAppel) throws Throwable {
        Assertions.assertThat(this.candidatsClasses).containsExactly(ordreAppel.split(" "));
    }
}
