package uk.gov.bis.lite.countryservice.mocks;

import com.google.inject.Singleton;
import uk.gov.bis.lite.countryservice.api.CountryView;
import uk.gov.bis.lite.countryservice.cache.CountryListEntry;
import uk.gov.bis.lite.countryservice.service.CountriesService;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Singleton
public class CountriesServiceMock implements CountriesService {

  private boolean countriesExist = true;

  private final CountryListEntry countryListEntry;

  public CountriesServiceMock() {
    List<CountryView> countries = Collections.singletonList(new CountryView("CRTY0", "United Kingdom"));
    countryListEntry = new CountryListEntry(countries);
  }

  @Override
  public Optional<CountryListEntry>getCountrySet(String countrySetName) {
    if (countriesExist) {
      return Optional.of(countryListEntry);
    } else {
      return Optional.empty();
    }
  }

  @Override
  public Optional<CountryListEntry> getCountryGroup(String groupName) {
    if (countriesExist) {
      return Optional.of(countryListEntry);
    } else {
      return Optional.empty();
    }
  }

  public void setCountriesExist(boolean countriesExist) {
    this.countriesExist = countriesExist;
  }

}
