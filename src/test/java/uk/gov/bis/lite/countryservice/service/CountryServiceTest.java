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
import uk.gov.bis.lite.countryservice.api.CountryView;
import uk.gov.bis.lite.countryservice.cache.CountryCache;
import uk.gov.bis.lite.countryservice.dao.SynonymDao;
import uk.gov.bis.lite.countryservice.model.CountryEntry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RunWith(MockitoJUnitRunner.class)
public class CountryServiceTest {

  private static final String COUNTRY_SET_NAME = "export-control";
  private static final String COUNTRY_GROUP_NAME = "eu";

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Mock
  private CountryCache countryCache;

  @Mock
  private SynonymDao synonymDao;

  @InjectMocks
  private CountryServiceImpl countriesService;

  @Test
  public void shouldGetCountrySet() throws Exception {
    List<CountryEntry> countries = Arrays.asList(new CountryEntry("1", "Albania"), new CountryEntry("4", "Brazil"),
        new CountryEntry("3", "Finland"));

    when(countryCache.getCountriesBySetName(COUNTRY_SET_NAME)).thenReturn(Optional.of(countries));
    when(synonymDao.getSynonyms()).thenReturn(new ArrayList<>());

    Optional<List<CountryView>> result = countriesService.getCountrySet(COUNTRY_SET_NAME);
    assertThat(result.isPresent()).isTrue();
    assertThat(result.get()).extracting(CountryView::getCountryName).containsExactly("Albania", "Brazil", "Finland");
  }

  @Test
  public void shouldReturnEmptyOptionalIfCountrySetDoesNotExist() throws Exception {
    when(countryCache.getCountriesBySetName("blah")).thenReturn(Optional.empty());

    Optional<List<CountryView>> countrySet = countriesService.getCountrySet("blah");

    assertThat(countrySet.isPresent()).isFalse();
  }

  @Test
  public void shouldGetCountryGroup() throws Exception {
    List<CountryEntry> countries = Arrays.asList(new CountryEntry("1", "Sweden"), new CountryEntry("4", "France"),
        new CountryEntry("3", "Germany"));

    when(countryCache.getCountriesByGroupName(COUNTRY_GROUP_NAME)).thenReturn(Optional.of(countries));
    when(synonymDao.getSynonyms()).thenReturn(new ArrayList<>());

    Optional<List<CountryView>> result = countriesService.getCountryGroup(COUNTRY_GROUP_NAME);
    assertThat(result.isPresent()).isTrue();
    assertThat(result.get()).extracting(CountryView::getCountryName).containsExactly("Sweden", "France", "Germany");
  }

  @Test
  public void shouldReturnEmptyOptionalCountryGroupDoesNotExist() throws Exception {
    when(countryCache.getCountriesByGroupName("blah")).thenReturn(Optional.empty());

    Optional<List<CountryView>> countryGroup = countriesService.getCountryGroup("blah");

    assertThat(countryGroup.isPresent()).isFalse();
  }

}