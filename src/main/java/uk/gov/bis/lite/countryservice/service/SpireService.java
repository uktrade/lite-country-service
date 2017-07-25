package uk.gov.bis.lite.countryservice.service;

import uk.gov.bis.lite.countryservice.model.CountryEntry;

import java.util.List;

public interface SpireService {

  List<CountryEntry> loadCountriesByCountrySetId(String countrySetId);

  List<CountryEntry> loadCountriesByCountryGroupId(String countryGroupId);
}
