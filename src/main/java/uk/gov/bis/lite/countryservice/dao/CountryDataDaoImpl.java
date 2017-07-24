package uk.gov.bis.lite.countryservice.dao;

import org.apache.commons.collections4.CollectionUtils;
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
        if (CollectionUtils.isEmpty(countryData.getSynonyms())) {
          countryDataJDBIDao.delete(countryData.getCountryRef());
        } else {
          countryDataJDBIDao.insert(countryData.getCountryRef(), JsonUtil.convertListToJson(countryData.getSynonyms()));
        }
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
