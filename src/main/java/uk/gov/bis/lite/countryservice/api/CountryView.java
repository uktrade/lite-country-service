package uk.gov.bis.lite.countryservice.api;

public class CountryView {

  private String countryRef;

  private String countryName;

  public CountryView(String countryRef, String countryName) {
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
