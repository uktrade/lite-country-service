package uk.gov.bis.lite.countryservice.service;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.bis.lite.countryservice.api.CountryData;
import uk.gov.bis.lite.countryservice.cache.CountryCache;
import uk.gov.bis.lite.countryservice.model.CountryEntry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RunWith(MockitoJUnitRunner.class)
public class CountryDataValidationServiceTest {

  @Mock
  private CountryCache countryCache;

  @InjectMocks
  private CountryDataValidationServiceImpl countryDataValidationService;

  @Test
  public void shouldGetUnmatchedCountryRefs() {
    when(countryCache.getCountryEntry(null)).thenReturn(Optional.empty());
    when(countryCache.getCountryEntry("MADE-UP")).thenReturn(Optional.empty());
    when(countryCache.getCountryEntry("1")).thenReturn(Optional.of(new CountryEntry("1", "Germany")));

    List<CountryData> countryDataList = Arrays.asList(
        new CountryData(null, null),
        new CountryData("MADE-UP", null),
        new CountryData("1", singletonList("Deutschland")));

    List<String> unmatchedCountryRefs = countryDataValidationService.getUnmatchedCountryRefs(countryDataList);
    assertThat(unmatchedCountryRefs).containsExactlyInAnyOrder(null, "MADE-UP");
  }

  @Test
  public void shouldGetDuplicateCountryRefs() {
    CountryData germany = new CountryData("1", Arrays.asList("Deutschland", "BRD"));
    CountryData unitedKingdom = new CountryData("2", singletonList("United Kingdom"));
    CountryData uk = new CountryData("2", singletonList("UK"));
    CountryData france = new CountryData("3", null);
    CountryData brazil = new CountryData("4", singletonList("Brasil"));
    CountryData nullRef = new CountryData(null, singletonList("Null"));
    List<CountryData> countryDataList = Arrays.asList(germany, germany, unitedKingdom, uk, france, france, france, brazil, nullRef, nullRef);

    Set<String> duplicates = countryDataValidationService.getDuplicateCountryRefs(countryDataList);
    assertThat(duplicates).containsExactlyInAnyOrder("1", "2", "3", null);
  }

  @Test
  public void shouldGetCountryRefsWithBlankSynonyms() {
    CountryData germany = new CountryData("1", Collections.singletonList("Germany"));
    CountryData unitedKingdom = new CountryData("2", Collections.singletonList(null));
    CountryData spain = new CountryData("3", Collections.singletonList("      "));
    CountryData portugal = new CountryData("4", Arrays.asList(null, ""));
    CountryData france = new CountryData("5", null);
    CountryData brazil = new CountryData("6", new ArrayList<>());
    List<CountryData> countryDataList = Arrays.asList(germany, unitedKingdom, spain, portugal, france, brazil);

    List<String> countryRefsWithBlankSynonyms = countryDataValidationService.getCountryRefsWithBlankSynonyms(countryDataList);
    assertThat(countryRefsWithBlankSynonyms).containsExactly("2", "3", "4");
  }

  @Test
  public void shouldGetCountryRefsWithDuplicateSynonyms() {
    CountryData germany = new CountryData("1", Arrays.asList("Deutschland", "BRD", "Deutschland"));
    CountryData unitedKingdom = new CountryData("2", Arrays.asList("UK", "United Kingdom", "England"));
    CountryData france = new CountryData("3", null);
    CountryData brazil = new CountryData("4", new ArrayList<>());
    List<CountryData> countryDataList = Arrays.asList(germany, unitedKingdom, france, brazil);

    List<String> countryRefsWithDuplicateSynonyms = countryDataValidationService.getCountryRefsWithDuplicateSynonyms(countryDataList);
    assertThat(countryRefsWithDuplicateSynonyms).containsExactly("1");
  }

}
