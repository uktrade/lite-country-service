package uk.gov.bis.lite.countryservice.api;

public class CountryView {

  private final String countryRef;
  private final String countryName;
  private final String[] synonyms;

  public CountryView(String countryRef, String countryName, String[] synonyms) {
    this.countryRef = countryRef;
    this.countryName = countryName;
    this.synonyms = synonyms;
  }

  public String getCountryRef() {
    return countryRef;
  }

  public String getCountryName() {
    return countryName;
  }

  public String[] getSynonyms() {
    return synonyms;
  }
}
