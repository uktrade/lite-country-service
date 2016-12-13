package uk.gov.bis.lite.countryservice;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import uk.gov.bis.lite.countryservice.config.CountryApplicationConfiguration;
import uk.gov.bis.lite.countryservice.spire.model.SpireCountry;

import javax.ws.rs.client.Client;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static io.dropwizard.testing.FixtureHelpers.fixture;
import static io.dropwizard.testing.ResourceHelpers.resourceFilePath;
import static org.assertj.core.api.Assertions.assertThat;

public class SpireCountryServiceIntegrationTest {

  @ClassRule
  public static final WireMockRule wireMockRule = new WireMockRule(9000);

  @Rule
  public final DropwizardAppRule<CountryApplicationConfiguration> RULE =
      new DropwizardAppRule<>(CountryServiceApplication.class, resourceFilePath("service-test.yaml"));

  @BeforeClass
  public static void setUp() throws Exception {

    stubFor(post(urlEqualTo("/spirefox4dev/fox/ispire/SPIRE_COUNTRIES"))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "text/xml")
            .withBody(fixture("spire-getCountrySet.xml"))));
  }

  @Test
  @Ignore
  public void shouldGetCountryList() throws Exception {

    Client client = new JerseyClientBuilder().build();

    Response response = client.target("http://localhost:8090/countries/set/export-control")
        .request()
        .get();

    assertThat(response.getStatus()).isEqualTo(200);

    List<SpireCountry> spireCountryList = response.readEntity(new GenericType<List<SpireCountry>>() {});
    assertThat(spireCountryList).isNotNull();
    assertThat(spireCountryList).isNotEmpty();
  }

  @Test
  @Ignore
  public void shouldReturn404StatusIfCountrySetNamesDoesNotExist() throws Exception {

    Client client = new JerseyClientBuilder().build();

    Response response = client.target("http://localhost:8090/countries/set/blah")
        .request()
        .get();

    assertThat(response.getStatus()).isEqualTo(404);

    assertThat(response.readEntity(String.class))
        .isEqualTo("The following country set name does not exist - blah");
  }

}
