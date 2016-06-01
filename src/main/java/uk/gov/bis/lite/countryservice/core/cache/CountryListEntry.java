package uk.gov.bis.lite.countryservice.core.cache;

import uk.gov.bis.lite.countryservice.model.Country;

import java.util.Collections;
import java.util.List;

public class CountryListEntry {

  private final List<Country> countryList;
  private final long timeStamp;

  public CountryListEntry(List<Country> countryList) {
    this.countryList = countryList;
    this.timeStamp = System.currentTimeMillis();
  }

  public List<Country> getList() {
    return Collections.unmodifiableList(countryList);
  }

  public long getTimeStamp() {
    return timeStamp;
  }
}
