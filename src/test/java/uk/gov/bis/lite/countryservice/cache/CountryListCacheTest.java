package uk.gov.bis.lite.countryservice.cache;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.bis.lite.common.spire.client.SpireRequest;
import uk.gov.bis.lite.countryservice.api.CountryView;
import uk.gov.bis.lite.countryservice.spire.SpireCountriesClient;
import uk.gov.bis.lite.countryservice.spire.model.SpireCountry;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CountryListCacheTest {

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Mock
  private SpireCountriesClient spireCountriesClient;

  @Mock
  private SpireRequest spireRequest;

  @Mock
  private CountryListFactory countryListFactory;

  private CountryListCache countryListCache;

  @Before
  public void setUp() throws Exception {
    countryListCache = new CountryListCache(spireCountriesClient);
    when(spireCountriesClient.createRequest()).thenReturn(spireRequest);
  }

  @Test
  public void shouldGetCountrySetFromCache() throws Exception {

    setupCache();

    Optional<CountryListEntry> countryListEntry = countryListCache.getCountriesBySetName("export-control");

    assertThat(countryListEntry.isPresent()).isTrue();

    List<CountryView> countries = countryListEntry.get().getList();
    assertThat(countries.size()).isEqualTo(3);
    assertThat(countries.get(0).getCountryName()).isEqualTo("Albania");
    assertThat(countries.get(1).getCountryName()).isEqualTo("Brazil");
    assertThat(countries.get(2).getCountryName()).isEqualTo("Finland");
  }

  @Test
  public void shouldGetEmptyListWhenCountrySetNotFoundInCache() throws Exception {

    Optional<CountryListEntry> countryListEntry = countryListCache.getCountriesBySetName("blah");

    assertThat(countryListEntry.isPresent()).isFalse();
  }

  @Test
  public void shouldGetCountryGroupFromCache() throws Exception {

    setupCache();

    Optional<CountryListEntry> countryListEntry = countryListCache.getCountriesByGroupName("eu");

    assertThat(countryListEntry.isPresent()).isTrue();

    List<CountryView> countries = countryListEntry.get().getList();
    assertThat(countries.size()).isEqualTo(3);
    assertThat(countries.get(0).getCountryName()).isEqualTo("France");
    assertThat(countries.get(1).getCountryName()).isEqualTo("Germany");
    assertThat(countries.get(2).getCountryName()).isEqualTo("Sweden");
  }

  @Test
  public void shouldGetEmptyListWhenCountryGroupNotFoundInCache() throws Exception {

    Optional<CountryListEntry> countryListEntry = countryListCache.getCountriesByGroupName("blah");

    assertThat(countryListEntry.isPresent()).isFalse();
  }

  private void setupCache() throws Exception {

    List<SpireCountry> countrySet = Arrays.asList(new SpireCountry("1", "Finland"), new SpireCountry("2", "Brazil"), new SpireCountry("3", "Albania"));
    List<SpireCountry> countryGroup = Arrays.asList(new SpireCountry("1", "Sweden"), new SpireCountry("2", "France"), new SpireCountry("3", "Germany"));

    when(spireCountriesClient.sendRequest(spireRequest))
      .thenReturn(countrySet)
      .thenReturn(countryGroup);

    countryListCache.load();
  }

}