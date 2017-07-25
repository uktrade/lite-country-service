package uk.gov.bis.lite.countryservice.service;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.bis.lite.countryservice.api.CountryData;
import uk.gov.bis.lite.countryservice.api.CountryView;
import uk.gov.bis.lite.countryservice.cache.CountryCache;
import uk.gov.bis.lite.countryservice.dao.CountryDataDao;
import uk.gov.bis.lite.countryservice.model.CountryEntry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RunWith(MockitoJUnitRunner.class)
public class CountryServiceTest {

  private static final String COUNTRY_SET_NAME = "export-control";
  private static final String COUNTRY_GROUP_NAME = "eu";

  @Mock
  private CountryCache countryCache;

  @Mock
  private CountryDataDao countryDataDao;

  @InjectMocks
  private CountryServiceImpl countryService;

  @Test
  public void shouldGetCountrySet() {
    List<CountryEntry> countries = Arrays.asList(
        new CountryEntry("1", "Albania"),
        new CountryEntry("4", "Brazil"),
        new CountryEntry("3", "Finland"));

    when(countryCache.getCountriesBySetName(COUNTRY_SET_NAME)).thenReturn(Optional.of(countries));
    when(countryDataDao.getAllCountryData()).thenReturn(singletonList(new CountryData("4", singletonList("Brasil"))));

    Optional<List<CountryView>> result = countryService.getCountrySet(COUNTRY_SET_NAME);

    assertThat(result).isPresent();
    assertThat(result.get()).usingFieldByFieldElementComparator().containsExactly(
        new CountryView("1", "Albania", new ArrayList<>()),
        new CountryView("4", "Brazil", singletonList("Brasil")),
        new CountryView("3", "Finland", new ArrayList<>()));
  }

  @Test
  public void shouldReturnEmptyOptionalIfCountrySetDoesNotExist() {
    when(countryCache.getCountriesBySetName("MADE-UP")).thenReturn(Optional.empty());

    Optional<List<CountryView>> countrySet = countryService.getCountrySet("MADE-UP");

    assertThat(countrySet).isEmpty();
  }

  @Test
  public void shouldGetCountryGroup() {
    List<CountryEntry> countries = Arrays.asList(
        new CountryEntry("4", "France"),
        new CountryEntry("3", "Germany"),
        new CountryEntry("1", "Sweden"));

    when(countryCache.getCountriesByGroupName(COUNTRY_GROUP_NAME)).thenReturn(Optional.of(countries));
    when(countryDataDao.getAllCountryData()).thenReturn(singletonList(new CountryData("3", singletonList("Deutschland"))));

    Optional<List<CountryView>> result = countryService.getCountryGroup(COUNTRY_GROUP_NAME);
    assertThat(result).isPresent();
    assertThat(result.get()).usingFieldByFieldElementComparator().containsExactly(
        new CountryView("4", "France", new ArrayList<>()),
        new CountryView("3", "Germany", singletonList("Deutschland")),
        new CountryView("1", "Sweden", new ArrayList<>()));
  }

  @Test
  public void shouldReturnEmptyOptionalIfCountryGroupDoesNotExist() {
    when(countryCache.getCountriesByGroupName("MADE-UP")).thenReturn(Optional.empty());

    Optional<List<CountryView>> countryGroup = countryService.getCountryGroup("MADE-UP");

    assertThat(countryGroup).isEmpty();
  }

  @Test
  public void shouldGetCountryView() {
    CountryEntry spain = new CountryEntry("1", "Spain");
    when(countryCache.getCountryEntry("1")).thenReturn(Optional.of(spain));
    when(countryDataDao.getCountryData("1")).thenReturn(
        Optional.of(new CountryData("1", Arrays.asList("Reino de España", "España"))));

    Optional<CountryView> countryView = countryService.getCountryView("1");

    assertThat(countryView).isPresent();
    assertThat(countryView.get().getCountryName()).isEqualTo("Spain");
    assertThat(countryView.get().getCountryRef()).isEqualTo("1");
    assertThat(countryView.get().getSynonyms()).containsExactly("España", "Reino de España");
  }

  @Test
  public void shouldReturnEmptyOptionalForGetIfCountryRefDoesNotExist() {
    when(countryCache.getCountryEntry(anyString())).thenReturn(Optional.empty());
    when(countryDataDao.getCountryData(anyString())).thenReturn(Optional.empty());

    Optional<CountryView> countryView = countryService.getCountryView("1");

    assertThat(countryView).isEmpty();
  }

  @Test
  public void shouldReturnCountryViews() {
    CountryEntry germany = new CountryEntry("1", "Germany");
    CountryEntry france = new CountryEntry("2", "France");
    when(countryCache.getCountryEntries()).thenReturn(Arrays.asList(germany, france));
    when(countryDataDao.getAllCountryData()).thenReturn(singletonList(new CountryData("1", singletonList("Deutschland"))));

    List<CountryView> countryViews = countryService.getCountryViews();
    assertThat(countryViews).usingFieldByFieldElementComparator().containsExactly(
        new CountryView("2", "France", new ArrayList<>()),
        new CountryView("1", "Germany", singletonList("Deutschland")));
  }

  @Test
  public void shouldBulkUpdateData() {
    CountryData germany = new CountryData("1", singletonList(("Deutschland")));
    CountryData france = new CountryData("2", null);
    List<CountryData> countryDataList = Arrays.asList(germany, france);
    countryService.bulkUpdateCountryData(countryDataList);

    verify(countryDataDao).bulkUpdateCountryData(countryDataList);
  }

  @Test
  public void shouldDeleteCountryData() {
    countryService.deleteCountryData("1");

    verify(countryDataDao).deleteCountryData("1");
  }

  @Test
  public void shouldDeleteAllCountryData() {
    countryService.deleteAllCountryData();

    verify(countryDataDao).deleteAllCountryData();
  }

  @Test
  public void shouldGetLastCached() {
    countryService.getLastCached();

    verify(countryCache).getLastCached();
  }

}
