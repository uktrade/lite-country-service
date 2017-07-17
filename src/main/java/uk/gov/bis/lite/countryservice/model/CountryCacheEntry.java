package uk.gov.bis.lite.countryservice.model;

public class CountryCacheEntry {

  private final String countryRef;
  private final String countryName;

  public CountryCacheEntry(String countryRef, String countryName) {
    this.countryRef = countryRef;
    this.countryName = countryName;
  }
}
