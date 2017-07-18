package uk.gov.bis.lite.countryservice.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.bis.lite.countryservice.cache.CountryListCache;
import uk.gov.bis.lite.countryservice.cache.CountryListEntry;
import uk.gov.bis.lite.countryservice.model.CountryEntry;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RunWith(MockitoJUnitRunner.class)
public class CountriesServiceTest {

  private static final String COUNTRY_SET_NAME = "export-control";
  private static final String COUNTRY_GROUP_NAME = "eu";

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Mock
  private CountryListCache countryListCache;

  @InjectMocks
  private CountriesServiceImpl countriesService;

  @Test
  public void shouldGetCountrySet() throws Exception {
    List<CountryEntry> countries = Arrays.asList(new CountryEntry("1", "Albania"), new CountryEntry("4", "Brazil"),
        new CountryEntry("3", "Finland"));

    CountryListEntry countryListEntry = new CountryListEntry(countries);
    when(countryListCache.getCountriesBySetName(COUNTRY_SET_NAME)).thenReturn(Optional.of(countryListEntry));

    Optional<CountryListEntry> result = countriesService.getCountrySet(COUNTRY_SET_NAME);
    assertThat(result.isPresent()).isTrue();
    assertThat(result.get().getList()).isEqualTo(countries);
  }

  @Test
  public void shouldReturnEmptyOptionalIfCountrySetDoesNotExist() throws Exception {
    when(countryListCache.getCountriesBySetName("blah")).thenReturn(Optional.empty());

    Optional<CountryListEntry> countrySet = countriesService.getCountrySet("blah");

    assertThat(countrySet.isPresent()).isFalse();
  }

  @Test
  public void shouldGetCountryGroup() throws Exception {
    List<CountryEntry> countries = Arrays.asList(new CountryEntry("1", "Sweden"), new CountryEntry("4", "France"),
        new CountryEntry("3", "Germany"));

    CountryListEntry countryListEntry = new CountryListEntry(countries);
    when(countryListCache.getCountriesByGroupName(COUNTRY_GROUP_NAME)).thenReturn(Optional.of(countryListEntry));

    Optional<CountryListEntry> result = countriesService.getCountryGroup(COUNTRY_GROUP_NAME);
    assertThat(result.isPresent()).isTrue();
    assertThat(result.get().getList()).isEqualTo(countries);
  }

  @Test
  public void shouldReturnEmptyOptionalCountryGroupDoesNotExist() throws Exception {
    when(countryListCache.getCountriesByGroupName("blah")).thenReturn(Optional.empty());

    Optional<CountryListEntry> countryGroup = countriesService.getCountryGroup("blah");

    assertThat(countryGroup.isPresent()).isFalse();
  }

}