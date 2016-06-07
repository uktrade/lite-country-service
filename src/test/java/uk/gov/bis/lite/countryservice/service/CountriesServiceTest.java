package uk.gov.bis.lite.countryservice.service;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.bis.lite.countryservice.cache.CountryListCache;
import uk.gov.bis.lite.countryservice.cache.CountryListEntry;
import uk.gov.bis.lite.countryservice.exception.CountrySetNotFoundException;
import uk.gov.bis.lite.countryservice.model.Country;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CountriesServiceTest {

  private static final String COUNTRY_SET_NAME = "export-control";

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Mock
  private CountryListCache countryListCache;

  @InjectMocks
  private CountriesService countriesService;

  @Test
  public void shouldGetCountries() throws Exception {

    List<Country> countryList = Arrays.asList(new Country("1", "Albania"), new Country("4", "Brazil"),
        new Country("3", "Finland"));

    CountryListEntry countryListEntry = new CountryListEntry(countryList);
    when(countryListCache.get(COUNTRY_SET_NAME)).thenReturn(Optional.of(countryListEntry));

    CountryListEntry result = countriesService.getCountryList(COUNTRY_SET_NAME);

    assertThat(result.getList()).isEqualTo(countryList);
  }

  @Test
  public void shouldThrowExceptionIfCountrySetDoesNotExist() throws Exception {

    expectedException.expect(CountrySetNotFoundException.class);
    expectedException.expectMessage("not found error");

    when(countryListCache.get("blah")).thenThrow(new CountrySetNotFoundException("not found error"));

    countriesService.getCountryList("blah");

  }

}