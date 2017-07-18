package uk.gov.bis.lite.countryservice.cache;

public enum CountryGroup {

  EU("eu", "EU");

  private final String name;
  private final String spireCountryGroupId;

  CountryGroup(String name, String spireCountryGroupId) {
    this.name = name;
    this.spireCountryGroupId = spireCountryGroupId;
  }

  public String getName() {
    return name;
  }

  public String getSpireCountryGroupId() {
    return spireCountryGroupId;
  }
  
}
