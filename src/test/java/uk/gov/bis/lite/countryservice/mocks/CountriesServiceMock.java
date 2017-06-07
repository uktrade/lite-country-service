package uk.gov.bis.lite.countryservice.mocks;

import com.google.inject.Singleton;
import uk.gov.bis.lite.countryservice.api.CountryView;
import uk.gov.bis.lite.countryservice.cache.CountryListEntry;
import uk.gov.bis.lite.countryservice.service.CountriesService;

import java.util.ArrayList;
import java.util.List;

@Singleton
public class CountriesServiceMock implements CountriesService {

  private boolean countriesExist = true;

  @Override
  public CountryListEntry getCountrySet(String countrySetName) {
    return buildCountryListEntry(countriesExist);
  }

  @Override
  public CountryListEntry getCountryGroup(String groupName) {
    return buildCountryListEntry(countriesExist);
  }

  public void setCountriesExist(boolean countriesExist) {
    this.countriesExist = countriesExist;
  }

  private CountryListEntry buildCountryListEntry(boolean countriesExist) {
    List<CountryView> countries = new ArrayList<>();
    if (countriesExist) {
      countries.add(new CountryView("CRTY0", "United Kingdom"));
    }
    return new CountryListEntry(countries);
  }
}
