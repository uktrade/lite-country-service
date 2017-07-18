package uk.gov.bis.lite.countryservice.service;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.bis.lite.countryservice.api.CountryData;
import uk.gov.bis.lite.countryservice.api.CountryView;
import uk.gov.bis.lite.countryservice.cache.CountryCache;
import uk.gov.bis.lite.countryservice.dao.SynonymDao;
import uk.gov.bis.lite.countryservice.model.CountryEntry;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class CountryServiceImpl implements CountryService {

  private static final Logger LOGGER = LoggerFactory.getLogger(CountryServiceImpl.class);

  private final SynonymDao synonymDao;
  private final CountryCache countryCache;

  @Inject
  public CountryServiceImpl(SynonymDao synonymDao, CountryCache countryCache) {
    this.synonymDao = synonymDao;
    this.countryCache = countryCache;
  }

  @Override
  public List<String> getUnmatchedCountryRefs(List<CountryData> countryDataList) {
    return countryDataList.stream()
        .map(countryData -> countryData == null ? null : countryData.getCountryRef())
        .filter(countryRef -> countryCache.getCountryEntry(countryRef).isPresent())
        .collect(Collectors.toList());
  }

  @Override
  public Set<String> getDuplicates(List<CountryData> countryDataList) {
    List<String> countryRefs = countryDataList.stream().map(CountryData::getCountryRef).collect(Collectors.toList());
    return HashMultiset.create(countryRefs).entrySet().stream()
        .filter(entry -> entry.getCount() > 1)
        .map(Multiset.Entry::getElement)
        .collect(Collectors.toSet());
  }

  @Override
  public Optional<List<CountryView>> getCountrySet(String countrySetName) {
    Optional<List<CountryEntry>> countryEntries = countryCache.getCountriesBySetName(countrySetName);
    if (!countryEntries.isPresent()) {
      LOGGER.error("Country set not found in cache - " + countrySetName);
      return Optional.empty();
    } else {
      List<CountryView> countryViews = createCountryViews(countryEntries.get());
      return Optional.of(countryViews);
    }
  }

  @Override
  public Optional<List<CountryView>> getCountryGroup(String groupName) {
    Optional<List<CountryEntry>> countryEntries = countryCache.getCountriesByGroupName(groupName);
    if (!countryEntries.isPresent()) {
      LOGGER.error("Country group not found in cache - " + groupName);
      return Optional.empty();
    } else {
      List<CountryView> countryViews = createCountryViews(countryEntries.get());
      return Optional.of(countryViews);
    }
  }

  @Override
  public Optional<CountryView> getCountryView(String countryRef) {
    Optional<CountryEntry> countryEntry = countryCache.getCountryEntry(countryRef);
    if (countryEntry.isPresent()) {
      String[] synonyms = synonymDao.getSynonyms(countryRef);
      Arrays.sort(synonyms);
      CountryView countryView = new CountryView(countryRef, countryEntry.get().getCountryName(), synonyms);
      return Optional.of(countryView);
    } else {
      return Optional.empty();
    }
  }

  @Override
  public List<CountryView> getCountryViews() {
    Collection<CountryEntry> countryEntries = countryCache.getCountryEntries();
    return createCountryViews(countryEntries);
  }

  private List<CountryView> createCountryViews(Collection<CountryEntry> countryEntries) {
    Multimap<String, String> synonymMap = HashMultimap.create();
    synonymDao.getSynonyms().forEach(synonymData -> synonymMap.put(synonymData.getCountryRef(), synonymData.getSynonym()));
    return countryEntries.stream()
        .map(countryEntry -> {
          String[] synonyms = synonymMap.get(countryEntry.getCountryRef()).toArray(new String[0]);
          return new CountryView(countryEntry.getCountryRef(), countryEntry.getCountryName(), synonyms);
        }).collect(Collectors.toList());
  }

  @Override
  public void bulkUpdateCountryData(List<CountryData> countryDataList) {
    synonymDao.bulkUpdateSynonyms(countryDataList);
  }

  @Override
  public void deleteCountryData(String countryRef) {
    synonymDao.deleteSynonyms(countryRef);
  }

  @Override
  public void deleteAllCountryData() {
    synonymDao.deleteAllSynonyms();
  }

  @Override
  public long getLastCached() {
    return countryCache.getLastCached();
  }

}
