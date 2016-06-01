package uk.gov.bis.lite.countryservice.cache;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.bis.lite.countryservice.core.service.CountryListFactory;
import uk.gov.bis.lite.countryservice.core.service.CountrySet;
import uk.gov.bis.lite.countryservice.core.service.SpireGetCountriesClient;
import uk.gov.bis.lite.countryservice.exception.CountryServiceException;
import uk.gov.bis.lite.countryservice.model.Country;

import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Singleton
public class CountryListCache {

  private static final Logger LOGGER = LoggerFactory.getLogger(CountryListCache.class);

  private final ConcurrentMap<String, CountryListEntry> cache = new ConcurrentHashMap<>();

  private final SpireGetCountriesClient spireGetCountriesClient;
  private final CountryListFactory countryListFactory;

  @Inject
  public CountryListCache(SpireGetCountriesClient spireGetCountriesClient, CountryListFactory countryListFactory) throws CountryServiceException {
    this.spireGetCountriesClient = spireGetCountriesClient;
    this.countryListFactory = countryListFactory;
  }

  public void load() throws CountryServiceException {
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
    if (cache.containsKey(key)) {
      return Optional.of(cache.get(key));
    }
    return Optional.empty();
  }

  private List<Country> loadCountries(String countrySetName) throws CountryServiceException {
    List<Country> countryList = null;
    try {

      Optional<CountrySet> countrySet = CountrySet.getByName(countrySetName);
      if (!countrySet.isPresent()) {
        LOGGER.warn("Invalid country set name - {}", countrySetName);
      } else {
        SOAPMessage soapResponse = spireGetCountriesClient.executeRequest(countrySet.get().getSpireCountrySetId());
        countryList = countryListFactory.create(soapResponse);
        countryList.sort((a, b) -> a.getCountryName().compareTo(b.getCountryName()));
      }

    } catch (SOAPException | UnsupportedEncodingException e) {
      throw new CountryServiceException("Failed to retrieve country list from SPIRE.", e);
    }
    return countryList;
  }
}
