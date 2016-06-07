package uk.gov.bis.lite.countryservice.resource;

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

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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

    List<Country> countryList = Arrays.asList(new Country("1", "France"), new Country("2", "Spain"));
    when(countriesService.getCountryList("export-control")).thenReturn(new CountryListEntry(countryList));

    List<Country> result = resources.client()
        .target("/countries/set/export-control")
        .request()
        .get(new GenericType<List<Country>>() {});

    assertThat(result, is(countryList));
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

}