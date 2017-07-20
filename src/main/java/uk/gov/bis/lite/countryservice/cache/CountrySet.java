package uk.gov.bis.lite.countryservice.cache;

public enum CountrySet {

  EXPORT_CONTROL("export-control", "EXPORT_CONTROL"),
  DENIALS("denials", "DENIALS");

  private final String name;
  private final String spireCountrySetId;

  CountrySet(String name, String spireCountrySetId) {
    this.name = name;
    this.spireCountrySetId = spireCountrySetId;
  }

  public String getName() {
    return name;
  }

  public String getSpireCountrySetId() {
    return spireCountrySetId;
  }

}
