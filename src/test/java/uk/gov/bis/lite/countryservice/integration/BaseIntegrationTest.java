package uk.gov.bis.lite.countryservice.integration;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;
import static com.github.tomakehurst.wiremock.client.WireMock.containing;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static io.dropwizard.testing.FixtureHelpers.fixture;
import static io.dropwizard.testing.ResourceHelpers.resourceFilePath;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import io.dropwizard.testing.ConfigOverride;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.junit.After;
import org.junit.Before;
import uk.gov.bis.lite.countryservice.CountryServiceApplication;
import uk.gov.bis.lite.countryservice.api.CountryData;
import uk.gov.bis.lite.countryservice.config.CountryApplicationConfiguration;

import java.util.Arrays;
import java.util.Collections;

import javax.ws.rs.client.Entity;

public class BaseIntegrationTest {

  /**
   * Configured to bind port dynamically to mitigate bug https://github.com/tomakehurst/wiremock/issues/97
   * TODO revert to static port once bug is resolved
   */
  private WireMockRule wireMockRule;

  private DropwizardAppRule<CountryApplicationConfiguration> dwAppRule;

  @Before
  public void setUp() throws Exception {
    //Note all setup must be done in a single method (without use of @Rule/@ClassRule), so execution order can be guaranteed

    wireMockRule = new WireMockRule(options().dynamicPort());
    wireMockRule.start();

    //Configures stubFor to use allocated port
    configureFor("localhost",  wireMockRule.port());

    stubFor(post(urlEqualTo("/spire/fox/ispire/SPIRE_COUNTRIES"))
        .withRequestBody(containing("countrySetId"))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "text/xml")
            .withBody(fixture("spire-getCountrySet.xml"))));

    stubFor(post(urlEqualTo("/spire/fox/ispire/SPIRE_COUNTRIES"))
        .withRequestBody(containing("countryGroupId"))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "text/xml")
            .withBody(fixture("spire-getCountryGroup.xml"))));

    //Tell Dropwizard to use the dynamically allocated Wiremock port
    dwAppRule = new DropwizardAppRule<>(CountryServiceApplication.class, resourceFilePath("service-test.yaml"),
        ConfigOverride.config("spireClientUrl", "http://localhost:" +  wireMockRule.port() + "/spire/fox/ispire/"));
    dwAppRule.getTestSupport().before(); //This would be called automatically when using the @Rule annotation

    //Await country cache load - if tests start before the cache is populated they will fail
    await().with().pollInterval(1, SECONDS).atMost(10, SECONDS).until(() -> JerseyClientBuilder.createClient()
        .target("http://localhost:"+ dwAppRule.getAdminPort()+"/ready")
        .request()
        .get()
        .getStatus() == 200);

    JerseyClientBuilder.createClient()
        .target("http://localhost:"+ dwAppRule.getLocalPort()+"/country-data")
        .request()
        .delete();

    CountryData uae = new CountryData("CTRY3", Arrays.asList("United Arab Emirates","UAE","U.A.E."));
    CountryData france = new CountryData("CTRY1434", Collections.singletonList("French Republic"));

    JerseyClientBuilder.createClient()
        .target("http://localhost:"+ dwAppRule.getLocalPort()+"/country-data")
        .request()
        .header("Authorization", "Basic dXNlcjpwYXNzd29yZA==")
        .put(Entity.json(Arrays.asList(uae, france)));
  }

  @After
  public void tearDown() throws Exception {
    wireMockRule.stop();
    dwAppRule.getTestSupport().after();
    wireMockRule = null;
    dwAppRule = null;
  }
}
