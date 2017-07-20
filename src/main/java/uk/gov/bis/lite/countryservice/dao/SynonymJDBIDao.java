package uk.gov.bis.lite.countryservice.dao;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;
import uk.gov.bis.lite.countryservice.model.SynonymEntry;

import java.util.List;

public interface SynonymJDBIDao {

  @Mapper(CountrySynonymEntryRSMapper.class)
  @SqlQuery("SELECT * FROM SYNONYMS")
  List<SynonymEntry> getSynonyms();

  @SqlQuery("SELECT SYNONYM FROM SYNONYMS WHERE COUNTRY_REF = :countryRef")
  List<String> getSynonyms(@Bind("countryRef") String countryRef);

  @SqlUpdate("INSERT INTO SYNONYMS (COUNTRY_REF, SYNONYM) VALUES (:countryRef, :synonym)")
  void insert(@Bind("countryRef") String countryRef, @Bind("synonym") String synonym);

  @SqlUpdate("DELETE FROM SYNONYMS WHERE COUNTRY_REF = :countryRef")
  void delete(@Bind("countryRef") String countryRef);

  @SqlUpdate("DELETE FROM SYNONYMS")
  void truncateTable();

}
