package uk.gov.bis.lite.countryservice.cache;

import uk.gov.bis.lite.countryservice.api.CountryView;
import uk.gov.bis.lite.countryservice.spire.model.SpireCountry;

import java.util.Collections;
import java.util.List;

public class CountryListEntry {

  private final List<CountryView> spireCountryList;
  private final long timeStamp;

  public CountryListEntry(List<CountryView> spireCountryList) {
    this.spireCountryList = spireCountryList;
    this.timeStamp = System.currentTimeMillis();
  }

  public List<CountryView> getList() {
    return Collections.unmodifiableList(spireCountryList);
  }

  public long getTimeStamp() {
    return timeStamp;
  }
}
