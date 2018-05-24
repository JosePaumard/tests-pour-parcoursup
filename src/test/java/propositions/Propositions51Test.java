package propositions;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        dryRun = false,
        format = {"pretty", "html:target/cucumber"},
        features = {"classpath:propositions/"},
        glue = {"propositions"}
)
public class Propositions51Test {
}
