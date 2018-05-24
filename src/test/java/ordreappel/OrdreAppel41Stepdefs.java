package ordreappel;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.assertj.core.api.Assertions;
import parcoursup.ordreappel.algo.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class OrdreAppel41Stepdefs {

    private int tauxMinBoursier = 0;
    private int tauxMinResident = 0;
    private List<VoeuClasse> voeuxClasses;
    private List<String> candidatsClasses;


    @Given("^les candidats sont (.*)$")
    public void les_candidats_sont(String candidats) throws Throwable {
        int idCandidat = 0;
        voeuxClasses = new ArrayList<>();
        for (String candidat : candidats.split(" ")) {
            boolean estBoursier = candidat.startsWith("B") || candidat.startsWith("T");
            boolean estResident = candidat.startsWith("R") || candidat.startsWith("T");
            int rangCandidat = Integer.parseInt(candidat.substring(1));
            VoeuClasse voeuClasse = new VoeuClasse(idCandidat, rangCandidat, estBoursier, estResident);
            voeuxClasses.add(voeuClasse);
            idCandidat++;
        }

    }

    @And("^le taux minimum de boursiers est (\\d+)$")
    public void le_taux_minimum_de_boursiers_est(int qb) throws Throwable {
        this.tauxMinBoursier = qb;
    }

    @And("^le taux minimum de résidents est (\\d+)$")
    public void le_taux_minimum_de_residents_est(int qr) throws Throwable {
        this.tauxMinResident = qr;
    }

    @When("^l'appel est calculé$")
    public void l_appel_est_calcule() throws Throwable {
        GroupeClassement groupeClassement = new GroupeClassement(1, this.tauxMinBoursier, this.tauxMinResident);
        voeuxClasses.forEach(groupeClassement::ajouterVoeu);
        AlgoOrdreAppelEntree algoOrdreAppelEntree = new AlgoOrdreAppelEntree();
        algoOrdreAppelEntree.groupesClassements.add(groupeClassement);

        AlgoOrdreAppelSortie ordresAppels = AlgoOrdreAppel.calculeOrdresAppels(algoOrdreAppelEntree);
        OrdreAppel ordreAppel = ordresAppels.ordresAppel.values().iterator().next();

        this.candidatsClasses = ordreAppel.voeux.stream()
                .map(this::stringForCandidate)
                .collect(Collectors.toList());
    }

    @Then("^l'ordre d'appel est (.*)$")
    public void le_candidat_suivant_appele_est(String ordreAppel) throws Throwable {
        Assertions.assertThat(this.candidatsClasses).containsExactly(ordreAppel.split(" "));
    }

    private String stringForCandidate(VoeuClasse voeuClasse) {
        if (voeuClasse.estBoursier() && voeuClasse.estResident()) {
            return "T" + voeuClasse.rang;
        } else if (voeuClasse.estResident()) {
            return "R" + voeuClasse.rang;
        } else if (voeuClasse.estBoursier()) {
            return "B" + voeuClasse.rang;
        } else {
            return "C" + voeuClasse.rang;
        }
    }
}
