package uk.gov.bis.lite.countryservice.mocks;

import com.google.inject.Singleton;
import uk.gov.bis.lite.countryservice.api.CountryData;
import uk.gov.bis.lite.countryservice.api.CountryView;
import uk.gov.bis.lite.countryservice.healthcheck.SpireHealthStatus;
import uk.gov.bis.lite.countryservice.service.CountryService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Singleton
public class CountryServiceMock implements CountryService {

  private boolean countriesExist = true;

  private final List<CountryView> countries;

  public CountryServiceMock() {
    countries = Collections.singletonList(new CountryView("CRTY0", "United Kingdom", new ArrayList<>()));
  }

  @Override
  public Optional<List<CountryView>> getCountrySet(String countrySetName) {
    if (countriesExist) {
      return Optional.of(countries);
    } else {
      return Optional.empty();
    }
  }

  @Override
  public Optional<List<CountryView>> getCountryGroup(String groupName) {
    if (countriesExist) {
      return Optional.of(countries);
    } else {
      return Optional.empty();
    }
  }

  @Override
  public Optional<CountryView> getCountryView(String countryRef) {
    return Optional.empty();
  }

  @Override
  public List<CountryView> getCountryViews() {
    return new ArrayList<>();
  }

  @Override
  public SpireHealthStatus getHealthStatus() {
    return SpireHealthStatus.healthy();
  }

  @Override
  public void bulkUpdateCountryData(List<CountryData> countryDataList) {

  }

  @Override
  public void deleteCountryData(String countryRef) {

  }

  @Override
  public void deleteAllCountryData() {

  }

  @Override
  public long getLastCached() {
    return 0;
  }

  public void setCountriesExist(boolean countriesExist) {
    this.countriesExist = countriesExist;
  }

}
