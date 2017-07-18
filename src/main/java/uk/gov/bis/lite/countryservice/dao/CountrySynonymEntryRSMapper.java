package uk.gov.bis.lite.countryservice.dao;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;
import uk.gov.bis.lite.countryservice.model.SynonymEntry;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CountrySynonymEntryRSMapper implements ResultSetMapper<SynonymEntry> {

  @Override
  public SynonymEntry map(int index, ResultSet r, StatementContext ctx) throws SQLException {
    String synonym = r.getString("synonym");
    String countryRef = r.getString("country_ref");
    return new SynonymEntry(countryRef, synonym);
  }
}
