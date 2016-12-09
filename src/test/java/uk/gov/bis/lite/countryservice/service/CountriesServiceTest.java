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
import uk.gov.bis.lite.countryservice.exception.CountriesNotFoundException;
import uk.gov.bis.lite.countryservice.model.Country;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CountriesServiceTest {

  private static final String COUNTRY_SET_NAME = "export-control";
  private static final String COUNTRY_GROUP_NAME = "eu";

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Mock
  private CountryListCache countryListCache;

  @InjectMocks
  private CountriesService countriesService;

  @Test
  public void shouldGetCountrySet() throws Exception {

    List<Country> countryList = Arrays.asList(new Country("1", "Albania"), new Country("4", "Brazil"),
        new Country("3", "Finland"));

    CountryListEntry countryListEntry = new CountryListEntry(countryList);
    when(countryListCache.getCountriesBySetName(COUNTRY_SET_NAME)).thenReturn(Optional.of(countryListEntry));

    CountryListEntry result = countriesService.getCountrySet(COUNTRY_SET_NAME);

    assertThat(result.getList()).isEqualTo(countryList);
  }

  @Test
  public void shouldThrowExceptionIfCountrySetDoesNotExist() throws Exception {

    expectedException.expect(CountriesNotFoundException.class);
    expectedException.expectMessage("not found error");

    when(countryListCache.getCountriesBySetName("blah")).thenThrow(new CountriesNotFoundException("not found error"));

    countriesService.getCountrySet("blah");

  }

  @Test
  public void shouldGetCountryGroup() throws Exception {

    List<Country> countryList = Arrays.asList(new Country("1", "Sweden"), new Country("4", "France"),
      new Country("3", "Germany"));

    CountryListEntry countryListEntry = new CountryListEntry(countryList);
    when(countryListCache.getCountriesByGroupName(COUNTRY_GROUP_NAME)).thenReturn(Optional.of(countryListEntry));

    CountryListEntry result = countriesService.getCountryGroup(COUNTRY_GROUP_NAME);

    assertThat(result.getList()).isEqualTo(countryList);
  }

  @Test
  public void shouldThrowExceptionIfCountryGroupDoesNotExist() throws Exception {

    expectedException.expect(CountriesNotFoundException.class);
    expectedException.expectMessage("not found error");

    when(countryListCache.getCountriesByGroupName("blah")).thenThrow(new CountriesNotFoundException("not found error"));

    countriesService.getCountryGroup("blah");

  }

}