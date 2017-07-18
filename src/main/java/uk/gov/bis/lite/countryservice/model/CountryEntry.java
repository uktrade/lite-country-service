package uk.gov.bis.lite.countryservice.model;

public class CountryEntry {

  private final String countryRef;
  private final String countryName;

  public CountryEntry(String countryRef, String countryName) {
    this.countryRef = countryRef;
    this.countryName = countryName;
  }

  public String getCountryRef() {
    return countryRef;
  }

  public String getCountryName() {
    return countryName;
  }
}
