package uk.gov.bis.lite.countryservice.dao;

import org.apache.commons.collections4.ListUtils;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import uk.gov.bis.lite.countryservice.api.CountryData;
import uk.gov.bis.lite.countryservice.util.JsonUtil;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

public class CountryDataDaoImpl implements CountryDataDao {

  private final DBI dbi;

  @Inject
  public CountryDataDaoImpl(DBI dbi) {
    this.dbi = dbi;
  }

  @Override
  public Optional<CountryData> getCountryData(String countryRef) {
    try (final Handle handle = dbi.open()) {
      CountryDataJDBIDao countryDataJDBIDao = handle.attach(CountryDataJDBIDao.class);
      CountryData countryData = countryDataJDBIDao.getCountryData(countryRef);
      return Optional.ofNullable(countryData);
    }
  }

  @Override
  public List<CountryData> getAllCountryData() {
    try (final Handle handle = dbi.open()) {
      CountryDataJDBIDao countryDataJDBIDao = handle.attach(CountryDataJDBIDao.class);
      return countryDataJDBIDao.getAllCountryData();
    }
  }

  @Override
  public void bulkUpdateCountryData(List<CountryData> countryDataList) {
    try (final Handle handle = dbi.open()) {
      CountryDataJDBIDao countryDataJDBIDao = handle.attach(CountryDataJDBIDao.class);
      countryDataList.forEach(countryData -> {
        List<String> synonyms = ListUtils.emptyIfNull(countryData.getSynonyms());
        // Since in the database we have defined COUNTRY_REF with the attribute UNIQUE ON CONFLICT REPLACE,
        // an insert will delete any previous data for the specified countryRef
        countryDataJDBIDao.insert(countryData.getCountryRef(), JsonUtil.convertListToJson(synonyms));
      });
    }
  }

  @Override
  public void deleteCountryData(String countryRef) {
    try (final Handle handle = dbi.open()) {
      CountryDataJDBIDao countryDataJDBIDao = handle.attach(CountryDataJDBIDao.class);
      countryDataJDBIDao.delete(countryRef);
    }
  }

  @Override
  public void deleteAllCountryData() {
    try (final Handle handle = dbi.open()) {
      CountryDataJDBIDao countryDataJDBIDao = handle.attach(CountryDataJDBIDao.class);
      countryDataJDBIDao.truncateTable();
    }
  }

}
