package uk.gov.bis.lite.countryservice.dao;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;
import uk.gov.bis.lite.countryservice.api.CountryData;

import java.util.List;

public interface CountryDataJDBIDao {

  @Mapper(CountryDataRSMapper.class)
  @SqlQuery("SELECT * FROM COUNTRY_DATA")
  List<CountryData> getAllCountryData();

  @Mapper(CountryDataRSMapper.class)
  @SqlQuery("SELECT * FROM COUNTRY_DATA WHERE COUNTRY_REF = :countryRef")
  CountryData getCountryData(@Bind("countryRef") String countryRef);

  @SqlUpdate("INSERT INTO COUNTRY_DATA (COUNTRY_REF, SYNONYMS) VALUES (:countryRef, :synonyms)")
  void insert(@Bind("countryRef") String countryRef, @Bind("synonyms") String synonyms);

  @SqlUpdate("DELETE FROM COUNTRY_DATA WHERE COUNTRY_REF = :countryRef")
  void delete(@Bind("countryRef") String countryRef);

  @SqlUpdate("DELETE FROM COUNTRY_DATA")
  void truncateTable();

}
