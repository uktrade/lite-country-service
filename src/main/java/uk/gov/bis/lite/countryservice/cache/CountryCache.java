package uk.gov.bis.lite.countryservice.cache;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import uk.gov.bis.lite.countryservice.model.CountryEntry;
import uk.gov.bis.lite.countryservice.service.SpireService;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Singleton
public class CountryCache {

  private volatile Map<String, List<CountryEntry>> groupCache = new HashMap<>();
  private volatile Map<String, List<CountryEntry>> setCache = new HashMap<>();
  private volatile Map<String, CountryEntry> countryCache = new HashMap<>();
  private volatile long lastCached = System.currentTimeMillis();

  private final SpireService spireService;

  @Inject
  public CountryCache(SpireService spireService) {
    this.spireService = spireService;
  }

  public void load() {
    Map<String, List<CountryEntry>> spireGroupCache = new HashMap<>();
    Map<String, List<CountryEntry>> spireSetCache = new HashMap<>();
    Map<String, CountryEntry> spireCountryCache = new HashMap<>();
    // Load country sets
    CountrySet[] countrySets = CountrySet.values();
    for (CountrySet countrySet : countrySets) {
      String countrySetName = countrySet.getName();
      List<CountryEntry> countries = spireService.loadCountriesByCountrySetId(countrySet.getSpireCountrySetId());
      countries.forEach(countryEntry -> spireCountryCache.put(countryEntry.getCountryRef(), countryEntry));
      spireSetCache.put(countrySetName, Collections.unmodifiableList(countries));
    }

    // Load country groups
    CountryGroup[] countryGroups = CountryGroup.values();
    for (CountryGroup countryGroup : countryGroups) {
      String countryGroupName = countryGroup.getName();
      List<CountryEntry> countries = spireService.loadCountriesByCountryGroupId(countryGroup.getSpireCountryGroupId());
      countries.forEach(countryEntry -> spireCountryCache.put(countryEntry.getCountryRef(), countryEntry));
      spireGroupCache.put(countryGroupName, Collections.unmodifiableList(countries));
    }

    lastCached = System.currentTimeMillis();
    groupCache = spireGroupCache;
    setCache = spireSetCache;
    countryCache = spireCountryCache;
  }

  public Optional<CountryEntry> getCountryEntry(String countryRef) {
    if (countryRef == null) {
      return Optional.empty();
    } else {
      return Optional.ofNullable(countryCache.get(countryRef));
    }
  }

  public Collection<CountryEntry> getCountryEntries() {
    return countryCache.values();
  }

  public long getLastCached() {
    return lastCached;
  }

  public Optional<List<CountryEntry>> getCountriesBySetName(String key) {
    if (key == null) {
      return Optional.empty();
    } else {
      return Optional.ofNullable(setCache.get(key));
    }
  }

  public Optional<List<CountryEntry>> getCountriesByGroupName(String key) {
    if (key == null) {
      return Optional.empty();
    } else {
      return Optional.ofNullable(groupCache.get(key));
    }
  }


}
