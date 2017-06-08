package uk.gov.bis.lite.countryservice.service;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.bis.lite.countryservice.cache.CountryListCache;
import uk.gov.bis.lite.countryservice.cache.CountryListEntry;

import java.util.Optional;

@Singleton
public class CountriesServiceImpl implements CountriesService {

  private static final Logger LOGGER = LoggerFactory.getLogger(CountriesServiceImpl.class);

  private final CountryListCache countryListCache;

  @Inject
  public CountriesServiceImpl(CountryListCache countryListCache) {
    this.countryListCache = countryListCache;
  }

  @Override
  public Optional<CountryListEntry> getCountrySet(String countrySetName) {
    Optional<CountryListEntry> cacheEntry = countryListCache.getCountriesBySetName(countrySetName);
    if (!cacheEntry.isPresent()) {
      LOGGER.error("Country set not found in cache - " + countrySetName);
    }
    return cacheEntry;
  }

  @Override
  public Optional<CountryListEntry> getCountryGroup(String groupName) {
    Optional<CountryListEntry> cacheEntry = countryListCache.getCountriesByGroupName(groupName);
    if (!cacheEntry.isPresent()) {
      LOGGER.error("Country group not found in cache - " + groupName);
    }
    return cacheEntry;
  }

}
