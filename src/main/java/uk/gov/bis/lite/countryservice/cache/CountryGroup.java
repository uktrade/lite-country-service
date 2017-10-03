package uk.gov.bis.lite.countryservice.cache;

public enum CountryGroup {

  // SIEL_DEST_EXCL -  Destination Exclude Countries
  // SIEL_UEU_EXCL - Exclude Ultimate End User Countries

  EU("eu", "EU"),
  SIEL_DEST_EXCL("siel_dest_excl", "SIEL_DEST_EXCL"),
  SIEL_UEU_EXCL("siel_ueu_excl", "SIEL_UEU_EXCL");

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
