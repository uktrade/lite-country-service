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

  private final ConcurrentMap<String, CountryListEntry> cache = new ConcurrentHashMap<>();

  private final SpireGetCountriesClient spireGetCountriesClient;
  private final CountryListFactory countryListFactory;

  @Inject
  public CountryListCache(SpireGetCountriesClient spireGetCountriesClient, CountryListFactory countryListFactory) throws CountryServiceException {
    this.spireGetCountriesClient = spireGetCountriesClient;
    this.countryListFactory = countryListFactory;
  }

  public void load() throws CacheLoadingException {
    CountrySet[] countrySets = CountrySet.values();
    for (CountrySet countrySet : countrySets) {
      String countrySetName = countrySet.getName();
      List<Country> countries = loadCountries(countrySetName);
      if (countries != null) {
        cache.put(countrySetName, new CountryListEntry(countries));
      }
    }
  }

  public Optional<CountryListEntry> get(String key) {
    if (!cache.containsKey(key)) {
      return Optional.empty();

    }
    return Optional.of(cache.get(key));
  }

  private List<Country> loadCountries(String countrySetName) throws CacheLoadingException {
    try {

      Optional<CountrySet> countrySet = CountrySet.getByName(countrySetName);
      if (!countrySet.isPresent()) {
        throw new CacheLoadingException("Invalid country set name - " + countrySetName);
      } else {
        SOAPMessage soapResponse = spireGetCountriesClient.executeRequest(countrySet.get().getSpireCountrySetId());
        List<Country> countryList = countryListFactory.create(soapResponse);
        countryList.sort((a, b) -> a.getCountryName().compareTo(b.getCountryName()));
        return countryList;
      }

    } catch (SOAPException | UnsupportedEncodingException e) {
      throw new CacheLoadingException("Failed to retrieve country list from SPIRE.", e);
    }

  }

}
