package uk.gov.bis.lite.countryservice.dao;

import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import uk.gov.bis.lite.countryservice.api.CountryData;
import uk.gov.bis.lite.countryservice.model.SynonymData;

import java.util.List;

import javax.inject.Inject;

public class SynonymDao {

  private final DBI dbi;

  @Inject
  public SynonymDao(DBI dbi) {
    this.dbi = dbi;
  }

  public String[] getSynonyms(String countryRef) {
    try (final Handle handle = dbi.open()) {
      SynonymJDBIDao synonymJDBIDao = handle.attach(SynonymJDBIDao.class);
      return synonymJDBIDao.getSynonyms(countryRef).toArray(new String[0]);
    }
  }

  public List<SynonymData> getSynonyms() {
    try (final Handle handle = dbi.open()) {
      SynonymJDBIDao synonymJDBIDao = handle.attach(SynonymJDBIDao.class);
      return synonymJDBIDao.getSynonyms();
    }
  }

  public void bulkUpdateSynonyms(List<CountryData> countryDataList) {
    try (final Handle handle = dbi.open()) {
      SynonymJDBIDao synonymJDBIDao = handle.attach(SynonymJDBIDao.class);
      handle.useTransaction((conn, status) -> countryDataList.forEach(countryData -> {
        synonymJDBIDao.delete(countryData.getCountryRef());
        for (String synonym : countryData.getSynonyms()) {
          synonymJDBIDao.insert(countryData.getCountryRef(), synonym);
        }
      }));
    }
  }

  public void deleteSynonyms(String countryRef) {
    try (final Handle handle = dbi.open()) {
      SynonymJDBIDao synonymJDBIDao = handle.attach(SynonymJDBIDao.class);
      synonymJDBIDao.delete(countryRef);
    }
  }

  public void deleteAllSynonyms() {
    try (final Handle handle = dbi.open()) {
      SynonymJDBIDao synonymJDBIDao = handle.attach(SynonymJDBIDao.class);
      synonymJDBIDao.truncateTable();
    }
  }

}
