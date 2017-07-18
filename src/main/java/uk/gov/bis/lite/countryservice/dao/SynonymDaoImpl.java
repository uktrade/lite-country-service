package uk.gov.bis.lite.countryservice.dao;

import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import uk.gov.bis.lite.countryservice.api.CountryData;
import uk.gov.bis.lite.countryservice.model.SynonymEntry;

import java.util.List;

import javax.inject.Inject;

public class SynonymDaoImpl implements SynonymDao {

  private final DBI dbi;

  @Inject
  public SynonymDaoImpl(DBI dbi) {
    this.dbi = dbi;
  }

  @Override
  public String[] getSynonyms(String countryRef) {
    try (final Handle handle = dbi.open()) {
      SynonymJDBIDao synonymJDBIDao = handle.attach(SynonymJDBIDao.class);
      return synonymJDBIDao.getSynonyms(countryRef).toArray(new String[0]);
    }
  }

  @Override
  public List<SynonymEntry> getSynonyms() {
    try (final Handle handle = dbi.open()) {
      SynonymJDBIDao synonymJDBIDao = handle.attach(SynonymJDBIDao.class);
      return synonymJDBIDao.getSynonyms();
    }
  }

  @Override
  public void bulkUpdateSynonyms(List<CountryData> countryDataList) {
    try (final Handle handle = dbi.open()) {
      SynonymJDBIDao synonymJDBIDao = handle.attach(SynonymJDBIDao.class);
      handle.useTransaction((conn, status) -> countryDataList.forEach(countryData -> {
        synonymJDBIDao.delete(countryData.getCountryRef());
        if (countryData.getSynonyms() != null) {
          for (String synonym : countryData.getSynonyms()) {
            synonymJDBIDao.insert(countryData.getCountryRef(), synonym);
          }
        }
      }));
    }
  }

  @Override
  public void deleteSynonyms(String countryRef) {
    try (final Handle handle = dbi.open()) {
      SynonymJDBIDao synonymJDBIDao = handle.attach(SynonymJDBIDao.class);
      synonymJDBIDao.delete(countryRef);
    }
  }

  @Override
  public void deleteAllSynonyms() {
    try (final Handle handle = dbi.open()) {
      SynonymJDBIDao synonymJDBIDao = handle.attach(SynonymJDBIDao.class);
      synonymJDBIDao.truncateTable();
    }
  }

}
