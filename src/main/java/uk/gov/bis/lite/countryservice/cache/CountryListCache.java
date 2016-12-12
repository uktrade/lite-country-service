package uk.gov.bis.lite.countryservice.cache;

import com.google.common.annotations.VisibleForTesting;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import uk.gov.bis.lite.common.spire.client.SpireRequest;
import uk.gov.bis.lite.countryservice.api.CountryView;
import uk.gov.bis.lite.countryservice.exception.CacheLoadingException;
import uk.gov.bis.lite.countryservice.exception.CountryServiceException;
import uk.gov.bis.lite.countryservice.spire.model.SpireCountry;
import uk.gov.bis.lite.countryservice.spire.SpireCountriesClient;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

@Singleton
public class CountryListCache {

  private static final String COUNTRY_GROUP_CACHE_KEY = "countryGroup";
  private static final String COUNTRY_SET_CACHE_KEY = "countrySet";

  private final ConcurrentMap<String, CountryListEntry> cache = new ConcurrentHashMap<>();

  private final SpireCountriesClient spireCountriesClient;

  @Inject
  public CountryListCache(SpireCountriesClient spireCountriesClient) throws CountryServiceException {
    this.spireCountriesClient = spireCountriesClient;
  }

  public void load() throws CacheLoadingException {
    // Load country sets
    CountrySet[] countrySets = CountrySet.values();
    for (CountrySet countrySet : countrySets) {
      String countrySetName = countrySet.getName();
      List<CountryView> countries = loadCountriesByCountrySetName(countrySetName);
      if (countries != null) {
        cache.put(getCountrySetCacheKey(countrySetName), new CountryListEntry(countries));
      }
    }

    // Load country groups
    CountryGroup[] countryGroups = CountryGroup.values();
    for (CountryGroup countryGroup : countryGroups) {
      String countryGroupName = countryGroup.getName();
      List<CountryView> countries = loadCountriesByCountryGroupName(countryGroupName);
      if (countries != null) {
        cache.put(getCountryGroupCacheKey(countryGroupName), new CountryListEntry(countries));
      }
    }
  }

  public Optional<CountryListEntry> getCountriesBySetName(String key) {
    String cacheKey = getCountrySetCacheKey(key);
    return getCountriesFromCache(cacheKey);
  }

  public Optional<CountryListEntry> getCountriesByGroupName(String key) {
    String cacheKey = getCountryGroupCacheKey(key);
    return getCountriesFromCache(cacheKey);
  }

  private Optional<CountryListEntry> getCountriesFromCache(String key) {
    if (!cache.containsKey(key)) {
      return Optional.empty();

    }
    return Optional.of(cache.get(key));
  }

  private List<CountryView> loadCountriesByCountrySetName(String countrySetName) throws CacheLoadingException {

    Optional<CountrySet> countrySet = CountrySet.getByName(countrySetName);
    if (!countrySet.isPresent()) {
      throw new CacheLoadingException("Invalid country set name - " + countrySetName);
    } else {
      SpireRequest request = spireCountriesClient.createRequest();
      request.addChild("countrySetId", countrySet.get().getSpireCountrySetId());
      return getCountriesFromSpire(request);
    }

  }

  private List<CountryView> loadCountriesByCountryGroupName(String countryGroupName) throws CacheLoadingException {

    Optional<CountryGroup> countryGroup = CountryGroup.getByName(countryGroupName);
    if (!countryGroup.isPresent()) {
      throw new CacheLoadingException("Invalid country group name - " + countryGroupName);
    } else {
      SpireRequest request = spireCountriesClient.createRequest();
      request.addChild("countryGroupId", countryGroup.get().getSpireCountryGroupId());
      return getCountriesFromSpire(request);
    }

  }

  private List<CountryView> getCountriesFromSpire(SpireRequest request) {
    List<CountryView> countries = spireCountriesClient.sendRequest(request).stream().map(this::getCountryView).collect(Collectors.toList());
    if (!countries.isEmpty()) {
      countries.sort((a, b) -> a.getCountryName().compareTo(b.getCountryName()));
    }
    return countries;
  }

  private String getCountryGroupCacheKey(String key) {
    return COUNTRY_GROUP_CACHE_KEY + "." + key;
  }

  private String getCountrySetCacheKey(String key) {
    return COUNTRY_SET_CACHE_KEY + "." + key;
  }

  private CountryView getCountryView(SpireCountry spireCountry) {
    return new CountryView(spireCountry.getCountryRef(), spireCountry.getCountryName());
  }

  @VisibleForTesting
  ConcurrentMap<String, CountryListEntry> getCache() {
    return cache;
  }
}
