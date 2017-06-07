package uk.gov.bis.lite.countryservice.service;

import uk.gov.bis.lite.countryservice.cache.CountryListEntry;

public interface CountriesService {
  CountryListEntry getCountrySet(String countrySetName);

  CountryListEntry getCountryGroup(String groupName);
}
