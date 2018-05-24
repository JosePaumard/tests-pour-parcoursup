package ordreappel;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        dryRun = false,
        format = {"pretty", "html:target/cucumber"},
        features = {"classpath:ordre-appel/"},
        glue = {"ordreappel"}
)
public class OrdreAppel41Test {
}
