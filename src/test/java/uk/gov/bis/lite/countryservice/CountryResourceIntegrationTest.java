package uk.gov.bis.lite.countryservice;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static io.dropwizard.testing.FixtureHelpers.fixture;
import static io.dropwizard.testing.ResourceHelpers.resourceFilePath;
import static org.assertj.core.api.Assertions.assertThat;
import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.flywaydb.core.Flyway;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.bis.lite.countryservice.api.CountryView;
import uk.gov.bis.lite.countryservice.config.CountryApplicationConfiguration;

import java.util.List;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

public class CountryResourceIntegrationTest {

  private static final ObjectWriter WRITER = new ObjectMapper().writerWithDefaultPrettyPrinter();
  private static final Logger LOGGER = LoggerFactory.getLogger(CountryResourceIntegrationTest.class);

  private static final String URL = "http://localhost:8090/countries";

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

  @Before
  public void setupDatabase() {
    DataSourceFactory f = RULE.getConfiguration().getDataSourceFactory();
    Flyway flyway = new Flyway();
    flyway.setDataSource(f.getUrl(), f.getUser(), f.getPassword());
    flyway.migrate();
  }

  @Test
  public void shouldGetCountryList() throws Exception {

    Response response = JerseyClientBuilder.createClient()
        .target(URL + "/set/export-control")
        .request()
        .get();

    assertThat(response.getStatus()).isEqualTo(200);

    List<CountryView> countryList = response.readEntity(new GenericType<List<CountryView>>() {
    });

    assertThat(countryList).hasSize(10);
    assertThat(countryList).extracting(CountryView::getCountryName).contains(
        "Abu Dhabi", "Afghanistan", "Ajman", "Aland Islands", "Albania", "Alderney", "Algeria", "American Samoa", "Andorra", "Angola");

    // Verify country with synonyms
    CountryView abuDhabi = countryList.stream()
        .filter(countryView -> countryView.getCountryName().equals("Abu Dhabi"))
        .findAny()
        .get();

    assertThat(abuDhabi.getCountryRef()).isEqualTo("CTRY3");
    assertThat(abuDhabi.getSynonyms()).containsExactly("U.A.E.", "UAE", "United Arab Emirates");

    // Verify country without synonyms
    CountryView algeria = countryList.stream()
        .filter(countryView -> countryView.getCountryName().equals("Algeria"))
        .findAny()
        .get();

    assertThat(algeria.getCountryRef()).isEqualTo("CTRY293");
    assertThat(algeria.getSynonyms()).isEmpty();
  }

  @Test
  public void shouldReturn404StatusIfCountrySetNamesDoesNotExist() throws Exception {

    Response response = JerseyClientBuilder.createClient()
        .target(URL + "/set/MADE-UP")
        .request()
        .get();

    assertThat(response.getStatus()).isEqualTo(404);
    String expected = "{'code':404,'message':'Country set does not exist - MADE-UP'}";
    String actual = response.readEntity(String.class);
    assertEquals(toJson(expected), actual, true);
  }

  private String toJson(String str) {
    return str.replace("'", "\"");
  }

}
