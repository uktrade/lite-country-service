package uk.gov.bis.lite.countryservice.cache;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import uk.gov.bis.lite.countryservice.model.CountryEntry;
import uk.gov.bis.lite.countryservice.service.SpireService;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Singleton
public class CountryListCache {

  private static final String COUNTRY_GROUP_CACHE_KEY = "countryGroup";
  private static final String COUNTRY_SET_CACHE_KEY = "countrySet";

  private final ConcurrentMap<String, CountryListEntry> cache = new ConcurrentHashMap<>();
  private final ConcurrentMap<String, CountryEntry> countryCache = new ConcurrentHashMap<>();

  private final SpireService spireService;

  @Inject
  public CountryListCache(SpireService spireService) {
    this.spireService = spireService;
  }

  public void load() {
    // Load country sets
    CountrySet[] countrySets = CountrySet.values();
    for (CountrySet countrySet : countrySets) {
      String countrySetName = countrySet.getName();
      List<CountryEntry> countries = spireService.loadCountriesByCountrySetId(countrySet.getSpireCountrySetId());
      countries.forEach(countryEntry -> countryCache.put(countryEntry.getCountryRef(), countryEntry));
      cache.put(getCountrySetCacheKey(countrySetName), new CountryListEntry(countries));
    }

    // Load country groups
    CountryGroup[] countryGroups = CountryGroup.values();
    for (CountryGroup countryGroup : countryGroups) {
      String countryGroupName = countryGroup.getName();
      List<CountryEntry> countries = spireService.loadCountriesByCountryGroupId(countryGroup.getSpireCountryGroupId());
      countries.forEach(countryEntry -> countryCache.put(countryEntry.getCountryRef(), countryEntry));
      cache.put(getCountryGroupCacheKey(countryGroupName), new CountryListEntry(countries));
    }
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

  public Optional<CountryListEntry> getCountriesBySetName(String key) {
    String cacheKey = getCountrySetCacheKey(key);
    return getCountriesFromCache(cacheKey);
  }

  public Optional<CountryListEntry> getCountriesByGroupName(String key) {
    String cacheKey = getCountryGroupCacheKey(key);
    return getCountriesFromCache(cacheKey);
  }

  private String getCountryGroupCacheKey(String key) {
    return COUNTRY_GROUP_CACHE_KEY + "." + key;
  }

  private String getCountrySetCacheKey(String key) {
    return COUNTRY_SET_CACHE_KEY + "." + key;
  }

  private Optional<CountryListEntry> getCountriesFromCache(String key) {
    if (key == null) {
      return Optional.empty();
    } else {
      return Optional.ofNullable(cache.get(key));
    }
  }


}
