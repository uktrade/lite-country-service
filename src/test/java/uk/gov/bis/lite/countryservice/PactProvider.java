package uk.gov.bis.lite.countryservice;

import static io.dropwizard.testing.ResourceHelpers.resourceFilePath;

import au.com.dius.pact.provider.junit.PactRunner;
import au.com.dius.pact.provider.junit.Provider;
import au.com.dius.pact.provider.junit.State;
import au.com.dius.pact.provider.junit.loader.PactFolder;
import au.com.dius.pact.provider.junit.target.HttpTarget;
import au.com.dius.pact.provider.junit.target.Target;
import au.com.dius.pact.provider.junit.target.TestTarget;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.junit.ClassRule;
import org.junit.runner.RunWith;
import ru.vyarus.dropwizard.guice.injector.lookup.InjectorLookup;
import uk.gov.bis.lite.countryservice.config.CountryApplicationConfiguration;
import uk.gov.bis.lite.countryservice.mocks.CountryServiceMock;
import uk.gov.bis.lite.countryservice.service.CountryServicePactTestApplication;

@RunWith(PactRunner.class)
@Provider("lite-country-service")
//@PactBroker(host = "pact-broker.mgmt.licensing.service.trade.gov.uk.test", port = "80")
@PactFolder("pacts")
public class PactProvider {
  @ClassRule
  public static final DropwizardAppRule<CountryApplicationConfiguration> RULE =
      new DropwizardAppRule<>(CountryServicePactTestApplication.class, resourceFilePath("service-test-pact.yaml"));

  @TestTarget
  public final Target target = new HttpTarget(RULE.getLocalPort());

  @State("provided country group exists")
  public void countryGroupExists() {
    InjectorLookup.getInjector(RULE.getApplication()).get().getInstance(CountryServiceMock.class).setCountriesExist(true);
  }

  @State("provided country group does not exist")
  public void countryGroupDoesNotExist() {
    InjectorLookup.getInjector(RULE.getApplication()).get().getInstance(CountryServiceMock.class).setCountriesExist(false);
  }

  @State("provided country set exists")
  public void countrySetExists() {
    InjectorLookup.getInjector(RULE.getApplication()).get().getInstance(CountryServiceMock.class).setCountriesExist(true);
  }

  @State("provided country set does not exist")
  public void countrySetDoesNotExist() {
    InjectorLookup.getInjector(RULE.getApplication()).get().getInstance(CountryServiceMock.class).setCountriesExist(false);
  }
}
