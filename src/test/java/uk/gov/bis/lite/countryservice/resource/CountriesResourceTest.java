package uk.gov.bis.lite.countryservice.resource;

import io.dropwizard.testing.junit.ResourceTestRule;
import org.junit.ClassRule;
import org.junit.Test;
import uk.gov.bis.lite.countryservice.cache.CountryListEntry;
import uk.gov.bis.lite.countryservice.core.service.GetCountriesService;
import uk.gov.bis.lite.countryservice.model.Country;

import javax.ws.rs.core.GenericType;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CountriesResourceTest {

  private static final GetCountriesService getCountriesService = mock(GetCountriesService.class);

  @ClassRule
  public static final ResourceTestRule resources = ResourceTestRule.builder()
      .addResource(new CountriesResource(getCountriesService, 1000))
      .build();

  @Test
  public void shouldGetCountriesResource() throws Exception {

    List<Country> countryList = Arrays.asList(new Country("1", "France"), new Country("2", "Spain"));
    when(getCountriesService.getCountryList("export-control")).thenReturn(Optional.of(new CountryListEntry(countryList)));


    List<Country> result = resources.client()
        .target("/countries/set/export-control")
        .request()
        .get(new GenericType<List<Country>>() {
        });

    assertThat(result, is(countryList));
  }

}