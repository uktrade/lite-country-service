package uk.gov.bis.lite.countryservice.integration;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;
import static com.github.tomakehurst.wiremock.client.WireMock.containing;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static io.dropwizard.testing.FixtureHelpers.fixture;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static ru.yandex.qatools.embed.postgresql.distribution.Version.Main.V9_5;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.testing.ConfigOverride;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.flywaydb.core.Flyway;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import ru.yandex.qatools.embed.postgresql.EmbeddedPostgres;
import uk.gov.bis.lite.countryservice.CountryServiceApplication;
import uk.gov.bis.lite.countryservice.api.CountryData;
import uk.gov.bis.lite.countryservice.config.CountryApplicationConfiguration;
import uk.gov.bis.lite.countryservice.util.AuthUtil;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

import javax.ws.rs.client.Entity;

public class BaseIntegrationTest {

  private static EmbeddedPostgres postgres;

  /**
   * Configured to bind port dynamically to mitigate bug https://github.com/tomakehurst/wiremock/issues/97
   * TODO revert to static port once bug is resolved
   */
  private WireMockRule wireMockRule;

  private DropwizardAppRule<CountryApplicationConfiguration> dwAppRule;

  @BeforeClass
  public static void pgSetup() {
    postgres = new EmbeddedPostgres(V9_5);
    try {
      postgres.start("localhost", 5432, "dbName", "postgres", "password");
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Before
  public void setUp() throws Exception {
    //Note all setup must be done in a single method (without use of @Rule/@ClassRule), so execution order can be guaranteed

    wireMockRule = new WireMockRule(options().dynamicPort());
    wireMockRule.start();

    //Configures stubFor to use allocated port
    configureFor("localhost", wireMockRule.port());

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
    dwAppRule = new DropwizardAppRule<>(CountryServiceApplication.class, "service-test.yaml",
        ConfigOverride.config("spireClientUrl", "http://localhost:" + wireMockRule.port() + "/spire/fox/ispire/"));
    dwAppRule.getTestSupport().before(); //This would be called automatically when using the @Rule annotation

    //Await country cache load - if tests start before the cache is populated they will fail
    await().with().pollInterval(1, SECONDS).atMost(20, SECONDS).until(() -> JerseyClientBuilder.createClient()
        .target("http://localhost:" + dwAppRule.getAdminPort() + "/admin/ready")
        .request()
        .header("Authorization", AuthUtil.SERVICE_USER)
        .get()
        .getStatus() == 200);

    CountryData uae = new CountryData("CTRY3", Arrays.asList("United Arab Emirates", "UAE", "U.A.E."));
    CountryData france = new CountryData("CTRY1434", Collections.singletonList("French Republic"));

    JerseyClientBuilder.createClient()
        .target("http://localhost:" + dwAppRule.getLocalPort() + "/country-data")
        .request()
        .header("Authorization", AuthUtil.ADMIN_USER)
        .put(Entity.json(Arrays.asList(uae, france)));
  }

  @After
  public void tearDown() throws Exception {
    //Delete all migrations so next test run resets them all
    DataSourceFactory f = dwAppRule.getConfiguration().getDataSourceFactory();
    Flyway flyway = new Flyway();
    flyway.setDataSource(f.getUrl(), f.getUser(), f.getPassword());
    flyway.clean();

    wireMockRule.stop();
    dwAppRule.getTestSupport().after();
    wireMockRule = null;
    dwAppRule = null;
  }

  @AfterClass
  public static void pgStop() {
    postgres.stop();
  }
}
