package uk.gov.bis.lite.countryservice.resource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;

import io.dropwizard.testing.junit.ResourceTestRule;
import org.junit.Rule;
import org.junit.Test;
import uk.gov.bis.lite.countryservice.api.CountryView;
import uk.gov.bis.lite.countryservice.exception.CountryServiceException;
import uk.gov.bis.lite.countryservice.service.CountryService;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

public class CountryResourceTest {

  private CountryService countryService = mock(CountryService.class);

  @Rule
  public final ResourceTestRule resources = ResourceTestRule.builder()
      .addResource(new CountryResource(countryService, 1000))
      .build();

  @Test
  public void shouldGetCountrySetResource() throws Exception {

    List<CountryView> countries = Arrays.asList(new CountryView("CTRY1", "France", new String[]{}), new CountryView("CTRY2", "Spain", new String[]{}));
    when(countryService.getCountrySet("export-control")).thenReturn(Optional.of(countries));

    Response result = resources.client()
        .target("/countries/set/export-control")
        .request()
        .get();

    assertThat(result.getStatus()).isEqualTo(200);

    String expectedJson = "[{\"countryRef\":\"CTRY1\",\"countryName\":\"France\"}," +
        "{\"countryRef\":\"CTRY2\",\"countryName\":\"Spain\"}]";
    assertEquals(expectedJson,
        result.readEntity(String.class), false);
  }

  @Test
  public void shouldGetCountryGroupResource() throws Exception {

    List<CountryView> countries = Arrays.asList(new CountryView("CTRY1", "France", new String[]{}), new CountryView("CTRY2", "Spain", new String[]{}));
    when(countryService.getCountryGroup("eu")).thenReturn(Optional.of(countries));

    Response result = resources.client()
        .target("/countries/group/eu")
        .request()
        .get();

    assertThat(result.getStatus()).isEqualTo(200);

    String expectedJson = "[{\"countryRef\":\"CTRY1\",\"countryName\":\"France\"}," +
        "{\"countryRef\":\"CTRY2\",\"countryName\":\"Spain\"}]";
    assertEquals(expectedJson,
        result.readEntity(String.class), false);
  }

  @Test
  public void shouldReturn404StatusCodeWhenCountrySetNotFound() throws Exception {

    when(countryService.getCountrySet("blah")).thenReturn(Optional.empty());

    Response result = resources.client()
        .target("/countries/set/blah")
        .request()
        .get();

    Map<String, Object> map = result.readEntity(new GenericType<Map<String, Object>>() {
    });
    assertThat((int) map.get("code")).isEqualTo(404);
    assertThat((String) map.get("message")).isEqualTo("Country set does not exist - blah");
  }

  @Test
  public void shouldReturn404StatusCodeWhenCountryGroupNotFound() throws Exception {

    when(countryService.getCountryGroup("blah")).thenReturn(Optional.empty());

    Response result = resources.client()
        .target("/countries/group/blah")
        .request()
        .get();

    Map<String, Object> map = result.readEntity(new GenericType<Map<String, Object>>() {
    });
    assertThat((int) map.get("code")).isEqualTo(404);
    assertThat((String) map.get("message")).isEqualTo("Country group does not exist - blah");
  }

  @Test
  public void shouldReturn500StatusCodeForServiceException() throws Exception {

    when(countryService.getCountrySet("blah")).thenThrow(new CountryServiceException("service error", null));

    Response result = resources.client()
        .target("/countries/set/blah")
        .request()
        .get();

    Map<String, Object> map = result.readEntity(new GenericType<Map<String, Object>>() {
    });
    assertThat((int) map.get("code")).isEqualTo(500);
    assertThat((String) map.get("message")).isEqualTo("service error");
  }

  @Test
  public void shouldFilterCountriesWithNegativeId() throws Exception {

    List<CountryView> countries = Arrays.asList(new CountryView("CTRY1", "France", new String[]{}), new CountryView("CTRY2", "Spain", new String[]{}),
        new CountryView(CountryResource.NEGATIVE_COUNTRY_ID_PREFIX + "1", "Negative", new String[]{}));
    when(countryService.getCountrySet("export-control")).thenReturn(Optional.of(countries));

    Response result = resources.client()
        .target("/countries/set/export-control")
        .request()
        .get();

    assertThat(result.getStatus()).isEqualTo(200);

    String expectedJson = "[{\"countryRef\":\"CTRY1\",\"countryName\":\"France\"}," +
        "{\"countryRef\":\"CTRY2\",\"countryName\":\"Spain\"}]";
    assertEquals(expectedJson, result.readEntity(String.class), false);
  }

}