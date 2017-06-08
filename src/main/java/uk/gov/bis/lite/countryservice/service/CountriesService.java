package uk.gov.bis.lite.countryservice.service;

import uk.gov.bis.lite.countryservice.cache.CountryListEntry;

import java.util.Optional;

public interface CountriesService {
  Optional<CountryListEntry> getCountrySet(String countrySetName);

  Optional<CountryListEntry> getCountryGroup(String groupName);
}
