package uk.gov.bis.lite.countryservice.core.cache;

import uk.gov.bis.lite.countryservice.api.Country;

import java.util.Collections;
import java.util.List;

public class CountryListCacheEntry {

    private final List<Country> countryList;
    private final long timeStamp;

    public CountryListCacheEntry(List<Country> countryList) {
        this.countryList = countryList;
        this.timeStamp = System.currentTimeMillis();
    }

    public List<Country> getCountryList() {
        return Collections.unmodifiableList(countryList);
    }

    public long getTimeStamp() {
        return timeStamp;
    }
}
