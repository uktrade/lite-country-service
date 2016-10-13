package uk.gov.bis.lite.countryservice.resource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;

import io.dropwizard.testing.junit.ResourceTestRule;
import org.junit.Rule;
import org.junit.Test;
import uk.gov.bis.lite.countryservice.cache.CountryListEntry;
import uk.gov.bis.lite.countryservice.exception.CountryServiceException;
import uk.gov.bis.lite.countryservice.exception.CountryServiceException.ServiceExceptionMapper;
import uk.gov.bis.lite.countryservice.exception.CountrySetNotFoundException;
import uk.gov.bis.lite.countryservice.exception.CountrySetNotFoundException.NotFoundExceptionMapper;
import uk.gov.bis.lite.countryservice.model.Country;
import uk.gov.bis.lite.countryservice.service.CountriesService;

import java.util.Arrays;
import java.util.List;

import javax.ws.rs.core.Response;

public class CountriesResourceTest {

  private CountriesService countriesService = mock(CountriesService.class);

  @Rule
  public final ResourceTestRule resources = ResourceTestRule.builder()
      .addResource(new CountriesResource(countriesService, 1000))
      .addProvider(NotFoundExceptionMapper.class)
      .addProvider(ServiceExceptionMapper.class)
      .build();

  @Test
  public void shouldGetCountriesResource() throws Exception {

    List<Country> countryList = Arrays.asList(new Country("CTRY1", "France"), new Country("CTRY2", "Spain"));
    when(countriesService.getCountryList("export-control")).thenReturn(new CountryListEntry(countryList));

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
  public void shouldReturn404StatusCodeWhenControlCodeNotFound() throws Exception {

    when(countriesService.getCountryList("blah")).thenThrow(new CountrySetNotFoundException("not found error"));

    Response result = resources.client()
        .target("/countries/set/blah")
        .request()
        .get();

    assertThat(result.getStatus()).isEqualTo(404);

    assertThat(result.readEntity(String.class)).isEqualTo("not found error");
  }

  @Test
  public void shouldReturn500StatusCodeForServiceException() throws Exception {

    when(countriesService.getCountryList("blah")).thenThrow(new CountryServiceException("service error", null));

    Response result = resources.client()
        .target("/countries/set/blah")
        .request()
        .get();

    assertThat(result.getStatus()).isEqualTo(500);

    assertThat(result.readEntity(String.class)).isEqualTo("service error");
  }

  @Test
  public void shouldFilterCountriesWithNegativeId() throws Exception {

    List<Country> countryList = Arrays.asList(new Country("CTRY1", "France"), new Country("CTRY2", "Spain"),
        new Country(CountriesResource.NEGATIVE_COUNTRY_ID_PREFIX + "1", "Negative"));
    when(countriesService.getCountryList("export-control")).thenReturn(new CountryListEntry(countryList));

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