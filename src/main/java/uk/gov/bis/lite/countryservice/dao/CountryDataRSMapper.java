package uk.gov.bis.lite.countryservice.dao;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;
import uk.gov.bis.lite.countryservice.api.CountryData;
import uk.gov.bis.lite.countryservice.util.JsonUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class CountryDataRSMapper implements ResultSetMapper<CountryData> {

  @Override
  public CountryData map(int index, ResultSet r, StatementContext ctx) throws SQLException {
    String synonymsJson = r.getString("synonyms");
    List<String> synonyms = JsonUtil.convertJsonToList(synonymsJson);
    String countryRef = r.getString("country_ref");
    return new CountryData(countryRef, synonyms);
  }
}
