package uk.gov.bis.lite.countryservice.cache;

import uk.gov.bis.lite.countryservice.model.CountryEntry;

import java.util.Collections;
import java.util.List;

public class CountryListEntry {

  private final List<CountryEntry> spireCountryList;
  private final long timeStamp;

  public CountryListEntry(List<CountryEntry> spireCountryList) {
    this.spireCountryList = spireCountryList;
    this.timeStamp = System.currentTimeMillis();
  }

  public List<CountryEntry> getList() {
    return Collections.unmodifiableList(spireCountryList);
  }

  public long getTimeStamp() {
    return timeStamp;
  }
}
