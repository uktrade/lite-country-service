package uk.gov.bis.lite.countryservice.model;

public class SynonymData {

  private final String countryRef;
  private final String synonym;

  public SynonymData(String countryRef, String synonym) {
    this.countryRef = countryRef;
    this.synonym = synonym;
  }

  public String getCountryRef() {
    return countryRef;
  }

  public String getSynonym() {
    return synonym;
  }
}
