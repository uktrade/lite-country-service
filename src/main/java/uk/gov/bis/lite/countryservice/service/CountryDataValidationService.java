package uk.gov.bis.lite.countryservice.service;

import uk.gov.bis.lite.countryservice.api.CountryData;

import java.util.List;
import java.util.Set;

public interface CountryDataValidationService {

  List<String> getUnmatchedCountryRefs(List<CountryData> countryDataList);

  Set<String> getDuplicateCountryRefs(List<CountryData> countryDataList);

  List<String> getCountryRefsWithDuplicateSynonyms(List<CountryData> countryDataList);

  List<String> getCountryRefsWithBlankSynonyms(List<CountryData> countryDataList);
}
