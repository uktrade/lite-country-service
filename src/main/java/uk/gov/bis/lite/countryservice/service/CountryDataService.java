package uk.gov.bis.lite.countryservice.service;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import uk.gov.bis.lite.countryservice.api.CountryView;
import uk.gov.bis.lite.countryservice.cache.CountryListCache;
import uk.gov.bis.lite.countryservice.dao.SynonymDao;
import uk.gov.bis.lite.countryservice.api.CountryData;

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
    Optional<CountryView> cachedCountryView = countryListCache.getCountryView(countryRef);
    if (cachedCountryView.isPresent()) {
      String[] synonyms = synonymDao.getSynonyms(countryRef);
      Arrays.sort(synonyms);
      CountryView countryView = new CountryView(countryRef, cachedCountryView.get().getCountryName(), synonyms);
      return Optional.of(countryView);
    } else {
      return Optional.empty();
    }
  }

  public List<CountryView> getCountryData() {
    Collection<CountryView> countryViews = countryListCache.getCountryViews();
    Multimap<String, String> map = HashMultimap.create();
    synonymDao.getSynonyms().forEach(synonymData -> map.put(synonymData.getCountryRef(), synonymData.getSynonym()));
    return countryViews.stream().map(countryView -> {
      String[] synonyms = map.get(countryView.getCountryRef()).toArray(new String[0]);
      return new CountryView(countryView.getCountryRef(), countryView.getCountryName(), synonyms);
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
