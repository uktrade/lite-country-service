package uk.gov.bis.lite.countryservice.service;

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
import uk.gov.bis.lite.countryservice.dao.SynonymDao;
import uk.gov.bis.lite.countryservice.model.CountryEntry;
import uk.gov.bis.lite.countryservice.model.SynonymEntry;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RunWith(MockitoJUnitRunner.class)
public class CountryServiceTest {

  private static final String COUNTRY_SET_NAME = "export-control";
  private static final String COUNTRY_GROUP_NAME = "eu";

  @Mock
  private CountryCache countryCache;

  @Mock
  private SynonymDao synonymDao;

  @InjectMocks
  private CountryServiceImpl countryService;

  @Test
  public void shouldGetUnmatchedCountryRefs() {
    when(countryCache.getCountryEntry(null)).thenReturn(Optional.empty());
    when(countryCache.getCountryEntry("MADE-UP")).thenReturn(Optional.empty());
    when(countryCache.getCountryEntry("1")).thenReturn(Optional.of(new CountryEntry("1", "Germany")));

    List<CountryData> countryDataList = Arrays.asList(
        null,
        new CountryData(null, null),
        new CountryData("MADE-UP", null),
        new CountryData("1", new String[]{"Deutschland"}));

    List<String> unmatchedCountryRefs = countryService.getUnmatchedCountryRefs(countryDataList);
    assertThat(unmatchedCountryRefs).containsExactlyInAnyOrder(null, null, "MADE-UP");
  }

  @Test
  public void shouldGetDuplicates() {
    CountryData germany = new CountryData("1", new String[]{"Deutschland", "BRD"});
    CountryData unitedKingdom = new CountryData("2", new String[]{"United Kingdom"});
    CountryData uk = new CountryData("2", new String[]{"UK"});
    CountryData france = new CountryData("3", null);
    CountryData brazil = new CountryData("4", new String[]{"Brasil"});
    List<CountryData> countryDataList = Arrays.asList(germany, germany, unitedKingdom, uk, france, france, france, brazil);

    Set<String> duplicates = countryService.getDuplicateCountryRefs(countryDataList);
    assertThat(duplicates).containsExactlyInAnyOrder("1", "2", "3");
  }

  @Test
  public void shouldGetCountrySet() {
    List<CountryEntry> countries = Arrays.asList(
        new CountryEntry("1", "Albania"),
        new CountryEntry("4", "Brazil"),
        new CountryEntry("3", "Finland"));

    when(countryCache.getCountriesBySetName(COUNTRY_SET_NAME)).thenReturn(Optional.of(countries));
    when(synonymDao.getSynonyms()).thenReturn(Collections.singletonList(new SynonymEntry("4", "Brasil")));

    Optional<List<CountryView>> result = countryService.getCountrySet(COUNTRY_SET_NAME);

    assertThat(result).isPresent();
    assertThat(result.get()).usingFieldByFieldElementComparator().containsExactly(
        new CountryView("1", "Albania", new String[]{}),
        new CountryView("4", "Brazil", new String[]{"Brasil"}),
        new CountryView("3", "Finland", new String[]{}));
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
    when(synonymDao.getSynonyms()).thenReturn(Collections.singletonList(new SynonymEntry("3", "Deutschland")));

    Optional<List<CountryView>> result = countryService.getCountryGroup(COUNTRY_GROUP_NAME);
    assertThat(result).isPresent();
    assertThat(result.get()).usingFieldByFieldElementComparator().containsExactly(
        new CountryView("4", "France", new String[]{}),
        new CountryView("3", "Germany", new String[]{"Deutschland"}),
        new CountryView("1", "Sweden", new String[]{}));
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
    when(synonymDao.getSynonyms("1")).thenReturn(new String[]{"Reino de España", "España"});

    Optional<CountryView> countryView = countryService.getCountryView("1");

    assertThat(countryView).isPresent();
    assertThat(countryView.get().getCountryName()).isEqualTo("Spain");
    assertThat(countryView.get().getCountryRef()).isEqualTo("1");
    assertThat(countryView.get().getSynonyms()).containsExactly("España", "Reino de España");
  }

  @Test
  public void shouldReturnEmptyOptionalForGetIfCountryRefDoesNotExist() {
    when(countryCache.getCountryEntry(anyString())).thenReturn(Optional.empty());
    when(synonymDao.getSynonyms(anyString())).thenReturn(new String[]{});

    Optional<CountryView> countryView = countryService.getCountryView("1");

    assertThat(countryView).isEmpty();
  }

  @Test
  public void shouldReturnCountryViews() {
    CountryEntry germany = new CountryEntry("1", "Germany");
    CountryEntry france = new CountryEntry("2", "France");
    when(countryCache.getCountryEntries()).thenReturn(Arrays.asList(germany, france));
    when(synonymDao.getSynonyms()).thenReturn(Collections.singletonList(new SynonymEntry("1", "Deutschland")));

    List<CountryView> countryViews = countryService.getCountryViews();
    assertThat(countryViews).usingFieldByFieldElementComparator().containsExactly(
        new CountryView("2", "France", new String[]{}),
        new CountryView("1", "Germany", new String[]{"Deutschland"}));
  }

  @Test
  public void shouldBulkUpdateData() {
    CountryData germany = new CountryData("1", new String[]{"Deutschland"});
    CountryData france = new CountryData("2", null);
    List<CountryData> countryDataList = Arrays.asList(germany, france);
    countryService.bulkUpdateCountryData(countryDataList);

    verify(synonymDao).bulkUpdateSynonyms(countryDataList);
  }

  @Test
  public void shouldDeleteCountryData() {
    countryService.deleteCountryData("1");

    verify(synonymDao).deleteSynonyms("1");
  }

  @Test
  public void shouldDeleteAllCountryData() {
    countryService.deleteAllCountryData();

    verify(synonymDao).deleteAllSynonyms();
  }

  @Test
  public void shouldGetLastCached() {
    countryService.getLastCached();

    verify(countryCache).getLastCached();
  }

}
