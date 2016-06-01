package uk.gov.bis.lite.countryservice.service;

import com.google.inject.Inject;
import uk.gov.bis.lite.countryservice.cache.CountryListCache;
import uk.gov.bis.lite.countryservice.cache.CountryListEntry;

import javax.xml.bind.JAXBException;
import java.util.Optional;

public class GetCountriesService {

  private final CountryListCache countryListCache;

  @Inject
  public GetCountriesService(CountryListCache countryListCache) throws JAXBException {
    this.countryListCache = countryListCache;
  }

  public Optional<CountryListEntry> getCountryList(String countrySetName) {
    return countryListCache.get(countrySetName);
  }

}
