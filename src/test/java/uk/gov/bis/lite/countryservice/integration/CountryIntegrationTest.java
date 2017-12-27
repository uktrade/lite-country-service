package uk.gov.bis.lite.countryservice.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;

import org.glassfish.jersey.client.JerseyClientBuilder;
import org.junit.Test;
import uk.gov.bis.lite.countryservice.api.CountryView;
import uk.gov.bis.lite.countryservice.util.AuthUtil;

import java.util.List;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

public class CountryIntegrationTest extends BaseIntegrationTest {

  private static final String URL = "http://localhost:8090/countries";

  @Test
  public void shouldGetCountryList() {

    Response response = JerseyClientBuilder.createClient()
        .target(URL + "/set/export-control")
        .request()
        .header(AuthUtil.HEADER, AuthUtil.SERVICE_USER)
        .get();

    assertThat(response.getStatus()).isEqualTo(200);

    List<CountryView> countryList = response.readEntity(new GenericType<List<CountryView>>() {
    });

    assertThat(countryList).hasSize(10);
    assertThat(countryList).extracting(CountryView::getCountryName).containsExactly(
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
  public void shouldReturn404StatusIfCountrySetNameDoesNotExist() {

    Response response = JerseyClientBuilder.createClient()
        .target(URL + "/set/MADE-UP")
        .request()
        .header(AuthUtil.HEADER, AuthUtil.SERVICE_USER)
        .get();

    assertThat(response.getStatus()).isEqualTo(404);
    String expected = "{'code':404,'message':'Country set does not exist - MADE-UP'}";
    String actual = response.readEntity(String.class);
    assertEquals(toJson(expected), actual, true);
  }

  @Test
  public void shouldGetCountryGroup() {

    Response response = JerseyClientBuilder.createClient()
        .target(URL + "/group/eu")
        .request()
        .header(AuthUtil.HEADER, AuthUtil.SERVICE_USER)
        .get();

    assertThat(response.getStatus()).isEqualTo(200);

    List<CountryView> countryList = response.readEntity(new GenericType<List<CountryView>>() {
    });

    assertThat(countryList).hasSize(10);
    assertThat(countryList).extracting(CountryView::getCountryName).containsExactly(
        "Austria", "Belgium", "Bulgaria", "Croatia", "Cyprus", "Czech Republic", "Denmark", "Estonia", "Finland", "France");

    // Verify country with synonyms
    CountryView france = countryList.stream()
        .filter(countryView -> countryView.getCountryName().equals("France"))
        .findAny()
        .get();

    assertThat(france.getCountryRef()).isEqualTo("CTRY1434");
    assertThat(france.getSynonyms()).containsExactly("French Republic");

    // Verify country without synonyms
    CountryView austria = countryList.stream()
        .filter(countryView -> countryView.getCountryName().equals("Austria"))
        .findAny()
        .get();

    assertThat(austria.getCountryRef()).isEqualTo("CTRY781");
    assertThat(austria.getSynonyms()).isEmpty();
  }

  @Test
  public void shouldReturn404StatusIfCountryGroupNameDoesNotExist() {

    Response response = JerseyClientBuilder.createClient()
        .target(URL + "/group/MADE-UP")
        .request()
        .header(AuthUtil.HEADER, AuthUtil.SERVICE_USER)
        .get();

    assertThat(response.getStatus()).isEqualTo(404);
    String expected = "{'code':404,'message':'Country group does not exist - MADE-UP'}";
    String actual = response.readEntity(String.class);
    assertEquals(toJson(expected), actual, true);
  }

  private String toJson(String str) {
    return str.replace("'", "\"");
  }

}
