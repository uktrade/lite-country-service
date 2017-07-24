package uk.gov.bis.lite.countryservice.dao;

import uk.gov.bis.lite.countryservice.api.CountryData;

import java.util.List;
import java.util.Optional;

public interface CountryDataDao {

  Optional<CountryData> getCountryData(String countryRef);

  List<CountryData> getAllCountryData();

  void bulkUpdateCountryData(List<CountryData> countryDataList);

  void deleteCountryData(String countryRef);

  void deleteAllCountryData();
}
