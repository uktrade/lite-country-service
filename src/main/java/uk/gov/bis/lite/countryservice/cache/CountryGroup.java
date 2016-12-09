package uk.gov.bis.lite.countryservice.cache;

import java.util.Arrays;
import java.util.Optional;

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

  public static Optional<CountryGroup> getByName(String name) {
    return Arrays.stream(CountryGroup.values()).filter(c -> c.getName().equals(name)).findFirst();
  }
}
