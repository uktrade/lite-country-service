package uk.gov.bis.lite.countryservice.core.service;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.junit.Rule;
import org.junit.Test;
import uk.gov.bis.lite.countryservice.spire.SpireGetCountriesClient;

import javax.xml.soap.SOAPMessage;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static io.dropwizard.testing.FixtureHelpers.fixture;

public class SpireGetCountriesClientTest {

  private static final String SOAP_URL = "http://localhost:9000/spirefox4dev/fox/ispire/SPIRE_COUNTRIES";
  private static final String SPIRE_CREDENTIALS = "credentials";

  @Rule
  public WireMockRule wireMockRule = new WireMockRule(9000);

  private SpireGetCountriesClient spireGetCountriesClient = new SpireGetCountriesClient(SOAP_URL, SPIRE_CREDENTIALS);

  @Test
  public void shouldExecuteSoapRequest() throws Exception {

    stubFor(post(urlEqualTo("/spirefox4dev/fox/ispire/SPIRE_COUNTRIES"))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "text/xml")
            .withBody(fixture("spire-getCountries.xml"))));

    SOAPMessage soapMessage = spireGetCountriesClient.executeRequest("EXPORT_CONTROL");

    soapMessage.writeTo(System.out);
  }
}