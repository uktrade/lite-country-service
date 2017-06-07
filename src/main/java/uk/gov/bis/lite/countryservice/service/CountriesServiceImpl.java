package uk.gov.bis.lite.countryservice.service;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import uk.gov.bis.lite.countryservice.cache.CountryListCache;
import uk.gov.bis.lite.countryservice.cache.CountryListEntry;
import uk.gov.bis.lite.countryservice.exception.CountriesNotFoundException;

import java.util.Optional;

@Singleton
public class CountriesServiceImpl implements CountriesService {

  private final CountryListCache countryListCache;

  @Inject
  public CountriesServiceImpl(CountryListCache countryListCache) {
    this.countryListCache = countryListCache;
  }

  @Override
  public CountryListEntry getCountrySet(String countrySetName) {

    Optional<CountryListEntry> cacheEntry = countryListCache.getCountriesBySetName(countrySetName);
    if (!cacheEntry.isPresent()) {
      throw new CountriesNotFoundException("The following country set does not exist in the cache - " + countrySetName);
    }
    return cacheEntry.get();

  }

  @Override
  public CountryListEntry getCountryGroup(String groupName) {
    Optional<CountryListEntry> cacheEntry = countryListCache.getCountriesByGroupName(groupName);
    if (!cacheEntry.isPresent()) {
      throw new CountriesNotFoundException("The following country group does not exist in the cache - " + groupName);
    }
    return cacheEntry.get();
  }

}
