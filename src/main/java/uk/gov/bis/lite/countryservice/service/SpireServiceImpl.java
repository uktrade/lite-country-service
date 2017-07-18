package uk.gov.bis.lite.countryservice.service;

import uk.gov.bis.lite.common.spire.client.SpireRequest;
import uk.gov.bis.lite.countryservice.model.CountryEntry;
import uk.gov.bis.lite.countryservice.spire.SpireCountriesClient;
import uk.gov.bis.lite.countryservice.spire.model.SpireCountry;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

public class SpireServiceImpl implements SpireService {

  private final SpireCountriesClient spireCountriesClient;

  @Inject
  public SpireServiceImpl(SpireCountriesClient spireCountriesClient) {
    this.spireCountriesClient = spireCountriesClient;
  }

  @Override
  public List<CountryEntry> loadCountriesByCountrySetId(String countrySetId) {
    SpireRequest request = spireCountriesClient.createRequest();
    request.addChild("countrySetId", countrySetId);
    return getCountriesFromSpire(request);
  }

  @Override
  public List<CountryEntry> loadCountriesByCountryGroupId(String countryGroupId) {
    SpireRequest request = spireCountriesClient.createRequest();
    request.addChild("countryGroupId", countryGroupId);
    return getCountriesFromSpire(request);
  }

  private List<CountryEntry> getCountriesFromSpire(SpireRequest request) {
    List<CountryEntry> countries = spireCountriesClient.sendRequest(request).stream()
        .map(this::getCountryEntry)
        .collect(Collectors.toList());
    countries.sort(Comparator.comparing(CountryEntry::getCountryName));
    return countries;
  }


  private CountryEntry getCountryEntry(SpireCountry spireCountry) {
    return new CountryEntry(spireCountry.getCountryRef(), spireCountry.getCountryName());
  }

}
