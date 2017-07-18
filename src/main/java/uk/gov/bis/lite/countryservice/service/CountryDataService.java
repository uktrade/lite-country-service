package uk.gov.bis.lite.countryservice.service;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import uk.gov.bis.lite.countryservice.api.CountryData;
import uk.gov.bis.lite.countryservice.api.CountryView;
import uk.gov.bis.lite.countryservice.cache.CountryListCache;
import uk.gov.bis.lite.countryservice.dao.SynonymDao;
import uk.gov.bis.lite.countryservice.model.CountryEntry;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

public class CountryDataService {

  private final SynonymDao synonymDao;
  private final CountryListCache countryListCache;

  @Inject
  public CountryDataService(SynonymDao synonymDao, CountryListCache countryListCache) {
    this.synonymDao = synonymDao;
    this.countryListCache = countryListCache;
  }

  public Optional<CountryView> getCountryData(String countryRef) {
    Optional<CountryEntry> countryEntry = countryListCache.getCountryEntry(countryRef);
    if (countryEntry.isPresent()) {
      String[] synonyms = synonymDao.getSynonyms(countryRef);
      Arrays.sort(synonyms);
      CountryView countryView = new CountryView(countryRef, countryEntry.get().getCountryName(), synonyms);
      return Optional.of(countryView);
    } else {
      return Optional.empty();
    }
  }

  public List<CountryView> getCountryData() {
    Collection<CountryEntry> countryEntries = countryListCache.getCountryEntries();
    Multimap<String, String> synonymMap = HashMultimap.create();
    synonymDao.getSynonyms().forEach(synonymData -> synonymMap.put(synonymData.getCountryRef(), synonymData.getSynonym()));
    return countryEntries.stream().map(countryEntry -> {
      String[] synonyms = synonymMap.get(countryEntry.getCountryRef()).toArray(new String[0]);
      return new CountryView(countryEntry.getCountryRef(), countryEntry.getCountryName(), synonyms);
    }).collect(Collectors.toList());
  }

  public void bulkUpdateCountryData(List<CountryData> countryDataList) {
    synonymDao.bulkUpdateSynonyms(countryDataList);
  }

  public void deleteCountryData(String countryRef) {
    synonymDao.deleteSynonyms(countryRef);
  }

  public void deleteAllCountryData() {
    synonymDao.deleteAllSynonyms();
  }

}
