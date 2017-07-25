package uk.gov.bis.lite.countryservice.service;

import uk.gov.bis.lite.countryservice.api.CountryData;
import uk.gov.bis.lite.countryservice.api.CountryView;

import java.util.List;
import java.util.Optional;

public interface CountryService {

  Optional<List<CountryView>> getCountrySet(String countrySetName);

  Optional<List<CountryView>> getCountryGroup(String groupName);

  Optional<CountryView> getCountryView(String countryRef);

  List<CountryView> getCountryViews();

  void bulkUpdateCountryData(List<CountryData> countryDataList);

  void deleteCountryData(String countryRef);

  void deleteAllCountryData();

  long getLastCached();
}
