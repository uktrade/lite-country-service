package uk.gov.bis.lite.countryservice.service;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import uk.gov.bis.lite.countryservice.cache.CountryListCache;
import uk.gov.bis.lite.countryservice.cache.CountryListEntry;
import uk.gov.bis.lite.countryservice.exception.CountrySetNotFoundException;

import java.util.Optional;

@Singleton
public class CountriesService {

  private final CountryListCache countryListCache;

  @Inject
  public CountriesService(CountryListCache countryListCache) {
    this.countryListCache = countryListCache;
  }

  public CountryListEntry getCountryList(String countrySetName) {

    Optional<CountryListEntry> cacheEntry = countryListCache.get(countrySetName);
    if (!cacheEntry.isPresent()) {
      throw new CountrySetNotFoundException("The following country set name does not exist - " + countrySetName);
    }
    return cacheEntry.get();

  }

}
