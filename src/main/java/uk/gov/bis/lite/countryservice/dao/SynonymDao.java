package uk.gov.bis.lite.countryservice.dao;

import uk.gov.bis.lite.countryservice.api.CountryData;
import uk.gov.bis.lite.countryservice.model.SynonymEntry;

import java.util.List;

public interface SynonymDao {

  String[] getSynonyms(String countryRef);

  List<SynonymEntry> getSynonyms();

  void bulkUpdateSynonyms(List<CountryData> countryDataList);

  void deleteSynonyms(String countryRef);

  void deleteAllSynonyms();
}
