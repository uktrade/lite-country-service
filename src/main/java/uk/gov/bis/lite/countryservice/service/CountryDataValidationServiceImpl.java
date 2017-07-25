package uk.gov.bis.lite.countryservice.service;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.inject.Inject;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import uk.gov.bis.lite.countryservice.api.CountryData;
import uk.gov.bis.lite.countryservice.cache.CountryCache;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CountryDataValidationServiceImpl implements CountryDataValidationService {

  private final CountryCache countryCache;

  @Inject
  public CountryDataValidationServiceImpl(CountryCache countryCache) {
    this.countryCache = countryCache;
  }

  @Override
  public List<String> getUnmatchedCountryRefs(List<CountryData> countryDataList) {
    return countryDataList.stream()
        .map(CountryData::getCountryRef)
        .filter(countryRef -> !countryCache.getCountryEntry(countryRef).isPresent())
        .collect(Collectors.toList());
  }

  @Override
  public Set<String> getDuplicateCountryRefs(List<CountryData> countryDataList) {
    List<String> countryRefs = countryDataList.stream()
        .map(CountryData::getCountryRef)
        .collect(Collectors.toList());
    return getDuplicateStrings(countryRefs);
  }

  @Override
  public List<String> getCountryRefsWithDuplicateSynonyms(List<CountryData> countryDataList) {
    return countryDataList.stream()
        .filter(countryData -> !CollectionUtils.isEmpty(countryData.getSynonyms()))
        .filter(countryData -> !getDuplicateStrings(countryData.getSynonyms()).isEmpty())
        .map(CountryData::getCountryRef)
        .collect(Collectors.toList());
  }

  @Override
  public List<String> getCountryRefsWithBlankSynonyms(List<CountryData> countryDataList) {
    return countryDataList.stream()
        .filter(countryData -> !CollectionUtils.isEmpty(countryData.getSynonyms()))
        .filter(countryData -> StringUtils.isAnyBlank(countryData.getSynonyms().toArray(new String[0])))
        .map(CountryData::getCountryRef)
        .collect(Collectors.toList());
  }

  private Set<String> getDuplicateStrings(List<String> strings) {
    return HashMultiset.create(strings).entrySet().stream()
        .filter(entry -> entry.getCount() > 1)
        .map(Multiset.Entry::getElement)
        .collect(Collectors.toSet());
  }

}
