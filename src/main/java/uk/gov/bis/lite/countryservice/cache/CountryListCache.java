package uk.gov.bis.lite.countryservice.cache;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import uk.gov.bis.lite.countryservice.exception.CacheLoadingException;
import uk.gov.bis.lite.countryservice.exception.CountryServiceException;
import uk.gov.bis.lite.countryservice.model.Country;
import uk.gov.bis.lite.countryservice.spire.SpireGetCountriesClient;

import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Singleton
public class CountryListCache {

  private static final String COUNTRY_GROUP_CACHE_KEY = "countryGroup";
  private static final String COUNTRY_SET_CACHE_KEY = "countrySet";

  private final ConcurrentMap<String, CountryListEntry> cache = new ConcurrentHashMap<>();

  private final SpireGetCountriesClient spireGetCountriesClient;
  private final CountryListFactory countryListFactory;

  @Inject
  public CountryListCache(SpireGetCountriesClient spireGetCountriesClient, CountryListFactory countryListFactory) throws CountryServiceException {
    this.spireGetCountriesClient = spireGetCountriesClient;
    this.countryListFactory = countryListFactory;
  }

  public void load() throws CacheLoadingException {

    // Load country sets
    CountrySet[] countrySets = CountrySet.values();
    for (CountrySet countrySet : countrySets) {
      String countrySetName = countrySet.getName();
      List<Country> countries = loadCountriesByCountrySetName(countrySetName);
      if (countries != null) {
        cache.put(getCountrySetCacheKey(countrySetName), new CountryListEntry(countries));
      }
    }

    // Load country groups
    CountryGroup[] countryGroups = CountryGroup.values();
    for (CountryGroup countryGroup : countryGroups) {
      String countryGroupName = countryGroup.getName();
      List<Country> countries = loadCountriesByCountryGroupName(countryGroupName);
      if (countries != null) {
        cache.put(getCountryGroupCacheKey(countryGroupName), new CountryListEntry(countries));
      }
    }
  }

  public Optional<CountryListEntry> getCountriesBySetName(String key) {
    String cacheKey = getCountrySetCacheKey(key);
    return getCountries(cacheKey);
  }

  public Optional<CountryListEntry> getCountriesByGroupName(String key) {
    String cacheKey = getCountryGroupCacheKey(key);
    return getCountries(cacheKey);
  }

  private Optional<CountryListEntry> getCountries(String key) {
    if (!cache.containsKey(key)) {
      return Optional.empty();

    }
    return Optional.of(cache.get(key));
  }

  private List<Country> loadCountriesByCountrySetName(String countrySetName) throws CacheLoadingException {
    try {
      Optional<CountrySet> countrySet = CountrySet.getByName(countrySetName);
      if (!countrySet.isPresent()) {
        throw new CacheLoadingException("Invalid country set name - " + countrySetName);
      } else {
        SOAPMessage soapResponse = spireGetCountriesClient.countriesByCountrySetId(countrySet.get().getSpireCountrySetId());
        return getCountries(soapResponse);
      }

    } catch (SOAPException | UnsupportedEncodingException e) {
      throw new CacheLoadingException("Failed to retrieve country list from SPIRE {countrySetName=" + countrySetName + "}", e);
    }

  }

  private List<Country> loadCountriesByCountryGroupName(String countryGroupName) throws CacheLoadingException {
    try {
      Optional<CountryGroup> countryGroup = CountryGroup.getByName(countryGroupName);
      if (!countryGroup.isPresent()) {
        throw new CacheLoadingException("Invalid country group name - " + countryGroupName);
      } else {
        SOAPMessage soapResponse = spireGetCountriesClient.countriesByCountryGroupId(countryGroup.get().getSpireCountryGroupId());
        return getCountries(soapResponse);
      }

    } catch (SOAPException | UnsupportedEncodingException e) {
      throw new CacheLoadingException("Failed to retrieve country list from SPIRE {countryGroupName=" + countryGroupName + "}", e);
    }
  }

  private List<Country> getCountries(SOAPMessage soapResponse) {
    List<Country> countryList = countryListFactory.create(soapResponse);
    if (!countryList.isEmpty()) {
      countryList.sort((a, b) -> a.getCountryName().compareTo(b.getCountryName()));
    }
    return countryList;
  }

  private String getCountryGroupCacheKey(String key) {
    return COUNTRY_GROUP_CACHE_KEY + "." + key;
  }

  private String getCountrySetCacheKey(String key) {
    return COUNTRY_SET_CACHE_KEY + "." + key;
  }

}
