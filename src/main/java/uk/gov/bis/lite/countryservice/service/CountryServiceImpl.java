package uk.gov.bis.lite.countryservice.service;

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.bis.lite.countryservice.api.CountryData;
import uk.gov.bis.lite.countryservice.api.CountryView;
import uk.gov.bis.lite.countryservice.cache.CountryCache;
import uk.gov.bis.lite.countryservice.dao.CountryDataDao;
import uk.gov.bis.lite.countryservice.healthcheck.SpireHealthStatus;
import uk.gov.bis.lite.countryservice.model.CountryEntry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CountryServiceImpl implements CountryService {

  private static final Logger LOGGER = LoggerFactory.getLogger(CountryServiceImpl.class);

  private final CountryDataDao countryDataDao;
  private final CountryCache countryCache;

  @Inject
  public CountryServiceImpl(CountryDataDao countryDataDao, CountryCache countryCache) {
    this.countryDataDao = countryDataDao;
    this.countryCache = countryCache;
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
      Optional<CountryData> countryData = countryDataDao.getCountryData(countryRef);
      List<String> synonyms;
      if (countryData.isPresent() && countryData.get().getSynonyms() != null) {
        synonyms = countryData.get().getSynonyms();
        Collections.sort(synonyms);
      } else {
        synonyms = new ArrayList<>();
      }
      CountryView countryView = new CountryView(countryRef, countryEntry.get().getCountryName(), synonyms);
      return Optional.of(countryView);
    } else {
      return Optional.empty();
    }
  }

  @Override
  public List<CountryView> getCountryViews() {
    Collection<CountryEntry> countryEntries = countryCache.getCountryEntries();
    List<CountryView> countryViews = createCountryViews(countryEntries);
    countryViews.sort(Comparator.comparing(CountryView::getCountryName));
    return countryViews;
  }

  @Override
  public SpireHealthStatus getHealthStatus() {
    return countryCache.getHealthStatus();
  }

  @Override
  public void bulkUpdateCountryData(List<CountryData> countryDataList) {
    countryDataDao.bulkUpdateCountryData(countryDataList);
  }

  @Override
  public void deleteCountryData(String countryRef) {
    countryDataDao.deleteCountryData(countryRef);
  }

  @Override
  public void deleteAllCountryData() {
    countryDataDao.deleteAllCountryData();
  }

  @Override
  public long getLastCached() {
    return countryCache.getLastCached();
  }

  private List<CountryView> createCountryViews(Collection<CountryEntry> countryEntries) {
    Map<String, CountryData> countryDataMap = countryDataDao.getAllCountryData().stream()
        .collect(Collectors.toMap(CountryData::getCountryRef, Function.identity()));
    return countryEntries.stream()
        .map(countryEntry -> {
          CountryData countryData = countryDataMap.get(countryEntry.getCountryRef());
          List<String> synonyms;
          if (countryData != null && countryData.getSynonyms() != null) {
            synonyms = countryData.getSynonyms();
            Collections.sort(synonyms);
          } else {
            synonyms = new ArrayList<>();
          }
          return new CountryView(countryEntry.getCountryRef(), countryEntry.getCountryName(), synonyms);
        }).collect(Collectors.toList());
  }

}
