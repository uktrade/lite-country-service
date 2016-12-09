package uk.gov.bis.lite.countryservice.spire;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.junit.Rule;
import org.junit.Test;

import javax.xml.soap.SOAPMessage;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.containing;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static io.dropwizard.testing.FixtureHelpers.fixture;
import static org.assertj.core.api.Assertions.assertThat;

public class SpireGetCountriesClientTest {

  private static final String SOAP_URL = "http://localhost:9000/spirefox4dev/fox/ispire/SPIRE_COUNTRIES";
  private static final String SPIRE_CREDENTIALS = "credentials";

  @Rule
  public WireMockRule wireMockRule = new WireMockRule(9000);

  private SpireGetCountriesClient spireGetCountriesClient = new SpireGetCountriesClient(SOAP_URL, SPIRE_CREDENTIALS);

  @Test
  public void countriesByCountrySetId() throws Exception {

    stubFor(post(urlEqualTo("/spirefox4dev/fox/ispire/SPIRE_COUNTRIES"))
        .withRequestBody(containing("<spire:getCountries><countrySetId>EXPORT_CONTROL</countrySetId></spire:getCountries>"))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "text/xml")
            .withBody(fixture("spire-getCountrySet.xml"))));

    SOAPMessage response = spireGetCountriesClient.countriesByCountrySetId("EXPORT_CONTROL");

    assertThat(response).isNotNull();
  }

  @Test
  public void countriesByCountryGroupId() throws Exception {

    stubFor(post(urlEqualTo("/spirefox4dev/fox/ispire/SPIRE_COUNTRIES"))
      .withRequestBody(containing("<spire:getCountries><countryGroupId>EU</countryGroupId></spire:getCountries>"))
      .willReturn(aResponse()
        .withStatus(200)
        .withHeader("Content-Type", "text/xml")
        .withBody(fixture("spire-getCountryGroup.xml"))));

    SOAPMessage response = spireGetCountriesClient.countriesByCountryGroupId("EU");

    assertThat(response).isNotNull();
  }

}