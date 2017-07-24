package uk.gov.bis.lite.countryservice.resource;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;

import io.dropwizard.testing.junit.ResourceTestRule;
import org.junit.Rule;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONCompareMode;
import uk.gov.bis.lite.countryservice.api.CountryView;
import uk.gov.bis.lite.countryservice.service.CountryService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.ws.rs.core.Response;

public class CountryResourceTest {

  private final CountryService countryService = mock(CountryService.class);

  @Rule
  public final ResourceTestRule resources = ResourceTestRule.builder()
      .addResource(new CountryResource(countryService, 1000))
      .build();

  @Test
  public void shouldGetCountrySetResource() {

    List<CountryView> countries = Arrays.asList(
        new CountryView("CTRY1", "France", new ArrayList<>()),
        new CountryView("CTRY2", "Spain", singletonList("España")));
    when(countryService.getCountrySet("export-control")).thenReturn(Optional.of(countries));

    Response result = resources.client()
        .target("/countries/set/export-control")
        .request()
        .get();

    assertThat(result.getStatus()).isEqualTo(200);

    String expected = "[{'countryRef':'CTRY1','countryName':'France','synonyms':[]},{'countryRef':'CTRY2','countryName':'Spain','synonyms':['España']}]";
    String actual = result.readEntity(String.class);
    assertEquals(toJson(expected), actual, JSONCompareMode.NON_EXTENSIBLE);
  }

  @Test
  public void shouldFilterNegativeIdFromCountrySet() {

    List<CountryView> countries = Arrays.asList(
        new CountryView("CTRY2", "Spain", singletonList("España")),
        new CountryView(CountryResource.NEGATIVE_COUNTRY_ID_PREFIX + "1", "Negative", new ArrayList<>()));
    when(countryService.getCountrySet("export-control")).thenReturn(Optional.of(countries));

    Response result = resources.client()
        .target("/countries/set/export-control")
        .request()
        .get();

    assertThat(result.getStatus()).isEqualTo(200);

    String expected = "[{'countryRef':'CTRY2','countryName':'Spain','synonyms':['España']}]";
    String actual = result.readEntity(String.class);
    assertEquals(toJson(expected), actual, true);
  }

  @Test
  public void shouldReturn404StatusCodeWhenCountrySetNotFound() {

    when(countryService.getCountrySet("blah")).thenReturn(Optional.empty());

    Response result = resources.client()
        .target("/countries/set/blah")
        .request()
        .get();

    assertThat(result.getStatus()).isEqualTo(404);

    String expected = "{'code':404,'message':'Country set does not exist - blah'}";
    String actual = result.readEntity(String.class);
    assertEquals(expected, actual, true);
  }

  @Test
  public void shouldGetCountryGroupResource() {

    List<CountryView> countries = Arrays.asList(
        new CountryView("CTRY1", "France", new ArrayList<>()),
        new CountryView("CTRY2", "Spain", singletonList("España")));
    when(countryService.getCountryGroup("eu")).thenReturn(Optional.of(countries));

    Response result = resources.client()
        .target("/countries/group/eu")
        .request()
        .get();

    assertThat(result.getStatus()).isEqualTo(200);

    String expected = "[{'countryRef':'CTRY1','countryName':'France','synonyms':[]},{'countryRef':'CTRY2','countryName':'Spain','synonyms':['España']}]";
    String actual = result.readEntity(String.class);
    assertEquals(toJson(expected), actual, JSONCompareMode.NON_EXTENSIBLE);
  }

  @Test
  public void shouldReturn404StatusCodeWhenCountryGroupNotFound() {

    when(countryService.getCountryGroup("blah")).thenReturn(Optional.empty());

    Response result = resources.client()
        .target("/countries/group/blah")
        .request()
        .get();

    assertThat(result.getStatus()).isEqualTo(404);

    String expected = "{'code':404,'message':'Country group does not exist - blah'}";
    String actual = result.readEntity(String.class);
    assertEquals(expected, actual, true);
  }

  @Test
  public void shouldFilterNegativeIdFromCountryGroup() {

    List<CountryView> countries = Arrays.asList(
        new CountryView("CTRY2", "Spain", singletonList("España")),
        new CountryView(CountryResource.NEGATIVE_COUNTRY_ID_PREFIX + "1", "Negative", new ArrayList<>()));
    when(countryService.getCountryGroup("eu")).thenReturn(Optional.of(countries));

    Response result = resources.client()
        .target("/countries/group/eu")
        .request()
        .get();

    assertThat(result.getStatus()).isEqualTo(200);

    String expected = "[{'countryRef':'CTRY2','countryName':'Spain','synonyms':['España']}]";
    String actual = result.readEntity(String.class);
    assertEquals(toJson(expected), actual, true);
  }

  private String toJson(String str) {
    return str.replace("'", "\"");
  }

}
