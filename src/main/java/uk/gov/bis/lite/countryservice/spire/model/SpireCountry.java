package uk.gov.bis.lite.countryservice.spire.model;

import javax.xml.bind.annotation.XmlElement;

public class SpireCountry {

  @XmlElement(name = "COUNTRY_REF")
  private final String countryRef;

  @XmlElement(name = "COUNTRY_NAME")
  private final String countryName;

  @SuppressWarnings("unused")
  private SpireCountry() {
    this(null, null);
  }

  public SpireCountry(String countryRef, String countryName) {
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
