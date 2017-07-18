package uk.gov.bis.lite.countryservice.cache;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.bis.lite.countryservice.model.CountryEntry;
import uk.gov.bis.lite.countryservice.service.SpireService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RunWith(MockitoJUnitRunner.class)
public class CountryListCacheTest {

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Mock
  private SpireService spireService;

  private CountryListCache countryListCache;

  @Before
  public void setUp() throws Exception {
    countryListCache = new CountryListCache(spireService);
  }

  @Test
  public void shouldGetCountrySetFromCache() throws Exception {

    setupCache();

    Optional<CountryListEntry> countryListEntry = countryListCache.getCountriesBySetName("export-control");

    assertThat(countryListEntry.isPresent()).isTrue();

    List<CountryEntry> countries = countryListEntry.get().getList();
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

    List<CountryEntry> countries = countryListEntry.get().getList();
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
    CountryEntry albania = new CountryEntry("1", "Albania");
    CountryEntry brazil = new CountryEntry("2", "Brazil");
    CountryEntry finland = new CountryEntry("3", "Finland");
    List<CountryEntry> countrySet = Arrays.asList(albania, brazil, finland);
    CountryEntry france = new CountryEntry("1", "France");
    CountryEntry germany = new CountryEntry("2", "Germany");
    CountryEntry sweden = new CountryEntry("3", "Sweden");
    List<CountryEntry> countryGroup = Arrays.asList(france, germany, sweden);

    when(spireService.loadCountriesByCountrySetId(Matchers.anyString())).thenReturn(countrySet);
    when(spireService.loadCountriesByCountryGroupId(Matchers.anyString())).thenReturn(countryGroup);

    countryListCache.load();
  }

}